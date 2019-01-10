package client.core;

import com.sun.istack.internal.Nullable;

import java.io.File;

public final class SendedMessage extends BaseMessage {
    private String[] to;

    public SendedMessage(@Nullable String subject, @Nullable String message) {
        super(subject, message);
    }

    public SendedMessage(@Nullable String message) {
        super(message);
    }

    String[] getTo() {
        return to;
    }
    private void setTo(String[] to) {
        this.to = to;
    }

    public SendedMessage attachFiles(File... file) {
        super.setAttachment(file);
        return SendedMessage.this;
    }

    public SendedMessage to(String... to) {
        setTo(to);
        return SendedMessage.this;
    }

    public SendedMessage from(String from) {
        this.setFrom(from);
        return SendedMessage.this;
    }
}
