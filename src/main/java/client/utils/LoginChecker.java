package client.utils;


import client.authenticator.AuthData;
import client.authenticator.EmailAuthenticator;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

public class LoginChecker {

    public static void check(String email, String password) throws MessagingException {
        final Session sessionSend = Session.getInstance(Host.getSendProperties());
        final Transport trSend = sessionSend.getTransport("smtps");
        trSend.connect(Host.getSendProperties().getProperty("mail.smtp.host"), email, password);
        trSend.close();
        final Store store = Session.getInstance(Host.getReceiveProperties()).getStore("imaps");
        store.connect(Host.getReceiveProperties().getProperty("mail.imap.host"), email, password);
        store.close();
        // Looks strange, but connect(...) method throw only a MessagingException
    }

    public static void check(EmailAuthenticator emailAuthenticator) throws MessagingException {
        final AuthData data = emailAuthenticator.getAuthData();
        final String email = data.getLogin();
        final String password = data.getPassword();
        check(email, password);
    }
}
