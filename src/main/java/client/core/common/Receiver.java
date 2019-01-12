package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.IMAPIdle;
import client.core.interfaces.IReceiver;
import client.utils.Host;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.*;
import java.util.*;

public class Receiver {
    private static Receiver receiver;
    private static Store store;
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
        // initialReceive(receiveCallback);
        startListen();
    }

    private void startListen() {
        receiveNewMessage();
       // IMAPIdle idle = new IMAPIdle();
        //idle.startListening(getFolder());
    }

    private List<ReceivedMessage> buildMessage(Message[] messages) {
        final List<ReceivedMessage> listMessages = new ArrayList<>();
        final List<File> listFiles = new ArrayList<>();
        try {
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
        } catch (MessagingException me) {

        } catch (IOException e) {
            e.printStackTrace();
        }
        return listMessages;
    }

    private void initialReceive(IReceiver.ReceiveCallback callback) {
        try {
            Message[] messages = getFolder().getMessages();
            callback.onReceive(buildMessage(messages));

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
                result.append("\n").append(org.jsoup.Jsoup.parse(html).text());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    public IMAPFolder getFolder() {
        Folder emailFolder = null;
        final Session session = Session.getInstance(Host.getReceiveProperties(), authenticator);
        try {
            store = session.getStore(Host.getSendProperties().getProperty("mail.imaps.protocol"));
            final PasswordAuthentication authentication = authenticator.getPasswordAuthentication();
            store.connect(
                    Host.getReceiveProperties().getProperty("mail.imap.host"),
                    authentication.getUserName(),
                    authentication.getPassword()
            );
            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
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

    public void receiveNewMessage() {
        IMAPFolder folder  = getFolder();
        folder.addMessageCountListener(new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                Message[] msgs = e.getMessages();
                System.out.println("new Messages" + msgs.length);
                for (int i = 0; i < msgs.length; i++) {
                    receiveCallback.onUpdate(buildMessage(msgs).get(i));
                }
            }
        });
        IdleThread idleThread = new IdleThread(folder);
        idleThread.setDaemon(false);
        idleThread.start();
            try{
        idleThread.join();}
        // idleThread.kill(); //to terminate from another thread
        catch (Exception e) {
        e.printStackTrace();
    } finally {

        close(folder);
        close(store);
    }

    }


    private static class IdleThread extends Thread {
        private final Folder folder;
        private volatile boolean running = true;

        public IdleThread(Folder folder) {
            super();
            this.folder = folder;
        }

        public synchronized void kill() {

            if (!running)
                return;
            this.running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    ensureOpen(folder);
                    System.out.println("enter idle");
                    ((IMAPFolder) folder).idle();
                } catch (Exception e) {
                    // something went wrong
                    // wait and try again
                    e.printStackTrace();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        // ignore
                    }
                }

            }
        }
    }

    public static void close(final Folder folder) {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
        } catch (final Exception e) {
            // ignore
        }

    }

    public static void close(final Store store) {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (final Exception e) {
            // ignore
        }

    }

    public static void ensureOpen(final Folder folder) throws MessagingException {

        if (folder != null) {
            Store store = folder.getStore();
            if (store != null && !store.isConnected()) {
                store.connect("serhiy.mazur0@gmail.com", "*****");
            }
        } else {
            throw new MessagingException("Unable to open a null folder");
        }

        if (folder.exists() && !folder.isOpen() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            System.out.println("open folder " + folder.getFullName());
            folder.open(Folder.READ_ONLY);
            if (!folder.isOpen())
                throw new MessagingException("Unable to open folder " + folder.getFullName());
        }

    }
}

