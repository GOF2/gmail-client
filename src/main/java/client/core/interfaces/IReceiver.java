package client.core.interfaces;

import client.core.common.ReceivedMessage;
import client.core.interfaces.callbacks.MessageErrorCallback;
import com.sun.istack.internal.NotNull;

import java.util.List;

public interface IReceiver {
    void receive(@NotNull ReceiveCallback callback);

    interface ReceiveCallback extends MessageErrorCallback {
        void onReceive(List<ReceivedMessage> messages);
        void onUpdate(ReceivedMessage message);
    }
}