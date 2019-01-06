package client.message;

import java.io.File;

public class Message {
    private String subject;
    private final String message;
    private final String[] to;
    private File[] attachment;

    Message(String subject, String message, File[] attachment, String... to) {
        this.subject = subject;
        this.message = message;
        this.attachment = attachment;
        this.to = to;
    }

    Message(String message, File[] attachment, String... to) {
        this.message = message;
        this.attachment = attachment;
        this.to = to;
    }

    Message(String message, String... to) {
        this.message = message;
        this.to = to;
    }

    Message(String subject, String message, String... to) {
        this.subject = subject;
        this.message = message;
        this.to = to;
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


}
