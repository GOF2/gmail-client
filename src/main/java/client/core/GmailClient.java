package client.core;

public class GmailClient extends BaseGmailClient {
    private static GmailClient client;

    private GmailClient() {
    }

    public static GmailClient get() {
        if (client == null)
            return create();
        return client;
    }

    public static GmailClient create() {
        return new GmailClient();
    }
}
