package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;

public class NetworkService {

    private static NetworkService instance;
    private final ObjectInputStream is;
    private final ObjectOutputStream os;
    private Socket socket;

    private NetworkService() {
        try {
            socket = new Socket("localhost", 1234);
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create network connection");
        }
    }

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NetworkService();
        }
        return instance;
    }

    public void closeConnect() throws IOException {
        socket.close();
    }

    public ObservableList<String> getFiles(String path) {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                observableList.add(file.getName());
            }
        } return observableList;
    }

    public void sendFile(String fileName, String from, String to, String command) {
        try {
            File file = new File(from + File.separator + fileName);
            if(file.exists()) {
                os.writeUTF(command);
                os.writeUTF(fileName);
                long length = file.length();
                os.writeLong(length);
                FileInputStream fis = new FileInputStream(file);
                int read = 0;
                byte[] buffer = new byte[256];
                while ((read = fis.read(buffer))!= -1) {
                    os.write(buffer, 0, read);
                }
                fis.close();
                System.out.println("File " + fileName + " uploaded from " + from + " to " + to);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String fileName, String from) {
        File file = new File(from + File.separator + fileName);
        if(file.delete()) {
            System.out.println("File " + fileName + " is deleted from " + from);
        } else System.out.println("This file is not exists");
    }

    public ObjectInputStream getInputStream() {
        return is;
    }
}