import client.authenticator.EmailAuthenticator.Gmail;
import client.core.GmailClient;
import client.core.common.SendedMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GmailClientTest {
    @Test
    public void test() {

    }

    @Test
    public void test1() {
        getClient()
                .auth()
                .send(
                        buildMessage(),
                        () -> System.out.println("Sent successfully"),
                        e -> System.out.println("Send error: " + e.getMessage())
                );
    }

    private SendedMessage buildMessage() {
        return new SendedMessage("Yesterday", "All my troubles seemed so far away")
                .from("John Lennon")
                .to("bbwgd77@gmail.com");
    }

    private GmailClient getClient() {
        return GmailClient.get()
                .loginWith(Gmail.auth("", ""))
                .beforeLogin(() -> System.out.print(""))
                .onLoginError(e -> System.out.println("Login error: " + e.getMessage()))
                .onLoginSuccess(() -> System.out.println("Login successfully"));
    }
}