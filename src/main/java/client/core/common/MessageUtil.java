package client.core.common;

import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class MessageUtil {
    static Set<ReceivedMessage> buildMessages(MimeMessage[] messages) {
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
                            File f = new File("src/main/java/client/tmp/" + bodyPart.getFileName());
                            f.createNewFile();
                            FileOutputStream fos = new FileOutputStream(f, false);
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
                    ReceivedMessage receivedMessage = new ReceivedMessage(email, subject, text, array);
                    receivedMessage.setDate(cmsg.getSentDate());
                    // TODO: 12.01.19
                    receivedMessageSet.add(receivedMessage);
                    listFiles.clear();
                }
            }

        } catch (MessagingException ignored) {

        } catch (IOException e) {

        }
        return receivedMessageSet;
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

}
