package client.core;

import client.authenticator.EmailAuthenticator;
import client.utils.Host;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class Receiver implements IReceiver {
    private static final Receiver receiver = new Receiver();

    public static Receiver getReceiver() {
        return receiver;
    }

    private Receiver() {
    }


    @Override
    public void startListening(IMAPFolder folder) {
        IMAPIDLE rec = new IMAPIDLE();
        rec.startListening(folder);
    }

    @Override
    public void stopListening() {

    }

    private Store receiveWithAuthentication(Session session, EmailAuthenticator authenticator) {
        Store store = null;
        try {
            store = session.getStore(Host.getReceiveProperties().getProperty("mail.imaps.protocol"));
            store.connect(Host.getReceiveProperties().getProperty("mail.imaps.host"),
                    authenticator.getPasswordAuthentication().getUserName(),
                    authenticator.getPasswordAuthentication().getPassword());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return store;


    }

    @Override
    public IMAPFolder firstReceive(EmailAuthenticator authenticator) {
        Folder emailfolder = null;
        Session emailSession = Session.getDefaultInstance(Host.getReceiveProperties());
        try {
            //emailfolder = firstReceive().getFolder("INBOX");
            emailfolder.open(Folder.READ_ONLY);
        } catch (MessagingException e1) {
            e1.printStackTrace();
        }
        return (IMAPFolder) emailfolder;
    }
}
