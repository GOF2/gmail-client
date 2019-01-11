package client.core;

public interface ISender {
    void sendMessage(SendedMessage message);

    void closeConnection();

}
