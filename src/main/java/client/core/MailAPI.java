package client.core;

import client.core.interfaces.IAuthentication;
import client.core.interfaces.IReceiver;
import client.core.interfaces.ISender;

public interface MailAPI extends IAuthentication, ISender, IReceiver {
}
