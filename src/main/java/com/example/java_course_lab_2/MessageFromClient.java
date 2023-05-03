package com.example.java_course_lab_2;

public class MessageFromClient {
    ClientActions clientAction;

    public MessageFromClient(ClientActions clientAction) {
        this.clientAction = clientAction;
    }
    public ClientActions getClientActions() {
        return clientAction;
    }

    @Override
    public String toString() {
        return "MessageFromClient{" + "clientAction=" + clientAction + '}';
    }
}
