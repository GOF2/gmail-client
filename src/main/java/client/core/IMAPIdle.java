package client.core;

import client.core.common.ReceivedMessage;
import client.core.common.Receiver;
import client.core.interfaces.IReceiver;
import client.core.interfaces.callbacks.Callback;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.FLAGS;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

public class IMAPIdle {
    public void startListening(IMAPFolder imapFolder) {
        Thread t = new Thread(
                new KeepAliveRunnable(imapFolder), "IdleConnectionKeepAlive"
        );
        t.start();
        while (!Thread.interrupted()) {
            System.out.println("Starting IDLE");
            try {
                imapFolder.idle();
            } catch (MessagingException e) {
                System.out.println("Messaging exception during IDLE");
                throw new RuntimeException(e);
            }
        }

        interruptIsAlive(t);
    }

    private void interruptIsAlive(Thread t) {
        if (t.isAlive()) {
            t.interrupt();
        }
    }


    private static class KeepAliveRunnable implements Runnable {

        private static final long KEEP_ALIVE_FREQ = 3000; // 5 minutes

        private IMAPFolder folder;

        public KeepAliveRunnable(IMAPFolder folder) {
            this.folder = folder;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(KEEP_ALIVE_FREQ);
                    System.out.println(folder.getMessageCount());
                    //receive new message and proccess
                    // Perform a NOOP just to keep alive the connection
                    System.out.println("Performing a NOOP to keep alvie the connection");
                    protocolCommand();
                } catch (InterruptedException | MessagingException e) {
                    // Ignore, just aborting the thread...
                }
            }
        }

        private void protocolCommand() throws MessagingException {
            folder.doCommand(p -> {
                p.simpleCommand("NOOP", null);
                return null;
            });
        }
    }
}
