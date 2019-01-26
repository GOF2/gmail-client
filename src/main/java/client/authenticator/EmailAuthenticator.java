package client.authenticator;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public final class EmailAuthenticator extends Authenticator {
    private final AuthData authData;
    private boolean dataCorrect;

    private EmailAuthenticator(AuthData authData) {
        this.authData = authData;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public boolean isDataCorrect() {
        return dataCorrect;
    }

    public void setDataCorrect(boolean dataCorrect) {
        this.dataCorrect = dataCorrect;
    }

    @Override
    public final PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(authData.getLogin(), authData.getPassword());
    }

    public static class Gmail {
        private Gmail(){
            throw new IllegalStateException("Gmail Utility class");
        }
        public static EmailAuthenticator auth(String login, String password) {
            return new EmailAuthenticator(new AuthData(login, password));
        }

        public static PasswordAuthentication getPasswordAuthentication(String login, String password) {
            return auth(login, password).getPasswordAuthentication();
        }
    }
}
