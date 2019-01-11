import client.authenticator.EmailAuthenticator.Gmail;
import client.core.BaseGmailClient;
import client.core.GmailClient;
import client.core.common.BaseMessage;
import client.core.common.ReceivedMessage;
import client.core.common.SendedMessage;
import client.core.interfaces.IReceiver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.mail.MessagingException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class GmailClientTest {
    @Test
    public void test() {

    }

    @Test
    public void test1() {
        final BaseGmailClient client = getClient().auth();
        client.receive(new IReceiver.ReceiveCallback() {
            @Override public void onReceive(List<ReceivedMessage> messages) {
                System.out.println("Received messages: " + messages
                        .stream()
                        .map(BaseMessage::getMessage)
                        .collect(Collectors.joining(", "))
                );
            }
            @Override public void onUpdate(ReceivedMessage message) { System.out.println("New message: " + message.getMessage()); }
            @Override public void onError(MessagingException e) { System.out.println("Error: " + e.getMessage()); }
        });
    }
/*
    private SendedMessage buildMessage() {
        return new SendedMessage("Yesterday", "All my troubles seemed so far away")
                .from("John Lennon")
                .;
    }
*/

    private GmailClient getClient() {
        return GmailClient.get()
                .loginWith(Gmail.auth("serhiy.mazur0@gmail.com", "******"))
                .beforeLogin(() -> System.out.println("Process login..."))
                .onLoginError(e -> System.out.println("Login error: " + e.getMessage()))
                .onLoginSuccess(() -> System.out.println("Login successfully"));
    }
}