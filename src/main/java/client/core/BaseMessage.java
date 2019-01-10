package client.core;

import java.io.File;
import java.util.Date;

public abstract class BaseMessage {
    private String from;
    private String subject;
    private String message;
    private File[] attachment;
    private Date date;

    BaseMessage(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    BaseMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    void setDate(Date date) {
        this.date = date;
    }


    String getFrom() {
        return from;
    }

    void setFrom(String from) {
        this.from = from;
    }

    String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    File[] getAttachment() {
        return attachment;
    }

    void setAttachment(File[] attachment) {
        this.attachment = attachment;
    }


}
