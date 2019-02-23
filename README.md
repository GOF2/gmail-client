# gmail-client
[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/dashboard?id=gmail-client)
[![Build Status](https://travis-ci.com/GOF2/gmail-client.svg?branch=test)](https://travis-ci.com/GOF2/gmail-client)

Java library based on javax.mail. Used for  <b> sending</b> and <b>receiving</b> mails with gmail account.You can implement it in project by adding to gradle:
```gradle

repositories {
    ...
    maven { url 'https://jitpack.io' }

}

dependencies {
	        implementation 'com.github.gof2:gmail-client:v0.0.9'
	}
```

<b>To begin  with</b>, this Java library architecture based on [callback system](https://en.wikipedia.org/wiki/Callback_(computer_programming)) .This was done to help the user <b>react</b> on some actions such as Error while receiving , NoInternetConnection error. This allows to keep your program in soft flow of data with no critical errors.The problem of NoInternetConnection done by using the <b>reconnect system</b>.
### So,how to start using it ?
# 1.Account authenthication
```java
    final GmailClient client = getClient().auth();
    private GmailClient getClient() {
        return GmailClient.get()
                .loginWith(Gmail.auth("your.email@gmail.com", "yourpass"))
                .beforeLogin(() -> someActionBeforeLogin())
                .reconnectIfError(millis, attempts)
                .onLoginError(e -> someActionOnLoginError())
                .onLoginSuccess(() -> someActionOnLoginError());
    }
```
# 2.Create and send message
```java
 private SendedMessage yourMessage() {
        return new SendedMessage("Topic", "Text message")
                .from("Your FC")
                .to("test.mail1@gmail.com")
                .to("test.mail2@gmail.com")
                .attachFiles(fileName);
    }

 client.send(yourMessage(), new ISender.SendCallback() {
            @Override
            public void onError(MessagingException e) {
                yourActionOnErrorWhileSending();
            }

            @Override
            public void onSuccess() {
                yourActionOnSuccessSending();
            }
        });
```
# 3.Receive messages
```java
 client.receive(new IReceiver.ReceiveCallback() {
            @Override
            public void onReceive(Set<ReceivedMessage> messages) {
                System.out.println("=====================================================");
                System.out.println("Received messages: " + messages
                        .stream()
                        .map(m -> (m.getMessage() + " => " + m.getDate()).trim())
                        .collect(Collectors.joining("\n"))
                );
                System.out.println("Received size: " + messages.size());
                System.out.println("=====================================================");
            }

            @Override
            public void onUpdate(ReceivedMessage message) {
                System.out.println("-----------------------------------------------------------------");
                System.out.println("New message: " + (message.getMessage() + " => " + message.getDate()).trim());
                System.out.println("-----------------------------------------------------------------");
            }

            @Override
            public void onError(MessagingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
    }
```
Receiving messages starts the thread which would work on his own(listening your mail folder).

