package client.message;

import javax.activation.FileDataSource;
import java.io.File;

public class Message {
    private String subject;

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public void setAttachment(File[] attachment) {
        this.attachment = attachment;
    }

    private String message;
    private  String[] to;
    private File[] attachment;

    /*Message(String subject, String message, File[] attachment, String... to) {
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
    }*/

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

    public class MessageBuilder{
        private MessageBuilder(){
        }

        public MessageBuilder setSubject(String subject){
            Message.this.subject = subject;
            return this;
        }
        public MessageBuilder setMessage(String message){
            Message.this.message = message;
            return this;
        }
        public MessageBuilder setTo(String... to ){
            Message.this.to = to;
            return this;
        }
        public MessageBuilder setAttachment(File... attachment){
            Message.this.attachment = attachment;
            return this;
        }


        public Message createMessage(){
            return Message.this;
        }
    }

}
