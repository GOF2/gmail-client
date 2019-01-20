package client.core.common;

import client.core.interfaces.callbacks.ConnectionCallback;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class WaitingThread extends Thread {

    @Override
    public void run() {
        while (true) {
            process(2 * 60 * 1000, 5, new ConnectionCallback() {
                @Override
                public void onConnected() {

                }

                @Override
                public void onConnectionError() {

                }
            });
        }
    }

    public void process(long millis, int attempts, ConnectionCallback connectionCallback) {
        for (int i = 0; i < attempts; i++) {
            try {
                if (checkInternetConnection()) {
                    connectionCallback.onConnected();
                    break;
                } else {
                    connectionCallback.onConnectionError();
                }
                Thread.sleep(millis);
            } catch (InterruptedException ignored) {
            }
        }
    }


    private boolean checkInternetConnection() {
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress("gmail.com", 80);
        try {
            sock.connect(addr, 3000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                sock.close();
            } catch (IOException ignored) {
            }
        }
    }
}
