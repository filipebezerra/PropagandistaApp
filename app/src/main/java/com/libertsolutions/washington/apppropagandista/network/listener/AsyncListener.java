package com.libertsolutions.washington.apppropagandista.network.listener;

/**
 *
 * @author Filipe Bezerra
 * @version #, 15/11/2015
 * @since #
 * @param <T> the async result type.
 */
public interface AsyncListener<T> {
    void onBeforeExecute();
    void onSuccess(T result);
    void onResultNothing();
    void onFailure(Throwable error);
    void onCancel();
}
