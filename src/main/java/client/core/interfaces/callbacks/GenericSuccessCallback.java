package client.core.interfaces.callbacks;

@FunctionalInterface
public interface GenericSuccessCallback<T> {
    void onSuccess(T t);
}
