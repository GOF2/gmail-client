package client;

import client.authenticator.EmailAuthenticator;
import client.message.Message;
import client.utils.LoginChecker;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        File file1 = new File("/home/serhiy/Downloads/maili-beach-park.jpg");
        File file2 = new File("/home/serhiy/Downloads/test.jpeg");

        Message message = new Message("Hello").to("serhiy.mazur0@gmail.com")
                .attachFiles(file1, file2);
      GmailClient client = new GmailClient(new EmailAuthenticator("serhiy.mazur0@gmail.com",
              "123456789lena"));
        GmailClient client1 = new GmailClient(new EmailAuthenticator("serhiy.mazur0@gmail.com",
                "12345789lena"));
        GmailClient client2 = new GmailClient(new EmailAuthenticator("serhiy.mazur0@gmail.com",
                "123456789ena"));
        GmailClient client3 = new GmailClient(new EmailAuthenticator("serhiy.mazur0@gmail.com",
                "123456789lna"));
        client.send(message);

    }
}
