package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.MockedDatabase;
import client.core.interfaces.IReceiver;
import com.sun.mail.imap.IMAPFolder;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Receiver {
    private static Receiver receiver;
    private EmailAuthenticator authenticator;
    private IReceiver.ReceiveCallback receiveCallback;

    private Receiver(EmailAuthenticator authenticator) {
        this.authenticator = authenticator;
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
        ConnectionManager.getFolder(authenticator).addMessageCountListener(listener());
        initialReceive(receiveCallback);
        startListen();
    }

    private void startListen() {
        receiveNewMessage();
    }

    private Set<ReceivedMessage> buildMessages(MimeMessage[] messages) {
        final Set<ReceivedMessage> receivedMessageSet = new TreeSet<>();
        final List<File> listFiles = new ArrayList<>();
        try {
            for (MimeMessage message : messages) {
                MimeMessage cmsg = new MimeMessage(message);
                Address[] fromAddress = cmsg.getFrom();
                String email = fromAddress == null ? null : ((InternetAddress) fromAddress[0]).getAddress();
                String subject = cmsg.getSubject();
                String text = "";
                String contentType = cmsg.getContentType();
                if (contentType.contains("multipart")) {
                    Multipart multipart = (Multipart) cmsg.getContent();
                    for (int i = 0; i < multipart.getCount(); i++) {
                        MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            BodyPart bodyPart = multipart.getBodyPart(i);
                            InputStream is = bodyPart.getInputStream();
                            File f = new File("/GIT/gmail-client/src/test/java/tmp/" + bodyPart.getFileName());
                            FileOutputStream fos = new FileOutputStream(f);
                            byte[] buf = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buf)) != -1) {
                                fos.write(buf, 0, bytesRead);
                            }
                            fos.close();
                            listFiles.add(f);
                        } else {
                            text = getTextFromMimeMultipart(multipart);
                        }
                    }
                    if (contentType.contains("text/plain")) {
                        Object content = cmsg.getContent();
                        if (content != null) {
                            text = getTextFromMimeMultipart(multipart);
                        }
                    }
                }
                if (listFiles.size() == 0) {
                    ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text);
                    receivedMessage.setDate(cmsg.getSentDate());
                    receivedMessageSet.add(receivedMessage);
                } else {
                    File[] array = listFiles.toArray(new File[0]);
                    ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text,
                            array);
                    receivedMessage.setDate(cmsg.getSentDate());
                    // TODO: 12.01.19
                    receivedMessageSet.add(receivedMessage);
                    listFiles.clear();
                }
            }

        } catch (MessagingException ignored) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        return receivedMessageSet;
    }


    private void initialReceive(IReceiver.ReceiveCallback callback) {
        try {
            Folder folder = ConnectionManager.getFolder(authenticator);
            MimeMessage[] messages = (MimeMessage[]) folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
            Set<ReceivedMessage> setMessages = buildMessages(messages);
            callback.onReceive(setMessages);
            MockedDatabase.getInstance().addAll(setMessages);
            //callback.onError(new MessagingException()); // use in the catch block
        } catch (MessagingException me) {
            callback.onError(me);
        }

    }


    private String getTextFromMimeMultipart(Multipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append("\n").append(bodyPart.getContent());
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append("\n").append(Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    private void receiveNewMessage() {
        IMAPFolder folder = ConnectionManager.getFolder(authenticator);
        folder.addMessageCountListener(listener());
        startListen(folder);
    }

    private MessageCountAdapter listener() {
        return new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                try {
                    Folder folder = ConnectionManager.getFolder(authenticator);
                    MimeMessage[] messagesArr = (MimeMessage[]) folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                    System.out.println("new Messages" + messagesArr.length);
                    Set<ReceivedMessage> messages = buildMessages(messagesArr);
                    messages.forEach(m ->
                            receiveCallback.onUpdate(m)
                    );
                    MockedDatabase.getInstance().addAll(messages);
                } catch (MessagingException e1) {
                    e1.printStackTrace();
                }
            }
        };
    }

    private void startListen(IMAPFolder folder) {
        IdleThread idleThread = new IdleThread(folder, authenticator.getAuthData());
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

