package client;

import client.message.Message;

import java.io.File;

public class GmailClient {
    private final String email;
    private final String password;

    public GmailClient(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static void main(String[] args) {
        // File[] file = new File[]{file1};
        //new GmailClient("serhiy.mazur0@gmail.com", "*******")
        //      .send("Subject", "Text", file, "serhiy.mazur0@gmail.com");*/


    }

    public  void start() {
        File file1 = new File("/home/serhiy/Downloads/maili-beach-park.jpg");
        new EmailAuthenticator("serhiy.mazur0@gmail.com", "123456789lena").getPasswordAuthentication();
        Message message =Message.MessageBuilder
                .setTo("serhiy.mazur0@gmail.com")
                .setMessage("hi")
                .setAttachment(file1)
                .createMessage();


    }

   /* public void send(String subject, String text, File[] attachment, String... to) {
        Sender.getInstanceSender().setTo(to)
                .setAttachment(attachment)
                .setSubject(subject)
                .setMessage(text)
                .send();
    }*/

    public void send(String text, File[] attachment, String... to) {

    }

    public void send(String text, String... to) {

    }

    public void send(String subject, String text, String... to) {

    }

    public void receive() {
    }
}
