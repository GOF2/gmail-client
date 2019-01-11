package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.interfaces.IReceiver;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Receiver {
    private static Receiver receiver;
    private EmailAuthenticator authenticator;
    private IReceiver.ReceiveCallback receiveCallback;

    private Receiver(EmailAuthenticator authenticator) {
    }

    public static Receiver getInstance(EmailAuthenticator authenticator) {
        if (receiver == null)
            return new Receiver(authenticator);
        return receiver;
    }

    public void setAuthenticator(EmailAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void handleReceiving(IReceiver.ReceiveCallback callback) {
        this.receiveCallback = callback;
        listen(receiveCallback);
    }

    private void listen(IReceiver.ReceiveCallback callback) {
        final List<ReceivedMessage> messages = mockRetrieveMessages();
        callback.onReceive(messages);

        final ReceivedMessage receivedMessage1 = mockUpdateMessage();
        final ReceivedMessage receivedMessage2 = mockUpdateMessage();
        final ReceivedMessage receivedMessage3 = mockUpdateMessage();

        callback.onUpdate(receivedMessage1);
        callback.onUpdate(receivedMessage2);
        callback.onUpdate(receivedMessage3);

        callback.onError(new MessagingException()); // use in the catch block
    }

    private List<ReceivedMessage> mockRetrieveMessages() {
        // TODO: 11.01.19 insert here loading and gathering all initial messages.
        // Note: this method can to throw exceptions
        return Arrays.asList(
                new ReceivedMessage("1", "a"),
                new ReceivedMessage("2", "b"),
                new ReceivedMessage("3", "c")
        );
    }

    private ReceivedMessage mockUpdateMessage() {
        // TODO: 11.01.19 paste here getting new message.
        // Note: this method can to throw exceptions
        return new ReceivedMessage("New", "mail => " + new Random().nextInt());
    }
}
