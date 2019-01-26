package client.core.common;

import com.sun.mail.imap.IMAPFolder;

import javax.mail.MessagingException;

public class IDLE implements Runnable {
    private static final long KEEP_ALIVE_FREQ = 2 * 60 * 1000L; // 5 minutes

    private IMAPFolder folder;

    public IDLE(IMAPFolder folder) {
        this.folder = folder;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(KEEP_ALIVE_FREQ);
                folder.doCommand(p -> {
                    p.simpleCommand("NOOP", null);
                    return null;
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (MessagingException e) {
                System.out.println("unexpected end of idle");
            }
        }
    }
}
