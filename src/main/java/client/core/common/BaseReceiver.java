package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.interfaces.IReceiver;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.MessagingException;

public abstract class BaseReceiver {
    private EmailAuthenticator authenticator;

    BaseReceiver(EmailAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setAuthenticator(EmailAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    IMAPFolder getFolder() throws MessagingException {
        return ConnectionManager.getFolder(authenticator);
    }

    public void closeFolder() throws MessagingException {
        ConnectionManager.close();
    }

    public EmailAuthenticator getAuthenticator() {
        return authenticator;
    }

    public abstract void handleReceiving(IReceiver.ReceiveCallback callback);
}
