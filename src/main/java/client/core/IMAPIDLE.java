package client.core;

import com.sun.mail.imap.IMAPFolder;

import javax.mail.*;

public class IMAPIDLE {
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

        if (t.isAlive()) {
            t.interrupt();
        }
    }


    private static class KeepAliveRunnable implements Runnable {

        private static final long KEEP_ALIVE_FREQ = 30000; // 5 minutes

        private IMAPFolder folder;

        public KeepAliveRunnable(IMAPFolder folder) {
            this.folder = folder;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(KEEP_ALIVE_FREQ);
                    //receive new message and proccess
                    // Perform a NOOP just to keep alive the connection
                    System.out.println("Performing a NOOP to keep alvie the connection");
                    folder.doCommand(p -> {
                        p.simpleCommand("NOOP", null);
                        return null;
                    });
                } catch (InterruptedException e) {
                    // Ignore, just aborting the thread...
                } catch (MessagingException e) {
                    // Shouldn't really happen...
                }
            }
        }
    }


}
