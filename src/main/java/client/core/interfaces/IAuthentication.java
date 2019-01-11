package client.core.interfaces;

import client.authenticator.AuthData;
import client.core.interfaces.callbacks.GenericSuccessCallback;
import client.core.interfaces.callbacks.MessageErrorCallback;
import com.sun.istack.internal.NotNull;

public interface IAuthentication {
    void auth(@NotNull AuthData authData);
    void auth(@NotNull AuthData authData, @NotNull AuthCallback callback);

    public interface AuthCallback<T> extends GenericSuccessCallback<T>, MessageErrorCallback {
    }
}
