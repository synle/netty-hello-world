package com.github.synle.netty.data;

public class ToDo {
    private String message;
    private String status;

    public ToDo() {
    }

    public ToDo(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}