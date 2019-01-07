package client;

import client.message.Message;
import client.sender.Sender;

import java.io.File;

public class GmailClient {
    //private final String email;
    // private final String password;


    public static void main(String[] args) {
        // File[] file = new File[]{file1};
        //new GmailClient("serhiy.mazur0@gmail.com", "*******")
        //      .send("Subject", "Text", file, "serhiy.mazur0@gmail.com");*/
        new GmailClient().start();


    }

    public void start() {
        File file1 = new File("/home/serhiy/Downloads/maili-beach-park.jpg");
        File file2 = new File("/home/serhiy/Downloads/test.jpeg");

        Message message = new Message("Hello").to("serhiy.mazur0@gmail.com", "artemgerman1706@gmail.com")
                .attachFiles(file1, file2);
        Sender sender = new Sender();
        sender.sendMessage(new EmailAuthenticator("serhiy.mazur0@gmail.com", "*******")
                , message);


    }


}
