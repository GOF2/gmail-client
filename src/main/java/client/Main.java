package client;

import client.authenticator.EmailAuthenticator;
import client.message.Message;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        File file1 = new File("/home/serhiy/Downloads/maili-beach-park.jpg");
        File file2 = new File("/home/serhiy/Downloads/test.jpeg");

        Message message = new Message("Hello").to("serhiy.mazur0@gmail.com")
                .attachFiles(file1, file2);
        GmailClient.Sender.closeConnection();

        GmailClient.Sender.sendMessage(new EmailAuthenticator("serhiy.mazur0@gmail.com",
                "******"), message);
    }
}
