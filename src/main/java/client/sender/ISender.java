package client.sender;

import client.message.Message;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;


public interface ISender {
    void sendMessage(Authenticator emailAuthenticator, Message message) throws MessagingException;
}
