package client.core;

import java.io.File;
import java.util.Date;

public class ReceivedMessage extends BaseMessage{
    private String subject;
    private String message;
    private File[] attachment;
    private String from;
    private Date date;



    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public File[] getAttachment() {
        return attachment;
    }

    @Override
    public void setAttachment(File[] attachment) {
        this.attachment = attachment;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    ReceivedMessage(String subject, String message) {
        super(subject, message);
    }

    ReceivedMessage(String message) {
        super(message);
    }



}
