package client.core.interfaces;

import client.core.common.ReceivedMessage;
import client.core.interfaces.callbacks.MessageErrorCallback;

import javax.validation.constraints.NotNull;
import java.util.Set;

public interface IReceiver {
    void receive(@NotNull ReceiveCallback callback);

    interface ReceiveCallback extends MessageErrorCallback {
        void onReceive(Set<ReceivedMessage> messages);
        void onUpdate(ReceivedMessage message);
    }
}