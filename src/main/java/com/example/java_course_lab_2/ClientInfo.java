package com.example.java_course_lab_2;

public class ClientInfo {
    // Имя игрока
    private String playerName;

    // Количество очков
    private int numberScores = 0;

    // Количество выстрелов
    private int numberShots = 0;

    // Количество побед
    private int numberWins;


    public ClientInfo(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getNumberShots() {
        return numberShots;
    }

    public int getNumberScores() {
        return numberScores;
    }

    public int getNumberWins() {
        return numberWins;
    }

    public void addNumberShots(int number) {
        numberShots += number;
    }

    public void addNumberScores(int numberScores) {
        this.numberScores += numberScores;
    }

    public void setNumberWins(int numberWins) {
        this.numberWins = numberWins;
    }

    @Override
    public String toString() {
        return "ClientInfo{playerName = " + playerName + ", numberScores = " + numberScores + ", numberShots = " + numberShots + ", numberWins = " + numberWins + "}";
    }

    public void reset() {
        numberShots = 0;
        numberScores = 0;
    }
}
