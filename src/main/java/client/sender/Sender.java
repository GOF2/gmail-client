package client.sender;

import client.authenticator.EmailAuthenticator;
import client.message.Message;
import client.utils.Host;
import client.utils.LoginChecker;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;


public class Sender extends LoginChecker implements ISender {
    private static Transport transport;

    @Override
    public void sendMessage(EmailAuthenticator authenticator, Message message) {
        boolean flag = LoginChecker.check(authenticator.getPasswordAuthentication().getUserName()
                , authenticator.getPasswordAuthentication().getPassword());
        if (flag) {

            Session session = Session.getDefaultInstance(Host.getSendProperties(), authenticator);
            MimeMessage mess = formMessage(message, session);
            try {
                transport = session.getTransport("smtps");
                Sender.transport.connect(Host.getSendProperties().getProperty("mail.smtp.host"),
                        authenticator.getPasswordAuthentication().getUserName(),
                        authenticator.getPasswordAuthentication().getPassword());
                Sender.transport.sendMessage(mess, mess.getAllRecipients());
                System.out.println("Mail Sent Successfully");
            } catch (SendFailedException sfe) {
                System.out.println(sfe);
            } catch (MessagingException e1) {
                e1.printStackTrace();
            }
        } else {
            System.out.println("Wrong email/password.Please check up");
        }

    }

    private InternetAddress[] adresses(Message message) {
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


    private MimeMessage formMessage(Message message, Session session) {
        MimeMessage mess = new MimeMessage(session);
        try {
            mess.setRecipients(MimeMessage.RecipientType.TO, adresses(message));
            mess.setSubject(message.getSubject());
            mess.setContent(multipart(message));
        } catch (MessagingException e1) {
            e1.printStackTrace();
        }
        return mess;
    }

    private Multipart multipart(Message message) {
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


    private BodyPart[] bodyParts(Message message) {
        BodyPart[] parts = new MimeBodyPart[message.getAttachment().length + 1];
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        MimeBodyPart attachBodyPart;
        try {
            messageBodyPart.setContent(message.getMessage(), "text/plain; charset=utf-8");
            parts[0] = messageBodyPart;
            if (message.getAttachment() == null) {
            } else {
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