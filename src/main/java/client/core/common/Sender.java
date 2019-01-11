package client.core.common;

public class Sender {
//    private Transport transport;
//    private PasswordAuthentication passwordAuthentication;
//    private EmailAuthenticator authenticator;
//
//    public Sender(EmailAuthenticator emailAuthenticator) {
//        this.authenticator = emailAuthenticator;
//        this.passwordAuthentication = emailAuthenticator.getPasswordAuthentication();
//    }
//
//    public void send(SendedMessage message) throws NoSuchProviderException, SendFailedException, NoInternetException {
//        Session session = Session.getDefaultInstance(Host.getSendProperties(), authenticator);
//        MimeMessage mess = formMessage(passwordAuthentication, message, session);
//        transport = session.getTransport("smtps");
//        try {
//            transport.connect(
//                    Host.getSendProperties().getProperty("mail.smtp.host"),
//                    passwordAuthentication.getUserName(),
//                    passwordAuthentication.getPassword()
//            );
//            transport.sendMessage(mess, mess.getAllRecipients());
//            System.out.println("Mail Sent Successfully");
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private InternetAddress[] adresses(SendedMessage message) {
//        InternetAddress[] addresses = new InternetAddress[message.getTo().length];
//        try {
//            for (int i = 0; i < message.getTo().length; i++) {
//                addresses[i] = new InternetAddress(message.getTo()[i]);
//            }
//        } catch (AddressException ae) {
//            System.out.println("Wrong address");
//        }
//        return addresses;
//    }
//
//
//    private MimeMessage formMessage(PasswordAuthentication authenticator, SendedMessage message, Session session) {
//        MimeMessage mess = new MimeMessage(session);
//        try {
//            mess.setRecipients(MimeMessage.RecipientType.TO, adresses(message));
//            mess.setSubject(message.getSubject());
//            mess.setContent(multipart(message));
//            mess.setFrom(new InternetAddress(authenticator.getUserName(), message.getFrom()));
//            mess.setSentDate(new Date());
//            message.setDate(mess.getSentDate());
//        } catch (MessagingException | UnsupportedEncodingException e1) {
//            e1.printStackTrace();
//        }
//        return mess;
//    }
//
//    private Multipart multipart(SendedMessage message) {
//        Multipart multipart = new MimeMultipart("mixed");
//        try {
//            for (BodyPart body : bodyParts(message)) {
//                multipart.addBodyPart(body);
//            }
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        return multipart;
//
//    }
//
//
//    private BodyPart[] bodyParts(SendedMessage message) {
//        BodyPart[] parts = new MimeBodyPart[1];
//        MimeBodyPart messageBodyPart = new MimeBodyPart();
//        MimeBodyPart attachBodyPart;
//        try {
//            messageBodyPart.setContent(message.getMessage(), "text/plain; charset=utf-8");
//            parts[0] = messageBodyPart;
//            if (message.getAttachment() == null) {
//                return parts;
//            } else {
//                parts = new MimeBodyPart[message.getAttachment().length + 1];
//                parts[0] = messageBodyPart;
//                for (int i = 0; i < message.getAttachment().length; i++) {
//                    DataSource source = new FileDataSource(message.getAttachment()[i]);
//                    attachBodyPart = new MimeBodyPart();
//                    try {
//                        attachBodyPart.setDataHandler(new DataHandler(source));
//                        attachBodyPart.setFileName(message.getAttachment()[i].getName());
//                        parts[i + 1] = attachBodyPart;
//                    } catch (MessagingException me) {
//                        me.printStackTrace();
//                    }
//                }
//            }
//        } catch (MessagingException ae) {
//            ae.printStackTrace();
//        }
//        return parts;
//    }
//
//    public void closeConnection() throws MessagingException {
//        transport.close();
//    }
}