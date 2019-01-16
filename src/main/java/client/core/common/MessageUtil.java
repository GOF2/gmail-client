package client.core.common;

import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class MessageUtil {
    private final static Set<ReceivedMessage> receivedMessageSet = new TreeSet<>();

    static Set<ReceivedMessage> messages(MimeMessage[] messages) throws MessagingException, IOException {
        for (MimeMessage m : messages) {
            receivedMessageSet.add(buildMessage(m));
        }
        return receivedMessageSet;
    }

    static ReceivedMessage buildMessage(MimeMessage message) throws MessagingException, IOException {
        if (message.getContentType().contains("multipart")) {
            return getMessageWithAttachment(message);
        } else {
            return getMessageNoAttachment(message);
        }
    }


    static ReceivedMessage getMessageWithAttachment(MimeMessage cmsg) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(cmsg);
        final List<File> listFiles = new ArrayList<>();
        Address[] fromAddress = message.getFrom();
        String email = fromAddress == null ? null : ((InternetAddress) fromAddress[0]).getAddress();
        String subject = message.getSubject();
        String text = "";
        Multipart multipart = (Multipart) message.getContent();
        for (int i = 0; i < multipart.getCount(); i++) {
            MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                MimeBodyPart bodyPart = (MimeBodyPart) multipart.getBodyPart(i);
                try {
                    bodyPart.saveFile("src/main/java/client/tmp/" + bodyPart.getFileName());
                    listFiles.add(new File("src/main/java/client/tmp/" + bodyPart.getFileName()));
                } catch (FileNotFoundException e) {
                    return getMessageNoAttachment(message);
                }
            } else {
                text = getTextFromMimeMultipart(multipart);
            }
        }
        if (message.getContentType().contains("text/plain")) {
            Object content = message.getContent();
            if (content != null) {
                text = getTextFromMimeMultipart(multipart);
            }
        }
        if (listFiles.size() == 0) {
            ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text);
            receivedMessage.setDate(message.getSentDate());
            return receivedMessage;
        } else {
            File[] array = listFiles.toArray(new File[0]);
            ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text, array);
            receivedMessage.setDate(message.getSentDate());
            listFiles.clear();
            return receivedMessage;
        }
    }

    static ReceivedMessage getMessageNoAttachment(MimeMessage cmsg) throws MessagingException, IOException {
        MimeMessage message = new MimeMessage(cmsg);
        Address[] fromAddress = message.getFrom();
        String email = fromAddress == null ? null : ((InternetAddress) fromAddress[0]).getAddress();
        String subject = message.getSubject();
        String text = html2text(message.getContent().toString());
       // System.out.println(text);
        ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text);
        receivedMessage.setDate(message.getSentDate());
        return receivedMessage;

    }



    private static String getTextFromMimeMultipart(Multipart mimeMultipart) throws MessagingException, IOException {
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

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}
