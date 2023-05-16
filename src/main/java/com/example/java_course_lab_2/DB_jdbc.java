package com.example.java_course_lab_2;

import javafx.scene.control.IndexRange;

import java.sql.*;
import java.util.ArrayList;

// Данный класс взаимодействует с базой данных
public class DB_jdbc {
     Connection c;

     void connection() {
          try {
               Class.forName("org.sqlite.JDBC");
               c = DriverManager.getConnection("jdbc:sqlite:DB_players_wins.db");
               System.out.println("Открытие базы данных произошло успешно");
          } catch (ClassNotFoundException e) {
               // Ничего не делаем, но можно сюда логгер запихнуть
          } catch (SQLException e) {
               // Ничего не делаем, но можно сюда логгер запихнуть
          }
     }

     public DB_jdbc() {
          // Организовываем подключение к базе данных
          connection();
     }

     // Метод, который может прочитать инфу из базы данных
     public ArrayList<ClientInfo> getAllUserInfo() {
          ArrayList<ClientInfo> names = new ArrayList<>();

          try {
               Statement st = c.createStatement();
               ResultSet r = st.executeQuery("select * from gameStat");

               while(r.next()) {
                    names.add(new ClientInfo(r.getString("playerName")));
                    names.get(names.size() - 1).setNumberWins(r.getInt("numberWins"));
               }

          } catch (SQLException ex) {}

          return names;
     }

     // Метод, который записывает инфу в базу данных
     public void writeInfo(ClientInfo player) {
          try {
               Statement st = c.createStatement();
               ResultSet resultSet = st.executeQuery("SELECT * FROM gameStat WHERE playerName = " + player.getPlayerName());
//               System.out.println("Alloha" + resultSet.getString("playerName"));
                    PreparedStatement pst = c.prepareStatement("UPDATE gameStat SET playerName = ?, numberWins = ? WHERE playerName = ?", Statement.RETURN_GENERATED_KEYS);
                    pst.setString(1, player.getPlayerName());
                    pst.setInt(2, player.getNumberWins());
                    pst.setString(3, player.getPlayerName());
                    pst.executeUpdate();
//               }

          } catch (SQLException ex) {
               try {
//                    System.out.println("Alloha");
                    PreparedStatement pst = c.prepareStatement("INSERT INTO gameStat(playerName, numberWins) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);
                    pst.setString(1, player.getPlayerName());
                    pst.setInt(2, player.getNumberWins());
                    pst.executeUpdate();
               } catch (SQLException ex1) {}

          }
     }

}
