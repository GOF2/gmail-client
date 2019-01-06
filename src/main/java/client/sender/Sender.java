package client.sender;

import java.io.File;

public class Sender {
    private String subject;
    private String message;
    private String[] to;
    private File[] files;

    private Sender() {
    }

    public String getMessage() {
        return message;
    }

    public File[] getFiles() {
        return files;
    }
    public String[] getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }
    public static MailSender getInstanceSender() {
        return new Sender().new MailSender();
    }


    public class MailSender{
        private MailSender(){

        }
        public MailSender setSubject(String subject){
            Sender.this.subject = subject;
            return this;
        }
        public MailSender setMessage(String message){
            Sender.this.message = message;
            return this;
        }
        public MailSender setTo(String... to ){
            Sender.this.to = to;
            return this;
        }
        public MailSender setAttachment(File... files){
            Sender.this.files = files;
            return this;
        }
        public Sender send(){
            return Sender.this;
        }
    }
}
