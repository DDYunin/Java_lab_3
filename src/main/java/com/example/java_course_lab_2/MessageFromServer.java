package com.example.java_course_lab_2;

import java.util.ArrayList;

public class MessageFromServer {

    // Инфа о клиентах: имя, число очков, количество выстрелов
    public ArrayList<ClientInfo> clientsInfoArrayList;

    // Инфа о положении мишений в данный момент
    public ArrayList<Target> targetsInfoArrayList;

    // Положение наконечников стрел в данный момент
    public ArrayList<MyPoint> nibArrowInfoArrayList;

    // Положение цевья стрел в данный момент
    public ArrayList<MyPoint> stickArrowInfoArrayList;

    // Лидеры
    public ArrayList<ClientInfo> leadersArrayList;

    // Победитель
    public String winnerName;

    @Override
    public String toString() {
        String s = "MessageFromServer{" + "clientsInfoArrayList=";
        if(clientsInfoArrayList != null) {
            for (ClientInfo clientInfo : clientsInfoArrayList) {
                s += clientInfo.toString();
            }
        }
        s += ", ";
        if(targetsInfoArrayList != null) {
            for (Target target : targetsInfoArrayList) {
                s += target.toString();
            }
        }
        s += ", ";

        if(nibArrowInfoArrayList != null) {
            for (MyPoint point : nibArrowInfoArrayList) {
                s += point.toString();
            }
        }
        s += ", ";
        if(stickArrowInfoArrayList != null) {
            for (MyPoint point : stickArrowInfoArrayList) {
                s += point.toString();
            }
        }
        s += ", ";
        if(leadersArrayList != null) {
            for (ClientInfo leader : leadersArrayList) {
                s += leader.toString();
            }
        }
        s += ", winnerName={" + winnerName + "}";
        s += '}';
        return s;
    }

}
