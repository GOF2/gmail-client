package client.core;

import client.authenticator.AuthData;
import client.authenticator.EmailAuthenticator;
import client.core.common.IDLE;
import client.core.common.Receiver;
import client.core.common.SendedMessage;
import client.core.common.Sender;
import client.core.interfaces.IReceiver;
import client.core.interfaces.ISender;
import client.core.interfaces.MailAPI;
import client.core.interfaces.callbacks.*;
import client.utils.LoginChecker;
import com.sun.istack.internal.NotNull;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.SendFailedException;

import static client.utils.ActionUtil.callIfNotNull;


public class BaseGmailClient extends LoginRequiredClient implements MailAPI {
    private int attemptsCount = 0;

    BaseGmailClient() {
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
            successAuth(callbacks);
        } catch (NoSuchProviderException | AuthenticationFailedException e) {
            errorAuth(callbacks, e);
        } catch (MessagingException e) {
            tryAgain(() -> auth(authData));
        }
    }


    @Override
    public void auth(AuthData authData, AuthCallback callback) {
        callIfNotNull(callback, () -> getBeforeLoginCallback().call());
        try {
            LoginChecker.check(getAuthenticator());
            successAuth(callback);
        } catch (MessagingException e) {
            errorAuth(callback, e);
        }
    }

    @Override
    public <T extends LoginRequiredClient> T auth() {
        return thisReference(() -> auth(getAuthData()));
    }

    public <T extends BaseGmailClient> T auth(AuthCallback callback) {
        return thisReference(() -> auth(getAuthData(), callback));
    }

    @Override
    public void send(SendedMessage message) {
        final Sender sender = Sender.getInstance(getAuthenticator());
        try {
            if (getAuthenticator().isDataCorrect()) {
                sender.send(message);
            } else {
                throw new AuthenticationFailedException("...");
            }
        } catch (NoSuchProviderException | AuthenticationFailedException | SendFailedException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            tryAgain(() -> send(message));
        }
    }

    @Override
    public void send(SendedMessage message, ISender.SendCallback callback) {
        final Sender sender = Sender.getInstance(getAuthenticator());
        try {
            if (getAuthenticator().isDataCorrect()) {
                sender.send(message);
                callIfNotNull(callback, callback::onSuccess);
            } else {
                throw new AuthenticationFailedException("Authetnication Failed...");
            }
        } catch (NoSuchProviderException | AuthenticationFailedException | SendFailedException e) {
            callback.onError(e);
        } catch (MessagingException e) {
            tryAgain(() -> send(message, callback));
        }
        attemptsCount = 0;
    }

    public void send(SendedMessage message, SuccessCallback successCallback, MessageErrorCallback errorCallback) {
        send(message, new SendCallback() {
            @Override
            public void onSuccess() {
                successCallback.onSuccess();
            }

            @Override
            public void onError(MessagingException e) {
                errorCallback.onError(e);
            }
        });
    }

    @Override
    public void receive(IReceiver.ReceiveCallback callback) {
        final EmailAuthenticator authenticator = getAuthenticator();
        if (authenticator.isDataCorrect()) {
            Receiver.getInstance(authenticator).handleReceiving(callback);
        }
    }

    private void errorAuth(LoginCallback<LoginRequiredClient, MessagingException> callbacks, MessagingException e) {
        callIfNotNull(callbacks, () -> callbacks.onLoginError(e));
        getAuthenticator().setDataCorrect(false);
    }

    private void successAuth(LoginCallback<LoginRequiredClient, MessagingException> callbacks) {
        callIfNotNull(callbacks, () -> callbacks.onSuccessLogin(this));
        getAuthenticator().setDataCorrect(true);
    }

    private void errorAuth(AuthCallback callback, MessagingException e) {
        callIfNotNull(callback, () -> callback.onError(e));
        getAuthenticator().setDataCorrect(false);
    }

    private void successAuth(AuthCallback callback) {
        callIfNotNull(callback, callback::onSuccess);
        getAuthenticator().setDataCorrect(true);
        attemptsCount = 0;
    }

    private void tryAgain(Function function) {
        if (attemptsCount < getAttempts()) {
            try {
                Thread.sleep(getDelayTime());
                attemptsCount++;
                function.call();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
