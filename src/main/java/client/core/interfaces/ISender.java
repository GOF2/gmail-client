package client.core.interfaces;

import client.core.common.SendedMessage;
import client.core.interfaces.callbacks.MessageErrorCallback;
import client.core.interfaces.callbacks.SuccessCallback;
import com.sun.istack.internal.NotNull;

public interface ISender {
    void send(@NotNull SendedMessage message);
    void send(@NotNull SendedMessage message, @NotNull SendCallback callback);

    interface SendCallback extends SuccessCallback, MessageErrorCallback {
    }
}