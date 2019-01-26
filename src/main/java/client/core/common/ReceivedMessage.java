package client.core.common;


import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class ReceivedMessage extends BaseMessage implements Comparable<ReceivedMessage>,Serializable {
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
    public ReceivedMessage(){}

    @Override
    public int compareTo(ReceivedMessage o) {
        return this.getDate().compareTo(o.getDate());
    }


}
