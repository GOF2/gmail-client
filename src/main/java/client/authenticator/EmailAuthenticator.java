package client.authenticator;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public final class EmailAuthenticator extends Authenticator {

    private final String login;
    private final String password;

    public EmailAuthenticator(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public final PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(login, password);
    }

}
