package com.example.java_course_lab_2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
}
