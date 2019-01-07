package client;

import client.utils.Host;

import javax.mail.*;

public class LoginChecker extends Host {
    protected static boolean check(String email, String password) {
        boolean flag = false;
        try {
            Session sessionSend = Session.getInstance(Host.getSendProperties());
            Transport trSend = sessionSend.getTransport("smtps");
            trSend.connect(Host.getSendProperties().getProperty("mail.smtp.host"), email, password);
            trSend.close();
            Session sessionRec = Session.getInstance(Host.getSendProperties());
            Store store = sessionRec.getStore("imaps");
            store.connect(Host.getReceiveProperties().getProperty("mail.imap.host"), email, password);
            store.close();
            flag = true;
        } catch (NoSuchProviderException np) {
            System.out.println("Wrong provider exception");
        } catch (AuthenticationFailedException e) {
            System.out.println("Wrong login/password");
        } catch (MessagingException e) {
            System.out.println("Message error");
        }
        return flag;
    }

}
