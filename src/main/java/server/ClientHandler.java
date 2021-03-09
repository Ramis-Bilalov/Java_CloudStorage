package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void commandHandler(String to, ObjectInputStream in, ObjectOutputStream out) throws IOException {
        try {
            File file = new File(to + File.separator + in.readUTF());
            if(!file.exists()) {
                file.createNewFile();
            }
            long size = in.readLong();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[256];
            for (int i = 0; i < (size+255)/256; i++) {
                int read = in.read(buffer);
                fos.write(buffer, 0, read);
            }
            fos.close();
            out.writeUTF("DONE");
        } catch (Exception e) {
            out.writeUTF("ERROR");
        }
    }

    @Override
    public void run() {
        try(ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){
            while (true) {
                String command = in.readUTF();
                if("upload".equals(command)) {
                    commandHandler("server", in, out);
                } else if("download".equals(command)) {
                    commandHandler("desktop", in, out);
                }
            }
        } catch (IOException e) {
            System.out.println("Client is disconnected!");
            System.out.println("Server is waiting for connection!\n");
        }
    }
}
