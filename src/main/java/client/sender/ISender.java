package client.sender;

import client.EmailAuthenticator;
import client.message.Message;


public interface ISender {
    void sendMessage(EmailAuthenticator emailAuthenticator, Message message);

}
