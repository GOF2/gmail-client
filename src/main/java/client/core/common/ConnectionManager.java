package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.utils.Host;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

import javax.mail.*;

public class ConnectionManager {
    private static POP3Folder folder;

    static POP3Folder getFolder(EmailAuthenticator authenticator) {
        return createdFolder(authenticator);
    }

    private static POP3Folder createdFolder(EmailAuthenticator authenticator) {
        final Session session = Session.getInstance(Host.getReceiveProperties(), authenticator);
        try {
            final Store store = session.getStore(Host.getSendProperties().getProperty("mail.store.protocol"));
            final PasswordAuthentication authentication = authenticator.getPasswordAuthentication();
            store.connect(
                    Host.getReceiveProperties().getProperty("mail.pop3.host"),
                    authentication.getUserName(),
                    authentication.getPassword()
            );
            folder = (POP3Folder) store.getFolder("INBOX");
            if (folder.exists() && !folder.isOpen())
                folder.open(Folder.READ_WRITE);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return folder;
    }

    public static void closeFolder() {
        if(folder.isOpen()){
        try {
            folder.close(false);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }}
}
