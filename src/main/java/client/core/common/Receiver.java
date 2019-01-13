package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.MockedDatabase;
import client.core.interfaces.IReceiver;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


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
        initialReceive(receiveCallback);
        // folder.addMessageCountListener(listener());
        //startListen(ConnectionManager.getFolder(authenticator));
        beepForAnHour();
        compareWithFile();
    }


    private Set<ReceivedMessage> buildMessages(Message[] messages) {
        final Set<ReceivedMessage> receivedMessageSet = new TreeSet<>();
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
                        Object content = message.getContent();
                        if (content != null) {
                            text = getTextFromMimeMultipart(multipart);
                        }
                    }
                }
                if (listFiles.size() == 0) {
                    ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text);
                    receivedMessage.setDate(message.getSentDate());
                    receivedMessageSet.add(receivedMessage);
                } else {
                    File[] array = listFiles.toArray(new File[0]);
                    ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text,
                            array);
                    receivedMessage.setDate(message.getSentDate());
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

    private void compareWithFile(){
        try {
            Folder folder = ConnectionManager.getFolder(authenticator);
            int size = folder.getMessageCount();
                FileInputStream file = new FileInputStream("/GIT/gmail-client/src/test/java/tmp/LogData");
                ObjectInputStream in = new ObjectInputStream(file);
                Set set = (Set) in.readObject();
                in.close();
                file.close();
                if(set.size() < size){
                    Flags seen = new Flags(Flags.Flag.SEEN);
                    FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
                    Message messages[] = folder.search(unseenFlagTerm);
                    folder.close(false);
                    Set<ReceivedMessage> receivedMessages = buildMessages(messages);
                    MockedDatabase.getInstance().addAll(receivedMessages);
                    storeInFile(MockedDatabase.getInstance().getMessages());

                    receivedMessages.forEach(m -> receiveCallback.onUpdate(m));
                }
        }catch (MessagingException | IOException | ClassNotFoundException ignored){
            ignored.printStackTrace();
        }
    }

    private void initialReceive(IReceiver.ReceiveCallback callback) {
        try {
            Folder folder = ConnectionManager.getFolder(authenticator);
            Message[] messages = folder.getMessages();
            Set<ReceivedMessage> setMessages = buildMessages(messages);
            ConnectionManager.closeFolder();//////////////////////////
            callback.onReceive(setMessages);
            MockedDatabase.getInstance().addAll(setMessages);
            storeInFile(MockedDatabase.getInstance().getMessages());
            //callback.onError(new MessagingException()); // use in the catch block
        } catch (MessagingException me) {
            callback.onError(me);
        }
    }
    private void storeInFile(Set set){
        try
        {
            FileOutputStream file = new FileOutputStream("/GIT/gmail-client/src/test/java/tmp/LogData");
            ObjectOutputStream out = new ObjectOutputStream(file);
            // Method for serialization of object
            out.writeObject(set);
            out.close();
            file.close();
            System.out.println("Object has been serialized");
        }
        catch(IOException ex)
        {
            System.out.println("IOException is caught");
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

    private MessageCountAdapter listener() {
        return new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                Message[] received = e.getMessages();
                Set<ReceivedMessage> messages = buildMessages(received);
                messages.forEach(m ->
                        receiveCallback.onUpdate(m)
                );
                MockedDatabase.getInstance().addAll(messages);
            }
        };
    }

        private final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        public void beepForAnHour() {
            final Runnable beeper = this::compareWithFile;
            final ScheduledFuture<?> beeperHandle =
                    scheduler.scheduleAtFixedRate(beeper, 10, 5, SECONDS);
            scheduler.schedule(() -> {
                beeperHandle.cancel(true);
                }, 60 * 60, SECONDS);
        }
}

