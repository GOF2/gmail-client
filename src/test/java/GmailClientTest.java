import client.GmailClient;
import client.authenticator.EmailAuthenticator;
import client.authenticator.EmailAuthenticator.Gmail;
import client.core.SendedMessage;
import client.utils.ErrorCallbacks;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

@RunWith(JUnit4.class)
public class GmailClientTest {
    @Test
    public void test() {
        final ErrorCallbacks errorCallbacks = new ErrorCallbacks() {
            @Override public void authenticationFailed() { System.out.println("authetication failed"); }
            @Override public void badInternetConnection() { System.out.println("bad internet"); }
        };
        final EmailAuthenticator auth = Gmail.auth("test.mail.client008@gmail.com", "test_mail_client");
        final SendedMessage message = new SendedMessage("What's up", "Hi")
                .from("Vasya")
                .to("bbwgd77@gmail.com")
                .attachFiles(
                        new File("/home/rost/Projects/IdeaProjects/gmail-client/src/test/java/file1.txt"),
                        new File("/home/rost/Projects/IdeaProjects/gmail-client/src/test/java/file2.txt")
                );
        GmailClient.getClient(auth, errorCallbacks).send(message);
    }
}