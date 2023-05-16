package com.example.java_course_lab_2;

public class TableEntity implements Comparable<TableEntity>{
    private int numberWins;
    private String playerName;

    public TableEntity(String playerName,int numberWins) {
        this.numberWins = numberWins;
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getNumberWins() {
        return numberWins;
    }

    public void setNumberWins(int numberWins) {
        this.numberWins = numberWins;
    }

    @Override
    public int compareTo(TableEntity tableEntity) {
        int compareage = ((TableEntity)tableEntity).getNumberWins();
        return compareage - this.numberWins;
    }

    @Override
    public String toString() {
        return "TableEntity{playerName = " + playerName + ", numberWins = " + numberWins + "}";
    }
}
