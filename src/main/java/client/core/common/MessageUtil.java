package client.core.common;

import client.core.interfaces.callbacks.Function;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageUtil {

    static Set<ReceivedMessage> buildMessages(MimeMessage[] messages) {
        return Stream.of(messages)
                .parallel()
                .map(MessageUtil::mimeToReceived)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private static ReceivedMessage mimeToReceived(MimeMessage message) {
        try {
            final String email = formatAddress(message.getFrom());
            final String subject = message.getSubject();
            final String text = getText(message);
            final Date date = message.getSentDate();
            final List<File> files = getAttachments(message);
            if (files == null || files.size() == 0) {
                return getReceivedMessage(email, subject, text, date);
            } else {
                return getReceivedMessage(email, subject, text, date, files);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    private static ReceivedMessage getReceivedMessage(String email, String subject, String text, Date date, List<File> files) {
        final File[] fileArray = files.stream().toArray(File[]::new);
        final ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text, fileArray);
        receivedMessage.setDate(date);
        return receivedMessage;
    }

    @NotNull
    private static ReceivedMessage getReceivedMessage(String email, String subject, String text, Date date) {
        final ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text);
        receivedMessage.setDate(date);
        return receivedMessage;
    }

    private static String formatAddress(Address[] fromAddress) {
        return fromAddress == null ? null : ((InternetAddress) fromAddress[0]).getAddress();
    }

    private static String getText(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
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


    public static List<File> getAttachments(MimeMessage message) throws Exception {
        Object content = message.getContent();
        if (content instanceof String)
            return null;
        else {
            final List<File> listFiles = new ArrayList<>();
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    MimeBodyPart bodyPart = (MimeBodyPart) multipart.getBodyPart(i);
                    try {
                        String fileName = "src/main/java/client/tmp/" + bodyPart.getFileName();
                        bodyPart.saveFile(fileName);
                        listFiles.add(new File(fileName));
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                }
            }
            return listFiles;
        }
    }

    public static void profile(String text, Function function) {
        long start = System.currentTimeMillis();
        function.call();
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("Method '" + text + "' => time: " + elapsedTime / 1_000 + " sec.");
    }
}