package client.sender;

import client.EmailAuthenticator;
import client.message.Message;
import client.utils.Host;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;


public class Sender extends Host implements ISender {
    @Override
    public void sendMessage(Authenticator authenticator, Message message) throws MessagingException {
    Session session = Session.getDefaultInstance(Host.getSendProperties(),authenticator);
        MimeMessage mess = new MimeMessage(session);
        mess.setRecipients(MimeMessage.RecipientType.TO,  adresses(message));
        mess.setSubject(message.getSubject());
        mess.setText(message.getMessage(),"utf-8");
        Multipart multipart = new MimeMultipart();
        for(int i = 0;i<message.getAttachment().length;i++){
            DataSource source = new FileDataSource(message.getAttachment()[i]);
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(message.getAttachment()[i].getName());
            multipart.addBodyPart(messageBodyPart);
        }
        mess.setContent(multipart);

    }
    private InternetAddress[] adresses(Message message) throws AddressException {
        InternetAddress[] adresses = new InternetAddress[message.getTo().length];
        for (int i =0;i<message.getTo().length;i++){
            adresses[i] = new InternetAddress(message.getTo()[i]);
        }
        return adresses;
    }

}
