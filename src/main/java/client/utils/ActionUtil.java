package client.utils;

import client.core.interfaces.callbacks.Function;

public class ActionUtil {
    private ActionUtil(){
        throw new IllegalStateException("Action Utility class");
    }
    public static void callIfNotNull(Object o, Function function) {
        if (o != null)
            function.call();
    }
}
