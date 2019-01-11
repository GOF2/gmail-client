package client.core.interfaces;

import client.core.common.ReceivedMessage;
import client.core.interfaces.callbacks.MessageErrorCallback;
import com.sun.istack.internal.NotNull;

public interface IReceiver {
    void receive(@NotNull ReceiveCallback callback);

    public interface ReceiveCallback extends MessageErrorCallback {
        void onReceive(ReceivedMessage message);
    }
}