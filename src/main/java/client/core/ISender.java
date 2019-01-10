package client.core;

import client.authenticator.EmailAuthenticator;


public interface ISender {
    void sendMessage(EmailAuthenticator emailAuthenticator, SendedMessage message);

    void closeConnection();

}
