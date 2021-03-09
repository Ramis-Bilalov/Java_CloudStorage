package nio;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

public class NioTelnetServer {
    private final ByteBuffer buffer = ByteBuffer.allocate(512);

    public static final String LS_COMMAND = "\tls\t\tview all files from current directory\n\r";
    public static final String TOUCH_COMMAND = "\ttouch\t\tcreate new file [touch path/filename]\n\r";
    public static final String MKDIR_COMMAND = "\tmkdir\t\tcreate the directory(ies) [create directoryName]\n\r";
    public static final String CD_COMMAND = "\tcd\t\tfile walk tree [cd path]\n\r";
    public static final String RM_COMMAND = "\trm\t\tremove file [rm path/fileName]\n\r";
    public static final String COPY_COMMAND = "\tcopy\t\tcopy file to directory [copy from-->to]\n\r";
    public static final String CAT_COMMAND = "\tcat\t\tread file [cat path/filename]\n\r";

    public NioTelnetServer() throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open(); // открыли
        server.bind(new InetSocketAddress(1235));
        server.configureBlocking(false); // ВАЖНО
        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started");
        while (server.isOpen()) {
            selector.select();
            var selectionKeys = selector.selectedKeys();
            var iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                var key = iterator.next();
                if (key.isAcceptable()) {
                    handleAccept(key, selector);
                } else if (key.isReadable()) {
                    handleRead(key, selector);
                }
                iterator.remove();
            }
        }
    }

    private void handleRead(SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        int readBytes = channel.read(buffer);
        if (readBytes < 0) {
            channel.close();
            return;
        } else if (readBytes == 0) {
            return;
        }

        buffer.flip();
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            sb.append((char) buffer.get());
        }
        buffer.clear();

        // touch (имя файла) - создание файла +
        // mkdir (имя директории) - создание директории +
        // cd (path) - перемещение по дереву папок +
        // rm (имя файла или папки) - удаление объекта +
        // copy (src, target) - копирование файла
        // cat (имя файла) - вывод в консоль содержимого



        if (key.isValid()) {
            String command = sb.toString()
                    .replace("\n", "")
                    .replace("\r", "");
            if ("--help".equals(command)) {
                sendMessage(LS_COMMAND, selector);
                sendMessage(TOUCH_COMMAND, selector);
                sendMessage(MKDIR_COMMAND, selector);
                sendMessage(CD_COMMAND, selector);
                sendMessage(RM_COMMAND, selector);
                sendMessage(COPY_COMMAND, selector);
                sendMessage(CAT_COMMAND, selector);
            } else if ("ls".equals(command)) {
                sendMessage(getFilesList().concat("\n"), selector);
            } else if ("exit".equals(command)) {
                System.out.println("Client logged out. IP: " + channel.getRemoteAddress());
                channel.close();
                return;
            }
            else if(command.startsWith("touch")) {
                StringBuilder fileName = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 6; i < array.length; i++) {
                    fileName.append(array[i]);
                }
                Path path = Paths.get("client", fileName.toString());
                if(!Files.exists(path)) {
                    Files.createFile(path);
                    sendMessage("File in path " + fileName + " is created\n\r", selector);
                }
            }
            else if(command.startsWith("mkdir")) {
                StringBuilder dirName = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 6; i < array.length; i++) {
                    dirName.append(array[i]);
                }
                Path path = Paths.get("client", dirName.toString());
                if(!Files.exists(path)) {
                    Files.createDirectories(Path.of("client", dirName.toString()));
                    sendMessage("Directory " + dirName + " is created\n\r", selector);
                }
            }
            else if(command.startsWith("cd")) {
                StringBuilder searchFile = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 3; i < array.length; i++) {
                    searchFile.append(array[i]);
                }
                Path path = Paths.get("client");
                Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        sendMessage(dir.getFileName().toString() + File.separator, selector);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            else if(command.startsWith("rm")) {
                StringBuilder deleteFile = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 3; i < array.length; i++) {
                    deleteFile.append(array[i]);
                }
                System.out.println(deleteFile);
                Path path = Path.of("client", String.valueOf(deleteFile));
                if(Files.exists(path)) {
                    Files.delete(path);
                    sendMessage("File in path " + deleteFile + " is deleted\n\r", selector);
                }
            }
            else if(command.startsWith("copy")) {
                StringBuilder copyFile = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 5; i < array.length; i++) {
                    copyFile.append(array[i]);
                }
                String[] array1 = copyFile.toString().split("-->");
                Path source = Path.of("client" + File.separator + array1[0]);
                Path target = Path.of("client" + File.separator + array1[1]);
                if(Files.exists(source)) {
                    Path path = Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    sendMessage("File in path " + source + " is copied to " + target + "\n\r", selector);
                }
                else if(!Files.exists(source)) {
                    Files.createFile(source);
                    Path path = Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    sendMessage("File from " + source + " is copied to " + target + "\n\r", selector);
                }
            }
            else if(command.startsWith("cat")) {
                StringBuilder fileName = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 4; i < array.length; i++) {
                    fileName.append(array[i]);
                }
                if(Files.exists(Path.of("client", String.valueOf(fileName)))) {
                    List<String> list = Files.newBufferedReader(Path.of("client", String.valueOf(fileName)))
                            .lines().collect(Collectors.toList());
                    sendMessage(list.toString(), selector);
                }
            }
        }
        sendMessage("\n\r", selector);
        sendName(channel);
    }

    private void sendName(SocketChannel channel) throws IOException {
        channel.write(
                ByteBuffer.wrap(channel
                        .getRemoteAddress().toString()
                        .concat(">: ")
                        .getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    private String getFilesList() {
        return String.join("\t", new File("server").list());
    }

    private void sendMessage(String message, Selector selector) throws IOException {
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                ((SocketChannel) key.channel())
                        .write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
            }
        }
    }

    private void handleAccept(SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        System.out.println("Client accepted. IP: " + channel.getRemoteAddress());
        channel.register(selector, SelectionKey.OP_READ, "some attach");
        channel.write(ByteBuffer.wrap("Hello user! \n\r".getBytes(StandardCharsets.UTF_8)));
        channel.write(ByteBuffer.wrap("Enter --help for support info \n\r".getBytes(StandardCharsets.UTF_8)));
    }

    public static void main(String[] args) throws IOException {
        new NioTelnetServer();
    }
}