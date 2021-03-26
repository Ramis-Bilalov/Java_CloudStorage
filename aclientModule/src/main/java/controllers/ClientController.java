package controllers;

import database.UserSQLiteDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    public ListView<String> serverList;
    public TextField uploadFile;
    public ListView<String> clientList;
    public final ObservableList<String> observableServerList = FXCollections.observableArrayList();
    public final ObservableList<String> observableClientList = FXCollections.observableArrayList();
    public TextField dirName;
    public String path;
    public TextField searchServer;
    public TextField searchClient;
    Stage trashStage = new Stage();
    String pathUsers = "aclientModule/src/main/resources/controllers/users";
    private final String pathNew = UserSQLiteDao.getInstance().getEmail();

    public ClientController() throws SQLException, ClassNotFoundException {
    }


    public void upload(ActionEvent actionEvent) throws IOException {
        NetworkService.getInstance().sendFile(uploadFile.getText(), pathUsers + File.separator + pathNew + File.separator + "client", path);
        uploadFile.clear();
        refreshLists();
    }

    public void delete(ActionEvent actionEvent) throws IOException, InterruptedException {
        NetworkService.getInstance().sendFile(serverList.getSelectionModel().getSelectedItem(), path, pathUsers + File.separator + pathNew + File.separator + "servertrash");
        NetworkService.getInstance().deleteFile(serverList.getSelectionModel().getSelectedItem(), path);
        refreshLists();
        if(trashStage.isShowing()) {
            toTrash();
        }
    }

    public void disconnect(ActionEvent actionEvent) throws IOException {
        NetworkService.getInstance().closeConnect();
        Parent reg = FXMLLoader.load(getClass().getResource("auth.fxml"));
        Stage stage = new Stage();
        stage.setTitle("CloudStorage - Авторизация");
        stage.setScene(new Scene(reg));
        stage.setResizable(false);
        stage.show();
        dirName.getScene().getWindow().hide();
        this.trashStage.close();
    }

    public void refreshLists() {
        try {
            clientList.setItems(NetworkService.getInstance().getDirectories(pathUsers + File.separator + pathNew + File.separator + "client"));
            serverList.setItems(NetworkService.getInstance().getDirectories(path));
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    public void uploadFromClientToServer(ActionEvent actionEvent) {
        NetworkService.getInstance().sendFile((String) clientList.getSelectionModel().getSelectedItem(), pathUsers + File.separator + pathNew + File.separator + "client", path);
        refreshLists();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Files.exists(Path.of(pathUsers, pathNew, "client"))) {
            try {
                Files.createDirectories(Path.of(pathUsers, pathNew, "client"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!Files.exists(Path.of(pathUsers, pathNew, "server"))) {
            try {
                Files.createDirectories(Path.of(pathUsers, pathNew, "server"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!Files.exists(Path.of(pathUsers, pathNew, "desktop"))) {
            try {
                Files.createDirectories(Path.of(pathUsers, pathNew, "desktop"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!Files.exists(Path.of(pathUsers, pathNew, "servertrash"))) {
            try {
                Files.createDirectories(Path.of(pathUsers, pathNew, "servertrash"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            observableClientList.addAll(NetworkService.getInstance().getFiles(pathUsers + File.separator + pathNew + File.separator + "client"));
            observableServerList.addAll(NetworkService.getInstance().getDirectories(pathUsers + File.separator + pathNew + File.separator + "server"));
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        path = pathUsers + File.separator + pathNew + File.separator + "server";
        serverList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                try {
                    String dir = serverList.getSelectionModel().getSelectedItem();
                    if (event.getClickCount() == 2) {
                        if (serverList.getSelectionModel().getSelectedItem().equals(dir)) {
                            serverList.setItems(NetworkService.getInstance().getDirectories(String.valueOf(Path.of(path, dir))));
                            path = String.valueOf(Path.of(path, dir));
                        }
                    }
                } catch (NullPointerException n) {
                    System.out.println("Not a directory");
                }
            }
        });
        refreshLists();

    }

    public void uploadFromServerToDesktop(ActionEvent actionEvent) {
        NetworkService.getInstance().sendFile(serverList.getSelectionModel().getSelectedItem(), path, pathUsers + File.separator + pathNew + File.separator + "desktop");
        refreshLists();
    }

    public void returnFromDirectory(ActionEvent actionEvent) {
        try {
            clientList.setItems(NetworkService.getInstance().getDirectories(pathUsers + File.separator + pathNew + File.separator + "client"));
            serverList.setItems(NetworkService.getInstance().getDirectories(pathUsers + File.separator + pathNew + File.separator + "server"));
            path = pathUsers + File.separator + pathNew + File.separator + "server";
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }

    public void createDirectory(ActionEvent actionEvent) throws IOException {
        if(!Files.exists(Path.of(path, dirName.getText()))) {
            Files.createDirectories(Path.of(path, dirName.getText()));
            dirName.clear();
        }
        refreshLists();
    }

    public void toTrash() throws IOException {
        Parent reg = FXMLLoader.load(getClass().getResource("trash.fxml"));
        trashStage.setTitle("CloudStorage - Корзина");
        trashStage.setX(1100);
        trashStage.setY(96);
        trashStage.setScene(new Scene(reg));
        trashStage.setResizable(false);
        trashStage.show();
    }

    public void closeConnection(ActionEvent actionEvent) throws IOException {
        NetworkService.getInstance().closeConnect();
        Platform.exit();
    }

    public void registrate(ActionEvent actionEvent) throws IOException {
        Parent reg = FXMLLoader.load(getClass().getResource("registration.fxml"));
        Stage stage = new Stage();
        stage.setTitle("CloudStorage - Регистрация");
        stage.setScene(new Scene(reg));
        stage.setResizable(false);
        stage.show();
        dirName.getScene().getWindow().hide();
    }

    public void searchOnCloud(ActionEvent actionEvent) throws IOException {
        Files.walkFileTree(Path.of(pathUsers, pathNew, "server"), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(file.getFileName().toString().contains(searchServer.getText())) {
                    System.out.println(file.getFileName() + " is founded. Path: " + file.getParent());
                    serverList.setItems(NetworkService.getInstance().getSearchFiles(String.valueOf(file.getParent()), String.valueOf(file.getFileName())));
                    path = String.valueOf(file.getParent());
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void searchOnComputer(ActionEvent actionEvent) throws IOException {
        Files.walkFileTree(Path.of(pathUsers, pathNew, "client"), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if(file.getFileName().toString().contains(searchClient.getText())) {
                    System.out.println(file.getFileName() + " is founded. Path: " + file.getParent());
                    clientList.setItems(NetworkService.getInstance().getSearchFiles(String.valueOf(file.getParent()), String.valueOf(file.getFileName())));
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}