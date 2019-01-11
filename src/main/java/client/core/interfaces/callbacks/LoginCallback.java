package client.core.interfaces.callbacks;

public interface LoginCallback<S, E> {
    void beforeLogin();
    void onLoginError(E e);
    void onSuccessLogin(S s);
}
