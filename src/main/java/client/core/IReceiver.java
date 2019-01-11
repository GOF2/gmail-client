package client.core;

import client.authenticator.EmailAuthenticator;
import com.sun.mail.imap.IMAPFolder;

public interface IReceiver {
    void startListening(IMAPFolder folder);

    IMAPFolder firstReceive(EmailAuthenticator authenticator);

    void stopListening();
}
