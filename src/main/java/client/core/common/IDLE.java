package client.core.common;

import com.sun.mail.imap.IMAPFolder;

import javax.mail.MessagingException;

public class IDLE implements Runnable {
    private static final long KEEP_ALIVE_FREQ = 60 * 1000; // 5 minutes

    private IMAPFolder folder;

    public IDLE(IMAPFolder folder) {
        this.folder = folder;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(KEEP_ALIVE_FREQ);

                // Perform a NOOP just to keep alive the connection
                System.out.println("Perfoming NOOP command");
                folder.doCommand(p -> {
                    p.simpleCommand("NOOP", null);
                    return null;
                });
            } catch (InterruptedException e) {
                // Ignore, just aborting the thread...
            } catch (MessagingException e) {
                // Shouldn't really happen...
                System.out.println("unexpected end of idle");
            }
        }
    }
}
