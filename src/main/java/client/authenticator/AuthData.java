package client.authenticator;


import javax.validation.constraints.NotNull;

public class AuthData {
    private final String login;
    private final String password;

    AuthData(@NotNull String login, @NotNull String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
