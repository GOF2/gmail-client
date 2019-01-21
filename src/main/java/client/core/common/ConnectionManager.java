package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.utils.Host;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;

class ConnectionManager {
    private static IMAPFolder folder;

    static IMAPFolder getFolder(EmailAuthenticator authenticator) throws MessagingException {
        if (folder == null) {
            return createdFolder(authenticator);
        }
        return folder;
    }

    private static IMAPFolder createdFolder(EmailAuthenticator authenticator) throws MessagingException {
        final Session session = Session.getInstance(Host.getReceiveProperties(), authenticator);
        final Store store = session.getStore(Host.getSendProperties().getProperty("mail.imaps.protocol"));
        final PasswordAuthentication authentication = authenticator.getPasswordAuthentication();
        store.connect(
                Host.getReceiveProperties().getProperty("mail.imap.host"),
                authentication.getUserName(),
                authentication.getPassword()
        );
        folder = (IMAPFolder) store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        return folder;
    }

    static void close() throws MessagingException {
        folder.close(false);
    }
}
