package com.example.java_course_lab_2;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Реализованно основное взаимодействие с клиентом
public class clientAtServer implements Runnable {
    Socket socket;
    MainServer mainServer;

    InputStream inputStream;
    OutputStream outputStream;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;

    Gson gson = new Gson();
    Model m = BModel.build();
    ClientInfo clientInfo;


    public clientAtServer(Socket socket, MainServer mainServer, String userName) {
        this.socket = socket;
        this.mainServer = mainServer;
        clientInfo = new ClientInfo(userName);
        try {
            inputStream = this.socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);
            outputStream = this.socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
        } catch (IOException ex) {}

    }

    public String getUserName() {
        return clientInfo.getPlayerName();
    }

    public void sendInfoToClient() {
        try {
            MessageFromServer message = new MessageFromServer();

//            System.out.println("В процессе посылки данных");

            message.clientsInfoArrayList = m.getAllPlayers();
//            System.out.println("Все игроки = " + message.clientsInfoArrayList);
            message.stickArrowInfoArrayList = m.getSticksAllArrows();
//            System.out.println("Палки = " + message.stickArrowInfoArrayList);
            message.nibArrowInfoArrayList = m.getNibsAllArrows();
//            System.out.println("Наконечники = " + message.nibArrowInfoArrayList);
            message.targetsInfoArrayList = m.getAllTargets();
//            System.out.println("Мишени = " + message.targetsInfoArrayList);
            message.winnerName = m.getWinner();
            System.out.println("Лидеры = " + message.leadersArrayList);
            message.leadersArrayList = m.getLeaders();

//            System.out.println("Победитель = " + message.winnerName);

//            TestGson testGson = new TestGson();
//            testGson.clientsInfoArrayList = m.getAllPlayers();
//            System.out.println("Test Gson = " + testGson);
//            try {
//                String s = gson.toJson(testGson);
//            } catch (Exception ex) {
//                System.out.println("Не удалось преобразовать в JSON" + ex);
//            }
//            System.out.println("Данные на клиент: ");

//            System.out.println("Все игроки 5 = " + message.clientsInfoArrayList);
            dataOutputStream.writeUTF(gson.toJson(message));
        } catch (IOException ex) {}
    }

    @Override
    public void run() {
        try {

            System.out.println("Client thread " + clientInfo.getPlayerName() + " started");

            // Нужно оповестить, что добавился новый перец
            m.addClient(clientInfo);
//            System.out.println("Info Client: " + clientInfo);
            mainServer.broadcast();

            while (true) {
                // Тут идёт обработка запросов с клиента (с формочки и для каждого действия вызывается соответствующий метод в моделе)
                String message = dataInputStream.readUTF();
                System.out.println("Mes: " + message);

                MessageFromClient messageFromClient = gson.fromJson(message, MessageFromClient.class);
                System.out.println("Message from Client: " + messageFromClient);

                if (messageFromClient.getClientActions() == ClientActions.SHOOT) {
                    // Делаем выстрел
                    m.clickedShoot(getUserName());
                }

                if (messageFromClient.getClientActions() == ClientActions.PAUSE) {
                    // Ставим на паузу
                    m.clickedPause(getUserName());
                }

                if (messageFromClient.getClientActions() == ClientActions.READY) {
                    System.out.println("READY " + getUserName());
                    m.clickedReady(mainServer, this.getUserName());
                }

                if (messageFromClient.getClientActions() == ClientActions.LEADERS) {
                    m.clickedLeaders(mainServer);
                }
            }



        } catch (IOException ex) {}
    }
}
