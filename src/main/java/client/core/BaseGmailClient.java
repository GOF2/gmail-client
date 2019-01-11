package client.core;

import client.authenticator.AuthData;
import client.authenticator.EmailAuthenticator;
import client.core.common.SendedMessage;
import client.core.exceptions.NoInternetException;
import client.core.interfaces.IReceiver;
import client.core.interfaces.ISender;
import client.core.interfaces.callbacks.LoginCallback;
import client.core.interfaces.callbacks.MessageErrorCallback;
import client.core.interfaces.callbacks.SuccessCallback;
import client.utils.LoginChecker;
import com.sun.istack.internal.NotNull;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;


public class BaseGmailClient extends LoginRequiredClient implements MailAPI {

    public BaseGmailClient() {
    }

    public BaseGmailClient(@NotNull EmailAuthenticator authenticator) {
        setAuthenticator(authenticator);
    }

    @Override
    public void auth(AuthData authData) {
        final LoginCallback<LoginRequiredClient, MessagingException> callbacks = getLoginCallbacks();
        callIfNotNull(callbacks, callbacks::beforeLogin);
        try {
            LoginChecker.check(getAuthenticator());
        } catch (NoSuchProviderException | NoInternetException | AuthenticationFailedException e) {
            callIfNotNull(callbacks, () -> callbacks.onLoginError(e));
        }
        callbacks.onSuccessLogin(this);
    }

    @Override
    public void auth(AuthData authData, AuthCallback callback) {
        callIfNotNull(callback, () -> getBeforeLoginCallback().call());
        try {
            LoginChecker.check(getAuthenticator());
        } catch (NoSuchProviderException | NoInternetException | AuthenticationFailedException e) {
            callIfNotNull(callback, () -> callback.onError(e));
        }
        callIfNotNull(callback, callback::onSuccess);
    }

    public <T extends BaseGmailClient> T auth() {
        return thisReference(() -> auth(getAuthData()));
    }

    public <T extends BaseGmailClient> T auth(AuthCallback callback) {
        return thisReference(() -> auth(getAuthData(), callback));
    }

    @Override
    public void send(SendedMessage message) {
        // TODO: 11.01.19
    }

    public void send(SendedMessage message, ISender.SendCallback callback) {
        // TODO: 11.01.19
    }

    public void send(SendedMessage message, SuccessCallback successCallback, MessageErrorCallback errorCallback) {
        // TODO: 10.01.19
    }

    @Override
    public void receive(IReceiver.ReceiveCallback callback) {
        // TODO: 11.01.19
    }
}
