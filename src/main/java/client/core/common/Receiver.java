package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.MockedDatabase;
import client.core.interfaces.IReceiver;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.*;
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
        initialReceive();
        startListen();
    }

    private void startListen() {
        IdleThread thread = new IdleThread(authenticator, receiveCallback);
        thread.run();
        //Timer timer = new Timer();
        //timer.schedule(new IdleThread(authenticator), 5000, 100000);
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

    void compareWithFile(IReceiver.ReceiveCallback receiveCallback) {
        Flags seen = new Flags(Flags.Flag.SEEN);
        FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
        try {
            //ConnectionManager.getFolder(authenticator);
           // Folder folder = ConnectionManager.getFolder(authenticator);
            int size = ConnectionManager.getFolder(authenticator).getMessageCount();
            FileInputStream file = new FileInputStream("/GIT/gmail-client/src/test/java/tmp/LogData");
            ObjectInputStream in = new ObjectInputStream(file);
            Set set = (Set) in.readObject();
            System.out.println(set.size());
            in.close();
            file.close();
            if (set.size() <= size) {
                unseenFlagTerm = new FlagTerm(seen, false);
                Message messages[] = ConnectionManager.getFolder(authenticator).search(unseenFlagTerm);
              /*  FetchProfile fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);

                fp.add(FetchProfile.Item.CONTENT_INFO);

                folder.fetch(messages, fp);*/
               // System.out.println(folder.getMessageCount());
                Set<ReceivedMessage> receivedMessages = buildMessages(messages);
                ConnectionManager.closeFolder();
                MockedDatabase.getInstance().addAll(receivedMessages);
                storeInFile(MockedDatabase.getInstance().getMessages());
                receivedMessages.forEach(receiveCallback::onUpdate);
            }
        } catch (MessagingException | IOException | ClassNotFoundException ignored) {
            ignored.printStackTrace();
        }
    }

    private void initialReceive() {
        try {
            //Folder folder = ConnectionManager.getFolder(authenticator);
            Message[] messages = ConnectionManager.getFolder(authenticator).getMessages();
            Set<ReceivedMessage> setMessages = buildMessages(messages);
            ConnectionManager.closeFolder(); //////////////////////////
            receiveCallback.onReceive(setMessages);
            MockedDatabase.getInstance().addAll(setMessages);
            storeInFile(MockedDatabase.getInstance().getMessages());
            //callback.onError(new MessagingException()); // use in the catch block
        } catch (MessagingException me) {
            receiveCallback.onError(me);
        }
    }

    private void storeInFile(Set set) {
        try {
            FileOutputStream file = new FileOutputStream("/GIT/gmail-client/src/test/java/tmp/LogData");
            ObjectOutputStream out = new ObjectOutputStream(file);
            // Method for serialization of object
            out.writeObject(set);
            out.close();
            file.close();
            System.out.println("Object has been serialized");
        } catch (IOException ex) {
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
/*
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
*/

}

