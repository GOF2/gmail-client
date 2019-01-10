package client;

import client.authenticator.EmailAuthenticator;
import client.message.Message;
import client.utils.ErrorCallbacks;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        File file1 = new File("/home/serhiy/Downloads/maili-beach-park.jpg");
        File file2 = new File("/home/serhiy/Downloads/test.jpeg");

        Message message = new Message("Subject","Hello").to("serhiy.mazur0@gmail.com",
                "artemgerman1706@gmail.com").attachFiles(file1).
                from("Сергей Мазур");
        GmailClient client = GmailClient.getClient(new EmailAuthenticator("serhiy.mazur0@gmail.com",
                ""), new ErrorCallbacks() {
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

    }
}
