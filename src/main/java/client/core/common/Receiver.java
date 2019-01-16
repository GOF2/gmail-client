package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.MockedDatabase;
import client.core.interfaces.IReceiver;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.IOException;
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
        folder.addMessageCountListener(listener());
        startListen(folder);
    }

    private MessageCountAdapter listener() {
        return new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                try {
                    MimeMessage[] received = retrieveMessages(Flags.Flag.SEEN, false);
                    System.out.println("Received len: " + received.length);
                    Set<ReceivedMessage> messages = buildMessages(received);
                    messages.forEach(m -> receiveCallback.onUpdate(m));
                    MockedDatabase.getInstance().addAll(messages);
                } catch (MessagingException e1) {
                    e1.printStackTrace();
                }
                System.out.println();
            }
        };
    }

    private void startListen(IMAPFolder folder) {
        IdleThread idleThread = new IdleThread(folder, getAuthenticator().getAuthData());
        idleThread.setDaemon(false);
        idleThread.start();
        try {
            idleThread.join();
        } catch (InterruptedException ioe) {
            ioe.printStackTrace();
            idleThread.close(folder);
            idleThread.kill();
        }
    }
}