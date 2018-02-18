package com.jira.updater.model;

/**
 * Created by nestor on 18.02.2018.
 */
public class Task {
    private String ticket;
    private String status;
    private String comment;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Task{" +
                "ticket='" + ticket + '\'' +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
