package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.interfaces.IReceiver;
import client.utils.Host;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Receiver {
    private static Receiver receiver;
    private Store store;
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
        listen(receiveCallback);
    }

    private void listen(IReceiver.ReceiveCallback callback) {
        final List<ReceivedMessage> listMessages = new ArrayList<>();
        final List<File> listFiles = new ArrayList<>();
        try {
            Message[] messages = getFolder().getMessages();
            for (Message message : messages) {
                Address[] fromAddress = message.getFrom();
                String email = fromAddress == null ? null : ((InternetAddress) fromAddress[0]).getAddress();
                String subject = message.getSubject();
                String text = "";
                String contentType = message.getContentType();
                if (contentType.contains("multipart")) {
                    Multipart multipart = (Multipart) message.getContent();
                    for (int i = 0; i < multipart.getCount(); i++) {
                        MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            BodyPart bodyPart = multipart.getBodyPart(i);
                            InputStream is = bodyPart.getInputStream();
                            File f = new File("src/test/java/tmp/" + bodyPart.getFileName());
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
                        Object content = message.getContent();
                        if (content != null) {
                            text = getTextFromMimeMultipart(multipart);
                        }
                    }
                }
                if (listFiles.size() == 0) {
                    ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text);
                    receivedMessage.setDate(message.getReceivedDate());
                    listMessages.add(receivedMessage);
                } else {
                    File[] array = listFiles.toArray(new File[0]);
                    ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text,
                            array);
                    receivedMessage.setDate(message.getReceivedDate());
                    listMessages.add(receivedMessage);
                    listFiles.clear();
                }
            }

            //callback.onUpdate(receivedMessage1);
            //callback.onUpdate(receivedMessage2);
            //callback.onUpdate(receivedMessage3);

            //callback.onError(new MessagingException()); // use in the catch block
        } catch (MessagingException me) {
            callback.onError(me);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        callback.onReceive(listMessages);
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
                result.append("\n").append(org.jsoup.Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    public IMAPFolder getFolder() throws NoSuchProviderException {
        Folder emailFolder = null;
        final Session session = Session.getInstance(Host.getReceiveProperties(), authenticator);
        store = session.getStore(Host.getSendProperties().getProperty("mail.imaps.protocol"));
        try {
            final PasswordAuthentication authentication = authenticator.getPasswordAuthentication();
            store.connect(
                    Host.getReceiveProperties().getProperty("mail.imap.host"),
                    authentication.getUserName(),
                    authentication.getPassword()
            );
            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return (IMAPFolder) emailFolder;
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
