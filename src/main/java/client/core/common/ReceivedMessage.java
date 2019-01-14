package client.core.common;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.Date;

public class ReceivedMessage extends BaseMessage implements Comparable<ReceivedMessage> {
    public ReceivedMessage(@Nullable String subject, @Nullable String message) {
        super(subject, message);
    }

    public ReceivedMessage(@Nullable String message) {
        super(message);
    }

    ReceivedMessage(@NotNull String from, @Nullable String subject, @Nullable String message, @Nullable File[] attachment) {
        super(from, subject, message, attachment);
    }

    ReceivedMessage(@NotNull String from, @Nullable String subject, @Nullable String message) {
        super(from, subject, message);
    }

    public ReceivedMessage(@NotNull String from, @Nullable String subject,
                           @Nullable String message, @NotNull Date date) {
        super(from, subject, message);
        setDate(date);
    }

    @Override
    public int compareTo(ReceivedMessage o) {
        return this.getDate().compareTo(o.getDate());
    }
}
