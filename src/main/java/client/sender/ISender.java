package client.sender;

import client.authenticator.EmailAuthenticator;
import client.message.Message;


public interface ISender {
    void sendMessage(EmailAuthenticator emailAuthenticator, Message message);

    void closeConnection();

}
