import client.authenticator.EmailAuthenticator.Gmail;
import client.core.GmailClient;
import client.core.common.ReceivedMessage;
import client.core.common.SendedMessage;
import client.core.interfaces.IReceiver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.mail.MessagingException;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class GmailClientTest {
    @Test
    public void test1() {
        final GmailClient client = getClient().auth();
        client.send(buildMessage());
        client.receive(new IReceiver.ReceiveCallback() {
            @Override
            public void onReceive(Set<ReceivedMessage> messages) {
                System.out.println("=====================================================");
                System.out.println("Received messages: " + messages
                        .stream()
                        .map(m ->  " => " + m.getDate())
                        .collect(Collectors.joining("\n"))
                );
                System.out.println("=====================================================");
                System.out.println(messages.size());
            }

            @Override
            public void onUpdate(ReceivedMessage message) {
                System.out.println("New message: " + message.getMessage() + " => " + message.getDate());
            }

            @Override
            public void onError(MessagingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
    }

    private SendedMessage buildMessage() {
        return new SendedMessage("Yesterday", "All my troubles seemed so far away")
                .from("John Lennon")
                .to("bbwgd77@gmail.com");
    }

    private GmailClient getClient() {
        return GmailClient.get()
                .loginWith(Gmail.auth("login", "password"))
                .beforeLogin(() -> System.out.println("Process login..."))
                .onLoginError(e -> System.out.println("Login error: " + e.getMessage()))
                .onLoginSuccess(() -> System.out.println("Login successfully"));
    }
}