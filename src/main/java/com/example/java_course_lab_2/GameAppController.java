package com.example.java_course_lab_2;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class GameAppController implements IObserver {

    // Лист мишеней

    // Лист стрел

    // Панель отображения игроков
    @FXML
    Pane playersPane;
    // Панель отображения игры
    @FXML
    Pane gamePane;
    // Панель отображения кнопок
    @FXML
    Pane buttonsPane;
    // Панель отображения статистики
    @FXML
    VBox infoPane;
    // Поле авторизации
    @FXML
    Pane connectPane;

    // Поле ввода
    @FXML
    TextField connectInput;

    @FXML
    Polygon nib1, nib2, nib3, nib4;

    @FXML
    Line line1, line2, line3, line4;

    @FXML
    Circle smallCircle, bigCircle;

    @FXML
    Arc player1, player2, player3, player4;

    @FXML
    Pane playerInfo1, playerInfo2, playerInfo3, playerInfo4;

    private Polygon[] nibs;
    private Line[] lines;
    private Circle[] targets;
    private Arc[] playersFigures;

    private  Pane[] playersInfoPanes;

    // Сокет, через который будем отправлять сообщения
    Socket socketAtClient;

    // Ссылка на модель
    private Model m = BModel.build();

    int port = 3124;
    InetAddress ip = null;

    InputStream inputStream;
    OutputStream outputStream;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    Gson gson = new Gson();

    boolean isShowTable = false;

    BufferedReader in;
    PrintWriter out;

    // Кнопка подключения, обработка нажатия
    @FXML
    void connectGame() {
        System.out.println("Подключаюсь");
        String currentName;
        System.out.println(line4.getLayoutY());
//        System.out.println(bigCircle.getLayoutY());

//        Label text = (Label) ((playerInfo2).getChildren().get(3));
//        text.setText("NiceJob");

//        for (Polygon p: nibs) {
//            p.setLayoutX(p.getLayoutX() + 10);
//        }

        // Имя должно обрабатываться на сервере, поэтому делаем так:
        try {
            ip = InetAddress.getLocalHost();
            socketAtClient = new Socket(ip, port);

//            Label text = (Label) ((playerInfo2).getChildren().get(3));
//            text.setText("Client Start");
//            in = new BufferedReader(new InputStreamReader(socketAtClient.getInputStream()));
//            out = new PrintWriter(socketAtClient.getOutputStream(), true);
            outputStream = socketAtClient.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
//            out.println(connectInput.getText().trim());
            dataOutputStream.writeUTF(connectInput.getText().trim());
            inputStream = socketAtClient.getInputStream();
            dataInputStream = new DataInputStream(inputStream);

//            String response = in.readLine();
            String response = dataInputStream.readUTF();
            System.out.println(response);

            if (response.equals("Доступ получен")) {
                // Запускаем поток, который регулярно будет принимать данные с сервера
//                m.allPlayers.add(new ClientInfo(connectInput.getText().trim()));
                System.out.println(m.getAllPlayers());
                // Делаем видимыми полотна игры
                playersPane.setVisible(true);
                gamePane.setVisible(true);
                buttonsPane.setVisible(true);
                infoPane.setVisible(true);
                // Эту штуку отключаем
                connectPane.setDisable(true);
                new Thread(() -> {
                    try {
                        System.out.println("Я в потоке");
                        while (true) {
                            System.out.println("Я в самом начале цикла");
                            String serverMessage = dataInputStream.readUTF();
                            System.out.println("Я в после приёма данных");
                            // Логгируем полученные данные
                            System.out.println("Ответ с сервера: " + serverMessage);
                            MessageFromServer messageFromServer = gson.fromJson(serverMessage, MessageFromServer.class);
                            m.setAllPlayers(messageFromServer.clientsInfoArrayList);
                            m.setAllTargets(messageFromServer.targetsInfoArrayList);
                            m.setWinner(messageFromServer.winnerName);
                            m.setNibsAllArrows(messageFromServer.nibArrowInfoArrayList);
                            m.setSticksAllArrows(messageFromServer.stickArrowInfoArrayList);
                            m.setLeaders(messageFromServer.leadersArrayList);

                            // Обноввляем модель
                            m.update();
                        }
                    } catch (IOException ex) {
                        System.out.println("Произошла ошибка");
                    }
                }).start();
            }
        } catch (IOException ex) {}
    }





    // Добавляем в качестве наблюдателя себя
    public void initialize() {
        m.addObserver(this);
        System.out.println("hello");

        playersPane.setVisible(false);
        gamePane.setVisible(false);
        buttonsPane.setVisible(false);
        infoPane.setVisible(false);

        nibs = new Polygon[]{nib1, nib2, nib3, nib4};
        playersInfoPanes = new Pane[]{playerInfo1, playerInfo2, playerInfo3, playerInfo4};
        playersFigures = new Arc[]{player1, player2, player3, player4};
        lines = new Line[]{line1, line2, line3, line4};
    }

    @FXML
    public void onReady() {
        try {
            dataOutputStream.writeUTF(gson.toJson(new MessageFromClient(ClientActions.READY)));
        } catch (IOException ignored) { }
    }

    @FXML
    public void onPause() {
        try {
            dataOutputStream.writeUTF(gson.toJson(new MessageFromClient(ClientActions.PAUSE)));
        } catch (IOException ignored) { }
    }

    @FXML
    public void onShoot() {
        try {
            dataOutputStream.writeUTF(gson.toJson(new MessageFromClient(ClientActions.SHOOT)));
        } catch (IOException ignored) { }
    }

    @FXML
    public void onLeaders() {
        try {
            dataOutputStream.writeUTF((gson.toJson(new MessageFromClient(ClientActions.LEADERS))));
//            isShowTable = true;
            showLeadersTable();

        } catch (IOException ignored) {}
    }

    public void checkWinner() {
        if (m.getWinner() != null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Победитель!");
                alert.setHeaderText("Победитель определён!");
                alert.setContentText("Победил игрок с ником: " + m.getWinner() + "!");
                alert.showAndWait();
            });
        }
    }

    public void updateTargets(ArrayList<Target> targets) {
        // Сначала маленькая, потом большая
        if (targets == null || targets.size() == 0) {
            return;
        }

        Platform.runLater(() -> {
            smallCircle.setLayoutY(targets.get(0).getY());
            bigCircle.setLayoutY(targets.get(1).getY());
        });
    }

    public void updatePlayersInfo(ArrayList<ClientInfo> clientInfos) {
        if (clientInfos == null || clientInfos.size() == 0) {
            return;
        }

        Platform.runLater(() -> {
            for (int i = 0; i < playersInfoPanes.length; i++) {
                if (i < clientInfos.size()) {
                    playersInfoPanes[i].setVisible(true);
                    Label tempLabel;
                    // Устанавливаем имя игрока
                    tempLabel = (Label) (playersInfoPanes[i]).getChildren().get(1);
                    tempLabel.setText(clientInfos.get(i).getPlayerName());
                    // Устанавливаем счёт игрока
                    tempLabel = (Label) (playersInfoPanes[i]).getChildren().get(3);
                    tempLabel.setText(Integer.toString(clientInfos.get(i).getNumberScores()));
                    // Устанавливаем количество выстрелов игрока
                    tempLabel = (Label) (playersInfoPanes[i]).getChildren().get(5);
                    tempLabel.setText(Integer.toString(clientInfos.get(i).getNumberShots()));
                    // Устанавливаем количество побед игрока
                    tempLabel = (Label) (playersInfoPanes[i]).getChildren().get(7);
                    tempLabel.setText(Integer.toString(clientInfos.get(i).getNumberWins()));
                } else {
                    playersInfoPanes[i].setVisible(false);
                }
            }
        });
    }

    public void updatePlayers(ArrayList<ClientInfo> clients) {
        if (clients == null || clients.size() == 0) {
            return;
        }

        Platform.runLater(() -> {
            // Максимум 4 игрока по ним проходимся, и то, число, что у нас есть игроков, а других скрываем
            for (int i = 0; i < playersFigures.length; i++) {
                if (i < clients.size()) {
                    playersFigures[i].setVisible(true);
                    if (clients.get(i).getPlayerName().equals(connectInput.getText().trim())) {
                        playersFigures[i].setFill(new Color(1, 0, 0,1));
                    } else {
                        playersFigures[i].setFill(new Color((30 / 255), (144 / 255), 1,1));
                    }
                } else {
                    playersFigures[i].setVisible(false);
                }
            }
        });
    }

    public void updateArrows(ArrayList<MyPoint> arrowsNibs, ArrayList<MyPoint> arrowsSticks) {
        // Сначала маленькая, потом большая
        if (arrowsNibs == null || arrowsNibs.size() == 0 || arrowsSticks == null || arrowsSticks.size() == 0) {
            return;
        }

        Platform.runLater(() -> {
            for (int i = 0; i < lines.length; i++) {
                if (i < arrowsNibs.size()) {
                    nibs[i].setVisible(true);
                    lines[i].setVisible(true);

                    // Устанавливаем нужные координаты
                    nibs[i].setLayoutX(arrowsNibs.get(i).getX());
                    lines[i].setLayoutX(arrowsSticks.get(i).getX());
                } else {
                    nibs[i].setVisible(false);
                    lines[i].setVisible(false);
                }
            }
        });
    }

    private void showLeadersTable() {
        TableView tableView = new TableView();

        ArrayList<TableEntity> tableInfo = new ArrayList<>();

        TableColumn<TableEntity, String> column1 =
                new TableColumn<>("Имя игрока");

        column1.setCellValueFactory(
                new PropertyValueFactory<>("playerName"));


        TableColumn<TableEntity, String> column2 =
                new TableColumn<>("Количество побед");

        column2.setCellValueFactory(
                new PropertyValueFactory<>("numberWins"));

        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);

        for (int i = 0; i < m.getLeaders().size(); i++) {
            tableInfo.add(new TableEntity(m.getLeaders().get(i).getPlayerName(), m.getLeaders().get(i).getNumberWins()));
        }

//        System.out.println("Common");
//        for (int i = 0; i < m.getLeaders().size(); i++) {
//            System.out.println(m.getLeaders().get(i));
//        }
//        System.out.println("Common = " + m.getLeaders().size());
//
//        System.out.println("Common guys");
//        for (int i = 0; i < tableInfo.size(); i++) {
//            System.out.println(tableInfo.get(i));
//        }
//        System.out.println("Common guys");

        Collections.sort(tableInfo);

        tableInfo.forEach(tableView.getItems()::add);
        Platform.runLater(() -> {
            VBox vbox = new VBox(tableView);
            Scene scene = new Scene(vbox);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        });
    }

    @Override
    public void update() {
        // Перерисовывать расположение мишеней и стрел
        checkWinner();
        updateTargets(m.getAllTargets());
        updatePlayersInfo(m.getAllPlayers());
        updatePlayers(m.getAllPlayers());
        updateArrows(m.getNibsAllArrows(), m.getSticksAllArrows());
        if (isShowTable) {
            showLeadersTable();
            isShowTable = false;
        }
        System.out.println("Common = " + m.getLeaders().size());
    }
}