package client.core.common;

import com.google.common.io.Files;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static client.utils.StringsUtil.isNotBlank;

class MessageUtil {

    static Set<ReceivedMessage> buildMessages(MimeMessage[] messages) {
        return Stream.of(messages)
                .parallel()
                .map(MessageUtil::mimeToNormal)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private static ReceivedMessage mimeToNormal(MimeMessage message) {
        try {
            final String email = formatAddress(message.getFrom());
            final String subject = message.getSubject();
            final String text = getText(message);
            final Date date = message.getSentDate();
            final List<File> files = getAttachments(message);
            if (files == null) {
                return getReceivedMessage(email, subject, text, date);
            }
            else {
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

    private static File saveFile(InputStream in, String name) throws IOException {
        new File("src/main/java/client/tmp/").mkdirs();
        final File file = new File("src/main/java/client/tmp/" + name);
        final byte[] buffer = new byte[in.available()];
        Files.write(buffer, file);
        return file;
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


    public static List<File> getAttachments(Message message) throws Exception {
        Object content = message.getContent();
        if (content instanceof String)
            return null;
        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            List<File> result = new ArrayList<>();
            for (int i = 0; i < multipart.getCount(); i++) {
                final BodyPart bodyPart = multipart.getBodyPart(i);
                for (InputStream stream : getAttachments(bodyPart)) {
                    final File file = saveFile(stream, bodyPart.getFileName());
                    result.add(file);
                }
            }
            return result;
        }
        return null;
    }

    private static List<InputStream> getAttachments(BodyPart part) throws Exception {
        List<InputStream> result = new ArrayList<>();
        Object content = part.getContent();
        if (content instanceof InputStream || content instanceof String) {
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || isNotBlank(part.getFileName())) {
                result.add(part.getInputStream());
                return result;
            } else {
                return new ArrayList<>();
            }
        }

        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                result.addAll(getAttachments(bodyPart));
            }
        }
        return result;
    }
}
