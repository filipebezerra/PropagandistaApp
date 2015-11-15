package com.libertsolutions.washington.apppropagandista.network.call;

import com.libertsolutions.washington.apppropagandista.network.listener.AsyncListener;

/**
 *
 * @author Fbs
 * @version #, 15/11/2015
 * @since #
 * @param <P> the param type.
 * @param <T> the result type.
 */
public interface AsyncCall<P, T> {
    void execute(P param, AsyncListener<T> callback);
}
