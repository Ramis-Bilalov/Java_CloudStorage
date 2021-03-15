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
import java.util.List;

public class NioTelnetServer {

    private final ByteBuffer buffer = ByteBuffer.allocate(512);
    public static final String LS_COMMAND = "\tls\t\tview all files from current directory\n\r";
    public static final String TOUCH_COMMAND = "\ttouch\t\tcreate new file [touch path/filename]\n\r";
    public static final String MKDIR_COMMAND = "\tmkdir\t\tcreate the directory(ies) [create directoryName]\n\r";
    public static final String CD_COMMAND = "\tcd\t\tfile walk tree [cd path]\n\r";
    public static final String RM_COMMAND = "\trm\t\tremove file [rm path/fileName]\n\r";
    public static final String COPY_COMMAND = "\tcopy\t\tcopy file to directory [copy filename to]\n\r";
    public static final String CAT_COMMAND = "\tcat\t\tread file [cat path/filename]\n\r";
    public static final String EXIT_COMMAND = "\texit\t\texit\n\r";
    private Path serverPath = Paths.get("server");

    public NioTelnetServer() throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open(); // открыли
        server.bind(new InetSocketAddress(1237));
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

        StringBuilder sbb = new StringBuilder();


        if (key.isValid()) {
            String command = sb.toString()
                    .replace("\n", "")
                    .replace("\r", "");
            if ("--help".equals(command)) {                                                     // Команда help
                sendMessage(LS_COMMAND, selector);
                sendMessage(TOUCH_COMMAND, selector);
                sendMessage(MKDIR_COMMAND, selector);
                sendMessage(CD_COMMAND, selector);
                sendMessage(RM_COMMAND, selector);
                sendMessage(COPY_COMMAND, selector);
                sendMessage(CAT_COMMAND, selector);
                sendMessage(EXIT_COMMAND, selector);
            } else if ("ls".equals(command)) {                                                  // Команда ls
                sendMessage(getFilesList().concat("\n"), selector);
            } else if ("exit".equals(command)) {                                                // Команда exit
                System.out.println("Client logged out. IP: " + channel.getRemoteAddress());
                channel.close();
                return;
            }
            else if(command.startsWith("touch")) {                                              // Команда touch
                StringBuilder fileName = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 6; i < array.length; i++) {
                    fileName.append(array[i]);
                }
                if(!Files.exists(serverPath)) {
                    Files.createDirectories(serverPath);
                }
                Path path1 = Paths.get(serverPath.toString(), fileName.toString());
                if(!Files.exists(path1)) {
                    Files.createFile(path1);
                    sendMessage("File " + fileName + " is created " + "in " + path1 + "\r\n", selector);
                } else sendMessage("File is already exists", selector);
            }
            else if(command.startsWith("mkdir")) {                                              // Команда mkdir
                StringBuilder dirName = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 6; i < array.length; i++) {
                    dirName.append(array[i]);
                }
                Path path2 = Paths.get(serverPath.toString(), dirName.toString());
                if(!Files.exists(path2)) {
                    Files.createDirectories(Path.of(serverPath.toString(), dirName.toString()));
                    sendMessage("Directory " + dirName.toString() + " is created\n\r", selector);
                }
            }
            else if(command.startsWith("cd")) {                                                 // Команда cd
                String[] array = command.split(" ");
                if(Files.exists(Path.of(array[1]))) {
                    serverPath = Path.of(String.valueOf(serverPath), array[1]);
                }
                if(!Files.exists(Path.of(array[1]))) {
                    serverPath = Files.createDirectories(Path.of(String.valueOf(serverPath), array[1]));
                }
            }
            else if(command.startsWith("rm")) {                                                 // Команда rm
                StringBuilder deleteFile = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 3; i < array.length; i++) {
                    deleteFile.append(array[i]);
                }
                if(!Files.exists(serverPath)) {
                    Files.createDirectories(serverPath);
                }
                Path path4 = Path.of(serverPath.toString(), String.valueOf(deleteFile));
                if(Files.exists(path4)) {
                    Files.delete(path4);
                    sendMessage("File " + deleteFile + " is deleted " + "in " + path4 + "\r\n", selector);
                } else sendMessage("File " + deleteFile + " is not exists " + "in " + path4 + "\r\n", selector);
            }
            else if(command.startsWith("copy")) {                                               // Команда copy
                StringBuilder copyFile = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 5; i < array.length; i++) {
                    copyFile.append(array[i]);
                }
                if(!Files.exists(serverPath)) {
                    Files.createDirectories(serverPath);
                }
                try {
                    String[] array1 = copyFile.toString().split(" ");
                    Path target = Path.of(serverPath + File.separator + array1[1]);
                    Path source = Path.of(serverPath + File.separator + array1[0]);
                    if (Files.exists(source)) {
                        if (!Files.exists(target)) {
                            Files.createDirectories(target);
                        }
                        if (Files.exists(target)) {
                            try {
                                Path path5 = Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                                sendMessage("File in path " + source + " is copied to " + target + "\n\r", selector);
                            } catch (NoSuchFileException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (!Files.exists(source)) {
                        sendMessage(source + " is not exists in server \n\r", selector);
                    }

                } catch (ArrayIndexOutOfBoundsException a) {
                    sendMessage("The command is incorrect", selector);
                }
            }
            else if(command.startsWith("cat")) {                                                // Команда cat
                StringBuilder fileName = new StringBuilder();
                char[] array = command.toCharArray();
                for (int i = 4; i < array.length; i++) {
                    fileName.append(array[i]);
                }
                if(Files.exists(Path.of(serverPath.toString(), String.valueOf(fileName)))) {
                    List<String> list = Files.readAllLines(Path.of(serverPath.toString(), String.valueOf(fileName)));
                    StringBuilder readFile = new StringBuilder();
                    for(String line : list) {
                        readFile.append(line);
                        readFile.append("\n\r");
                    }
                    sendMessage(String.valueOf(readFile), selector);
                } else sendMessage("File " + fileName + " is not exists in server", selector);
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

    private String getFilesList() throws IOException {
        String path = "server";
        if(!Files.exists(Path.of(path))) {
            Files.createDirectories(Path.of(path));
        }
        return String.join("\t", new File(path).list());
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