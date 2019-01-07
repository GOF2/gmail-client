package client.message;

import java.io.File;

public class Message {
    private String subject;
    private String message;
    private String[] to;
    private File[] attachment;

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


}
