package com.example.java_course_lab_2;

public class MyPoint {

    private int x = 0, y = 0;
    MyPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void SetCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "MyPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }


}
