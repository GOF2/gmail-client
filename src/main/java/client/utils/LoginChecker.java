package client.utils;


import javax.mail.*;

public class LoginChecker {
    protected static boolean check(String email, String password,ErrorCallbacks errorCallbacks) {
        boolean flag = false;
        try {
            Session sessionSend = Session.getInstance(Host.getSendProperties());
            Transport trSend = sessionSend.getTransport("smtps");
            trSend.connect(Host.getSendProperties().getProperty("mail.smtp.host"), email, password);
            trSend.close();
            Session sessionRec = Session.getInstance(Host.getReceiveProperties());
            Store store = sessionRec.getStore("imaps");
            store.connect(Host.getReceiveProperties().getProperty("mail.imap.host"), email, password);
            store.close();
            flag = true;
        } catch (NoSuchProviderException np) {
            System.out.println("Wrong provider exception");
        } catch (AuthenticationFailedException e) {
            errorCallbacks.authenticationFailed();
        } catch (MessagingException e) {
            errorCallbacks.badInternetConnection();
        }
        return flag;
    }

}
