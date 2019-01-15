import client.authenticator.EmailAuthenticator.Gmail;
import client.core.BaseGmailClient;
import client.core.GmailClient;
import client.core.MockedDatabase;
import client.core.common.BaseMessage;
import client.core.common.ReceivedMessage;
import client.core.common.SendedMessage;
import client.core.interfaces.IReceiver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@RunWith(JUnit4.class)
public class GmailClientTest {
    @Test
    public void test() {
        MockedDatabase database = MockedDatabase.getInstance();
        database.getMessages().add(new ReceivedMessage("", "", "1", new Date(2018, 0, 3)));
        database.getMessages().add(new ReceivedMessage("", "", "2", new Date(2018, 2, 20)));
        database.getMessages().add(new ReceivedMessage("", "", "3", new Date(2018, 2, 1)));
        database.getMessages().add(new ReceivedMessage("", "", "4", new Date(2018, 11, 3)));
        System.out.println(database.getMessages().stream()
                .map(m -> m.getMessage() + " " + m.getDate())
                .collect(Collectors.joining("\n")));
    }

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
                .loginWith(Gmail.auth("serhiy.mazur1@gmail.com", "123456789lena"))
                .beforeLogin(() -> System.out.println("Process login..."))
                .onLoginError(e -> System.out.println("Login error: " + e.getMessage()))
                .onLoginSuccess(() -> System.out.println("Login successfully"));
    }
}