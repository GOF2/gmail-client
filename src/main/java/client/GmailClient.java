package client;

import client.authenticator.EmailAuthenticator;
import client.message.Message;
import client.sender.Sender;
import client.utils.ErrorCallbacks;
import client.utils.LoginChecker;



public class GmailClient extends LoginChecker {

    private static  GmailClient client;
    private ErrorCallbacks errorCallbacks;
    private EmailAuthenticator authenticator;
    public static GmailClient getClient(EmailAuthenticator authenticator,ErrorCallbacks errorCallbacks){
        if (client==null){
        return new GmailClient(authenticator,errorCallbacks);}
        return client;
    }
    private GmailClient(EmailAuthenticator authenticator,ErrorCallbacks errorCallbacks) {
        LoginChecker.check(authenticator.getPasswordAuthentication().getUserName(),
                authenticator.getPasswordAuthentication().getPassword(),errorCallbacks);
        this.authenticator = authenticator;
    }

    public void setErrorCallbacks(ErrorCallbacks errorCallbacks) {
        this.errorCallbacks = errorCallbacks;
    }

    public void send(Message message) {
        Sender.getSender().sendMessage(authenticator, message);
    }

    public void closeConnection() {
        Sender.getSender().closeConnection();
        //Receiver.getReceiver.closeConnection();
    }

}
