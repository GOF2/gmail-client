package client.message;

import java.io.File;

public final class Message {
    private String subject;
    private String message;
    private String[] to;
    private File[] attachment;
    private String from;
    public Message(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    public Message(String message) {
        this.message = message;
    }


    private void setTo(String[] to) {
        this.to = to;
    }
    private void setFrom(String from) {
        this.from = from;
    }

    private void setAttachment(File[] attachment) {
        this.attachment = attachment;
    }


    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String[] getTo() {
        return to;
    }
    public String getFrom() {
        return from;
    }

    public File[] getAttachment() {
        return attachment;
    }

    public Message attachFiles(File... file) {
        this.setAttachment(file);
        return Message.this;
    }

    public Message to(String... to) {
        this.setTo(to);
        return Message.this;
    }

    public Message from(String from) {
        this.setFrom(from);
        return Message.this;
    }

}
