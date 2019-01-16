package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.interfaces.IReceiver;
import com.sun.mail.imap.IMAPFolder;

public abstract class BaseReceiver {
    private EmailAuthenticator authenticator;

    BaseReceiver(EmailAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setAuthenticator(EmailAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    IMAPFolder getFolder() {
        return ConnectionManager.getFolder(authenticator);
    }

    public void closeFolder() {
        ConnectionManager.close();
    }

    public EmailAuthenticator getAuthenticator() {
        return authenticator;
    }

    public abstract void handleReceiving(IReceiver.ReceiveCallback callback);
}
