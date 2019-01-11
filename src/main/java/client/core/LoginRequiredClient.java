package client.core;

import client.authenticator.AuthData;
import client.authenticator.EmailAuthenticator;
import client.core.interfaces.IAuthentication;
import client.core.interfaces.callbacks.Callback;
import client.core.interfaces.callbacks.GenericSuccessCallback;
import client.core.interfaces.callbacks.MessageErrorCallback;
import com.sun.istack.internal.NotNull;

public abstract class LoginRequiredClient {
    private AuthData authData;
    private EmailAuthenticator authenticator;

    private IAuthentication.AuthCallback authCallback;
    private Callback beforeLoginCallback;
    private GenericSuccessCallback<? extends LoginRequiredClient> successLoginCallback;
    private MessageErrorCallback errorLoginCallback;

    public void setAuthCallback(IAuthentication.AuthCallback authCallback) {
        this.authCallback = authCallback;
    }
    public void setErrorLoginCallback(MessageErrorCallback errorLoginCallback) {
        this.errorLoginCallback = errorLoginCallback;
    }
    public void setSuccessLoginCallback(GenericSuccessCallback<? extends LoginRequiredClient> successLoginCallback) {
        this.successLoginCallback = successLoginCallback;
    }
    public void setBeforeLoginCallback(Callback beforeLoginCallback) {
        this.beforeLoginCallback = beforeLoginCallback;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }
    public AuthData getAuthData() {
        return authData;
    }
    public EmailAuthenticator getAuthenticator() {
        return authenticator;
    }
    public void setAuthenticator(EmailAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public <T extends LoginRequiredClient> T beforeLogin(Callback callback) {
        System.out.println("Before loginWith");
        return thisReference(() -> setBeforeLoginCallback(callback));
    }
    public <T extends LoginRequiredClient> T loginWith(@NotNull EmailAuthenticator emailAuthenticator) {
        final String login = emailAuthenticator.getAuthData().getLogin();
        final String password = Integer.toHexString(emailAuthenticator.getAuthData().getPassword().hashCode());
        System.out.println("Login with: [" + login + ", " + password + "]");
        return thisReference(() -> setAuthenticator(emailAuthenticator));
    }
    public <T extends LoginRequiredClient> T loginWith(@NotNull AuthData authData) {
        final String login = authData.getLogin();
        final String password = Integer.toHexString(authData.getPassword().hashCode());
        System.out.println("Login with: [" + login + ", " + password + "]");
        return thisReference(() -> setAuthData(authData));
    }
    public <T extends LoginRequiredClient> T onLogin(IAuthentication.AuthCallback callback) {
        return thisReference(() -> setAuthCallback(callback));
    }
    public <T extends LoginRequiredClient> T onLoginError(MessageErrorCallback callback) {
        return thisReference(() -> setErrorLoginCallback(callback));
    }
    public <T extends LoginRequiredClient> T onLoginSuccess(GenericSuccessCallback<T> callback) {
        return thisReference(() -> setSuccessLoginCallback(callback));
    }

    @FunctionalInterface
    protected interface BuilderFunction { void call(); }

    @SuppressWarnings("unchecked")
    protected <T extends LoginRequiredClient> T thisReference(BuilderFunction function) {
        function.call();
        return (T) this;
    }
}
