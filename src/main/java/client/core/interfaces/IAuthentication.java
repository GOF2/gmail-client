package client.core.interfaces;

import client.authenticator.AuthData;
import client.core.interfaces.callbacks.MessageErrorCallback;
import client.core.interfaces.callbacks.SuccessCallback;

import javax.validation.constraints.NotNull;

public interface IAuthentication {
    void auth(@NotNull AuthData authData);
    void auth(@NotNull AuthData authData, @NotNull AuthCallback callback);

    interface AuthCallback extends SuccessCallback, MessageErrorCallback {
    }
}
