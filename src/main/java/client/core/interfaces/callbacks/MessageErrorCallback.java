package client.core.interfaces.callbacks;

import javax.mail.MessagingException;

@FunctionalInterface
public interface MessageErrorCallback {
    void onError(MessagingException e);
}
