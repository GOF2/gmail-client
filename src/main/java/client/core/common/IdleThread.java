package client.core.common;

import client.authenticator.AuthData;
import com.sun.mail.imap.IMAPFolder;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

 class IdleThread extends Thread {
     private AuthData authData;

     private final Folder folder;
     private volatile boolean running = true;

     IdleThread(Folder folder, AuthData authData) {
         super();
         this.folder = folder;
         this.authData = authData;
     }

     synchronized void kill() {
         if (!running)
             return;
         this.running = false;
     }

     @Override
     public void run() {
         while (running) {
             try {
                 ensureOpen(folder);
                 System.out.println("enter idle");
                 ((IMAPFolder) folder).idle();
             } catch (Exception e) {
                 // something went wrong
                 // wait and try again
                 e.printStackTrace();
                 try {
                     Thread.sleep(100);
                 } catch (InterruptedException e1) {
                     // ignore
                 }
             }

         }
     }


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
     }
 }