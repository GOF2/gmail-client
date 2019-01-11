package client.utils;

import java.util.Properties;

public class Host {
    public  static Properties getSendProperties() {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtp.protocol","smtps");
        props.put("mail.smtp.starttls.enable", "true");
        return props;
    }

    public static Properties getReceiveProperties() {
        Properties props = System.getProperties();
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.protocol","imaps");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.starttls.enable", "true");
        return props;
    }
}
