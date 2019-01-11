package client.core;

import client.authenticator.AuthData;
import client.authenticator.EmailAuthenticator;
import client.core.interfaces.IAuthentication;
import client.core.interfaces.callbacks.*;
import com.sun.istack.internal.NotNull;

import javax.mail.MessagingException;

public abstract class LoginRequiredClient {
    private AuthData authData;
    private EmailAuthenticator authenticator;

    private IAuthentication.AuthCallback authCallback;
    private Callback beforeLoginCallback;
    private SuccessCallback successLoginCallback;
    private MessageErrorCallback errorLoginCallback;

    public void setAuthCallback(IAuthentication.AuthCallback authCallback) {
        this.authCallback = authCallback;
    }
    public void setErrorLoginCallback(MessageErrorCallback errorLoginCallback) {
        this.errorLoginCallback = errorLoginCallback;
    }
    public void setSuccessLoginCallback(SuccessCallback successLoginCallback) {
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

    public IAuthentication.AuthCallback getAuthCallback() {
        return authCallback;
    }
    public Callback getBeforeLoginCallback() {
        return beforeLoginCallback;
    }
    public SuccessCallback getSuccessLoginCallback() {
        return successLoginCallback;
    }
    public MessageErrorCallback getErrorLoginCallback() {
        return errorLoginCallback;
    }

    public <T extends LoginRequiredClient> T beforeLogin(Callback callback) {
        System.out.println("Before login");
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
    public <T extends LoginRequiredClient> T onLoginSuccess(SuccessCallback callback) {
        return thisReference(() -> setSuccessLoginCallback(callback));
    }

    @FunctionalInterface
    public interface Function { void call(); }

    @SuppressWarnings("unchecked")
    protected <T extends LoginRequiredClient> T thisReference(Function function) {
        function.call();
        return (T) this;
    }

    public void callIfNotNull(Object o, Function function) {
        if (o != null)
            function.call();
    }

    protected <T extends LoginRequiredClient> LoginCallback<T, MessagingException> getLoginCallbacks() {
        return new LoginCallback<T, MessagingException>() {
            @Override public void beforeLogin() {
                final Callback callback = LoginRequiredClient.this.beforeLoginCallback;
                callIfNotNull(callback, callback::call);
            }
            @Override public void onLoginError(MessagingException e) {
                final MessageErrorCallback callback = LoginRequiredClient.this.errorLoginCallback;
                callIfNotNull(callback, () -> callback.onError(e));
            }

            @Override public void onSuccessLogin(T t) {
                final SuccessCallback callback = LoginRequiredClient.this.successLoginCallback;
                callIfNotNull(callback, callback::onSuccess);
            }
        };
    }
}
