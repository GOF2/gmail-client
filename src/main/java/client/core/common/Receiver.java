package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.MockedDatabase;
import client.core.interfaces.IReceiver;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.util.Set;

import static client.core.common.MessageUtil.buildMessages;


public class Receiver extends BaseReceiver {
    private static Receiver receiver;
    private IReceiver.ReceiveCallback receiveCallback;

    private Receiver(EmailAuthenticator authenticator) {
        super(authenticator);
    }

    public static Receiver getInstance(EmailAuthenticator authenticator) {
        if (receiver == null)
            return new Receiver(authenticator);
        return receiver;
    }

    @Override
    public void handleReceiving(IReceiver.ReceiveCallback callback) {
        this.receiveCallback = callback;
        getFolder().addMessageCountListener(listener());
        initialReceive(receiveCallback);
        receiveNewMessage();
    }


    private void initialReceive(IReceiver.ReceiveCallback callback) {
        try {
            MimeMessage[] allMessages = retrieveMessages(Flags.Flag.USER, true);
            Set<ReceivedMessage> messages = buildMessages(allMessages);
            callback.onReceive(messages);
            MockedDatabase.getInstance().addAll(messages);
        } catch (MessagingException me) {
            callback.onError(me);
        }
    }

    private MimeMessage[] retrieveMessages(Flags.Flag flag, boolean set) throws MessagingException {
        return (MimeMessage[]) getFolder().search(new FlagTerm(new Flags(flag), set));
    }

    private void receiveNewMessage() {
        IMAPFolder folder = getFolder();
        startListen(folder);
    }

    private MessageCountAdapter listener() {
        return new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                try {
                    MimeMessage[] received = castMessage(e.getMessages());
                    System.out.println("Received len: " + received.length);
                    Set<ReceivedMessage> messages = buildMessages(received);
                    messages.forEach(m -> receiveCallback.onUpdate(m));
                } catch (ClassCastException s) {
                    System.out.println(s.getMessage());
                }
            }
        };
    }

    private MimeMessage[] castMessage(Message[] messages) {
        MimeMessage[] cast = new MimeMessage[messages.length];
        for (int i = 0; i < messages.length; i++) {
            cast[i] = ((MimeMessage) messages[i]);
        }
        return cast;
    }

    private void startListen(IMAPFolder folder) {
        Thread t = new Thread(
                new KeepAliveRunnable(folder), "IdleConnectionKeepAlive"
        );

        t.start();

        while (!Thread.interrupted()) {
            System.out.println("Starting IDLE");
            try {
                folder.idle();
            } catch (MessagingException e) {
                System.out.println("messaging exception while trying idle");
                throw new RuntimeException(e);
            }
        }

        // Shutdown keep alive thread
        if (t.isAlive()) {
            t.interrupt();
        }
    }
}