package client.core;

import client.authenticator.EmailAuthenticator;
import client.utils.Host;
import client.utils.LoginChecker;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;


public class Sender extends LoginChecker implements ISender {
    private static Transport transport;
    private static final Sender sender = new Sender();

    public static Sender getSender() {
        return sender;
    }

    private Sender() {
    }

    @Override
    public void sendMessage(EmailAuthenticator authenticator, SendedMessage message) {
        Session session = Session.getDefaultInstance(Host.getSendProperties(), authenticator);
        MimeMessage mess = formMessage(authenticator, message, session);
        try {
            transport = session.getTransport("smtps");
            Sender.transport.connect(Host.getSendProperties().getProperty("mail.smtp.host"),
                    authenticator.getPasswordAuthentication().getUserName(),
                    authenticator.getPasswordAuthentication().getPassword());
            Sender.transport.sendMessage(mess, mess.getAllRecipients());
            System.out.println("Mail Sent Successfully");
        } catch (SendFailedException sfe) {

        } catch (MessagingException e1) {
            e1.printStackTrace();
        }
    }


    private InternetAddress[] adresses(SendedMessage message) {
        InternetAddress[] addresses = new InternetAddress[message.getTo().length];
        try {
            for (int i = 0; i < message.getTo().length; i++) {
                addresses[i] = new InternetAddress(message.getTo()[i]);
            }
        } catch (AddressException ae) {
            System.out.println("Wrong address");
        }
        return addresses;
    }


    private MimeMessage formMessage(EmailAuthenticator authenticator, SendedMessage message, Session session) {
        MimeMessage mess = new MimeMessage(session);
        try {
            mess.setRecipients(MimeMessage.RecipientType.TO, adresses(message));
            mess.setSubject(message.getSubject());
            mess.setContent(multipart(message));
            mess.setFrom(new InternetAddress(
                    authenticator.getPasswordAuthentication().getUserName(), message.getFrom()));
            mess.setSentDate(new Date());
            message.setDate(mess.getSentDate());
        } catch (MessagingException | UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return mess;
    }

    private Multipart multipart(SendedMessage message) {
        Multipart multipart = new MimeMultipart("mixed");
        try {
            for (BodyPart body : bodyParts(message)) {
                multipart.addBodyPart(body);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return multipart;

    }


    private BodyPart[] bodyParts(SendedMessage message) {
        BodyPart[] parts = new MimeBodyPart[1];
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        MimeBodyPart attachBodyPart;
        try {
            messageBodyPart.setContent(message.getMessage(), "text/plain; charset=utf-8");
            parts[0] = messageBodyPart;
            if (message.getAttachment() == null) {
                return parts;
            } else {
                parts = new MimeBodyPart[message.getAttachment().length + 1];
                parts[0] = messageBodyPart;
                for (int i = 0; i < message.getAttachment().length; i++) {
                    DataSource source = new FileDataSource(message.getAttachment()[i]);
                    attachBodyPart = new MimeBodyPart();
                    try {
                        attachBodyPart.setDataHandler(new DataHandler(source));
                        attachBodyPart.setFileName(message.getAttachment()[i].getName());
                        parts[i + 1] = attachBodyPart;
                    } catch (MessagingException me) {
                        me.printStackTrace();
                    }
                }
            }
        } catch (MessagingException ae) {
            ae.printStackTrace();
        }
        return parts;
    }

    @Override
    public void closeConnection() {
        try {
            transport.close();
        } catch (MessagingException ignored) {
        }
    }

}