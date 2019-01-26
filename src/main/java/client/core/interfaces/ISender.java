package client.core.interfaces;

import client.core.common.SendedMessage;
import client.core.interfaces.callbacks.MessageErrorCallback;
import client.core.interfaces.callbacks.SuccessCallback;

import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;

public interface ISender {
    void send(@NotNull SendedMessage message) throws MessagingException;
    void send(@NotNull SendedMessage message, @NotNull SendCallback callback);

    interface SendCallback extends SuccessCallback, MessageErrorCallback {
    }
}