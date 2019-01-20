package client.utils;


import client.authenticator.AuthData;
import client.authenticator.EmailAuthenticator;
import client.core.common.WaitingThread;
import client.core.exceptions.NoInternetException;

import javax.mail.*;

public class LoginChecker {

    public static void check(String email, String password) throws NoSuchProviderException, NoInternetException, AuthenticationFailedException {
        final Session sessionSend = Session.getInstance(Host.getSendProperties());
        final Transport trSend = sessionSend.getTransport("smtps");
        try {
            trSend.connect(Host.getSendProperties().getProperty("mail.smtp.host"), email, password);
            trSend.close();
            final Store store = Session.getInstance(Host.getReceiveProperties()).getStore("imaps");
            store.connect(Host.getReceiveProperties().getProperty("mail.imap.host"), email, password);
            store.close();
        } catch (AuthenticationFailedException | NoInternetException e ) {
            // Looks strange, but connect(...) method throw only a MessagingException
            throw e;
        } catch (MessagingException e) {
          e.printStackTrace();
        }
    }

    public static void check(EmailAuthenticator emailAuthenticator) throws NoSuchProviderException, NoInternetException, AuthenticationFailedException {
        final AuthData data = emailAuthenticator.getAuthData();
        final String email = data.getLogin();
        final String password = data.getPassword();
        check(email, password);
    }
}
