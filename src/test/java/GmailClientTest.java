import client.authenticator.EmailAuthenticator;
import client.authenticator.EmailAuthenticator.Gmail;
import client.core.GmailClient;
import client.core.common.SendedMessage;
import client.core.interfaces.IAuthentication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.mail.MessagingException;

@RunWith(JUnit4.class)
public class GmailClientTest {
    @Test
    public void test() {

    }

    @Test
    public void test1() {
        final SendedMessage message = new SendedMessage("", "").from("").to("");
        getClient()
                .auth()
                .send(
                        message,
                        () -> System.out.println("Sent successfully"),
                        e -> System.out.println("Send error: " + e.getMessage())
                );
    }

    private GmailClient getClient() {
        return GmailClient.get()
                .loginWith(Gmail.auth("abc", "pass"))
                .beforeLogin(() -> System.out.println("Before"))
                .onLoginError(e -> System.out.println("Login error: " + e.getMessage()))
                .onLoginSuccess(c -> System.out.println("Save session"));
    }
}