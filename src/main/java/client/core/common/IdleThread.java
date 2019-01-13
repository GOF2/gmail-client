package client.core.common;

import client.authenticator.EmailAuthenticator;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

class IdleThread extends TimerTask {
    private EmailAuthenticator authenticator;
    IdleThread(EmailAuthenticator authenticator){
        this.authenticator = authenticator;
    }

    @Override
    public void run() {
        Receiver.getInstance(authenticator).compareWithFile();
    }
}