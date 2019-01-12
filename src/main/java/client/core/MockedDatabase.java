package client.core;

import client.core.common.ReceivedMessage;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class MockedDatabase {
    private static Set<ReceivedMessage> messages = new TreeSet<>();
    private static MockedDatabase database;

    public static MockedDatabase getInstance() {
        if (database == null)
            return new MockedDatabase();
        return database;
    }

    public Set<ReceivedMessage> getMessages() {
        return messages;
    }

    public void add(ReceivedMessage message) {
        getMessages().add(message);
    }

    public void addAll(Collection<ReceivedMessage> collection) {
        messages.addAll(collection);
    }
}