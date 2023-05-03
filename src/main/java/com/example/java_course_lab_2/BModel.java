package com.example.java_course_lab_2;

// Содержит статическое поле модели
public class BModel {
    static Model m = new Model();

    static public Model build() {
        return m;
    }
}
