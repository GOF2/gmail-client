package client.utils;

import java.util.Properties;

public class Host {
    private Host(){
        throw new IllegalStateException("Host class");
    }
    public  static Properties getSendProperties() {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtp.port","465");
        props.put("mail.smtp.protocol","smtps");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtps.timeout", "5000");
        props.put("mail.smtps.connectiontimeout", "5000");
        props.put("mail.smtp.ssl.trust", "*");

        return props;
    }

    public static Properties getReceiveProperties() {
        Properties props = System.getProperties();
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.protocol","imaps");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.starttls.enable", "true");
        props.put("mail.imaps.partialfetch", false);
        props.put("mail.imaps.fetchsize", "1048576");
        return props;
    }
}
