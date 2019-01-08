package client;

import client.authenticator.EmailAuthenticator;
import client.message.Message;
import client.sender.Sender;
import client.utils.LoginChecker;



public class GmailClient extends LoginChecker {
    private EmailAuthenticator authenticator;

    public GmailClient(EmailAuthenticator authenticator) {
        LoginChecker.check(authenticator.getPasswordAuthentication().getUserName(),
                authenticator.getPasswordAuthentication().getPassword());
        this.authenticator = authenticator;
    }

    public void send(Message message) {
        Sender.getSender().sendMessage(authenticator, message);
    }

    public void closeConnection() {
        Sender.getSender().closeConnection();
        //Receiver.getReceiver.closeConnection();
    }

}
