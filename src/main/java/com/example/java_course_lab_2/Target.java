package com.example.java_course_lab_2;

public class Target {
    private int x = 0, y = 0, radius = 0;

    public Target(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public void setNewPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getRadius() {
        return radius;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Target{" +
                "x=" + x +
                ", y=" + y +
                ", r=" + radius +
                '}';
    }
}
