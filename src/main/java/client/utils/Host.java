package client.utils;

import java.util.Properties;

public class Host {
    protected static Properties getSendProperties() {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return props;
    }

    protected static Properties getReceiveProperties() {
        Properties props = System.getProperties();
        props.put("mail.imap.host", "imap.gmail.com");
        props.put("mail.imap.port", "993");
        props.put("mail.imap.starttls.enable", "true");
        return props;
    }
}
