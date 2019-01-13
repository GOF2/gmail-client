package client.core.common;

import client.authenticator.EmailAuthenticator;
import client.core.interfaces.IReceiver;

class IdleThread  extends  Thread{
    private EmailAuthenticator authenticator;
    private IReceiver.ReceiveCallback callback;

    IdleThread(EmailAuthenticator authenticator, IReceiver.ReceiveCallback callback){
        this.authenticator = authenticator;
        this.callback = callback;
    }


    //private final Folder folder;
    private volatile boolean running = true;


    synchronized void kill() {
        if (!running)
            return;
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(5000);
                System.out.println("enter idle");
                Receiver.getInstance(authenticator).compareWithFile(callback);
            } catch (Exception e) {
                // something went wrong
                // wait and try again
                e.printStackTrace();
                try {
                    Thread.sleep(5*60*1000);
                } catch (InterruptedException e1) {
                    // ignore
                }
            }

        }
    }

/*
    void close(final Folder folder) {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
        } catch (final Exception e) {
            // ignore
        }
    }

    void close(final Store store) {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (final Exception e) {
            // ignore
        }
    }

    private  void ensureOpen(final Folder folder) throws MessagingException {
        if (folder != null) {
            Store store = folder.getStore();
            if (store != null && !store.isConnected()) {
                store.connect(authData.getLogin(), authData.getPassword());
            }
        } else {
            throw new MessagingException("Unable to open a null folder");
        }

        if (folder.exists() && !folder.isOpen() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            folder.open(Folder.READ_ONLY);
            if (!folder.isOpen())
                throw new MessagingException("Unable to open folder " + folder.getFullName());
        }
    }*/
}