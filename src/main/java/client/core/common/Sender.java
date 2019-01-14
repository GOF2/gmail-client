package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.exceptions.NoInternetException;
import client.utils.Host;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class Sender {
    private Transport transport;
    private EmailAuthenticator authenticator;
    private static Sender sender;

    private Sender(EmailAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public static Sender getInstance(EmailAuthenticator authenticator) {
        if (sender == null) {
            return new Sender(authenticator);
        }
        return sender;
    }

    public void setAuthenticator(EmailAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void send(SendedMessage message) throws NoSuchProviderException, NoInternetException, SendFailedException {
        final Session session = Session.getInstance(Host.getSendProperties(), authenticator);
        final MimeMessage mess = formMessage(authenticator, message, session);
        transport = session.getTransport(Host.getSendProperties().getProperty("mail.smtp.protocol"));
        try {
            final PasswordAuthentication authentication = authenticator.getPasswordAuthentication();
            transport.connect(
                    Host.getSendProperties().getProperty("mail.smtp.host"),
                    authentication.getUserName(),
                    authentication.getPassword()
            );
            transport.sendMessage(mess, mess.getAllRecipients());
        } catch (SendFailedException | NoInternetException e) {
            // It also looks strange...
            throw e;
        } catch (MessagingException e) {
            e.printStackTrace();
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

}