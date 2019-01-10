package client;

import client.authenticator.EmailAuthenticator;
import client.core.SendedMessage;
import client.utils.ErrorCallbacks;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file1 = new File("/home/serhiy/Downloads/maili-beach-park.jpg");
        File file2 = new File("/home/serhiy/Downloads/test.jpeg");

        SendedMessage message = new SendedMessage("Hello").to("serhiy.mazur0@gmail.com").
                from("Сергей Мазур");
        GmailClient client = GmailClient.getClient(new EmailAuthenticator("serhiy.mazur0@gmail.com",
                "123456789lena"), new ErrorCallbacks() {
            @Override
            public void authenticationFailed() {
                System.out.println("authetication failed");
            }

            @Override
            public void badInternetConnection() {
                System.out.println("bad internet");
            }
        });


        client.send(message);
        System.out.println(message.getDate());


    }
}
