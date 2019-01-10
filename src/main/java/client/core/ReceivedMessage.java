package client.core;

import java.io.File;
import java.util.Date;

public class ReceivedMessage extends BaseMessage{
    private String subject;
    private String message;
    private File[] attachment;
    private String from;
    private Date date;

    ReceivedMessage(String subject, String message) {
        super(subject, message);
    }

    ReceivedMessage(String message) {
        super(message);
    }


}
