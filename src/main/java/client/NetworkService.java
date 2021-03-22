package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;


public class NetworkService implements Closeable{

    private static NetworkService instance;
    private final DataInputStream is;
    private final DataOutputStream os;
    private Socket socket;
    private byte[] buffer;

    private NetworkService() {
        try {
            socket = new Socket("localhost", 1234);
            os = new DataOutputStream(socket.getOutputStream());
            is = new DataInputStream(socket.getInputStream());
            buffer = new byte[512];
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

    public void sendMessage(String msg) {
        try {
            os.write(msg.getBytes(StandardCharsets.UTF_8));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readMessage() {
        String msg = "";
        try {
            int bytesRead = is.read(buffer);
            msg = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public ObservableList<String> getDirectories(String path) {
        ObservableList<String> observableDirList = FXCollections.observableArrayList();
        String[] listt = new File(String.valueOf(path)).list();
        for (String file : listt) {
            observableDirList.add(file);
        } return observableDirList;
    }

    public void sendFile(String fileName, String from, String to) {
        try {
            File file = new File(from + File.separator + fileName);
            if(file.exists()) {
                if(!Files.exists(Path.of(to, fileName))) {
                    Files.copy(Path.of(from, fileName), Path.of(to, fileName), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File " + fileName + " is uploaded from " + from + " to " + to);
                }
            } if(!file.exists()) {
                System.out.println("File " + fileName + " is not exists in " + from);
            }
        } catch (IOException e) {
            System.out.println("Please enter the filename!");
            //e.printStackTrace();
        }
    }

    public void deleteFile(String fileName, String from) {
        File file = new File(from + File.separator + fileName);
        if(file.exists()) {
            file.delete();
            System.out.println("File " + fileName + " is deleted from " + from);
        } else System.out.println("This file is not exists");
    }

    public DataInputStream getInputStream() {
        return is;
    }

    @Override
    public void close() throws IOException {
        is.close();
        os.close();
    }
}