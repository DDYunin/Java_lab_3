package com.example.java_course_lab_2;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Находится код сервера и код обработки взаимодействия этого сервера
public class MainServer {
    Model m = BModel.build();

    int port = 3124;
    InetAddress ip = null;

    // Чтобы было потоков неограниченное количество, чтобы они создавались автоматически
    ExecutorService service = Executors.newCachedThreadPool();

    // Число клиентов на сервере
    ArrayList<clientAtServer> allClients = new ArrayList<>();

    BufferedReader in;
    PrintWriter out;

    public void broadcast() {
        System.out.println(allClients);
        for (clientAtServer allClient : allClients) {
            // Посылаем данные
            System.out.println("Посылаем данные клиенту");
            allClient.sendInfoToClient();
        }
    }
    public void serverStart() {
        ServerSocket serverSocket;
        try {

            // Создаём экземпляр класса, который взаимодействует с базой данных
            DB_jdbc db = new DB_jdbc();
            m.init(db);

            ip = InetAddress.getLocalHost();
            serverSocket = new ServerSocket(port, 0, ip);
            System.out.println("Server Start");
            // Нужно создать модель
            m.createStartPos();

            while (true) {
                Socket socket;
                socket = serverSocket.accept();
                System.out.println("Client connect");

//                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                out = new PrintWriter(socket.getOutputStream(), true);
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

//                String userName = in.readLine();
                String userName = dataInputStream.readUTF();
                System.out.println("Current userName = " + userName);
                System.out.println("AllClients = " + allClients.size());
                if (allClients.size() < 4 && allClients.stream().filter( client -> client.getUserName().equals(userName)).findFirst().orElse(null) == null) {
//                    out.println("Доступ получен");
                    dataOutputStream.writeUTF("Доступ получен");
                    clientAtServer client = new clientAtServer(socket, this, userName);
                    allClients.add(client);
                    service.submit(client);
                    System.out.println("RESPONSE ACCEPT");
                } else {
//                    out.println("В доступе отказано");
                    dataOutputStream.writeUTF("В доступе отказано");
                    System.out.println("RESPONSE DECLINE");
//                    socket.close();
                }
            }
        } catch (IOException ex) {

        }
    }

    public static void main(String[] args) {
        new MainServer().serverStart();
    }

}
