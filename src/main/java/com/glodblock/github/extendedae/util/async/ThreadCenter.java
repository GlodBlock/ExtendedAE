package com.glodblock.github.extendedae.util.async;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class ThreadCenter {

    private static final ListeningExecutorService SERVICE = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public static <T> void requestSearch(Callable<T> task, SuccessCallback<T> listener) {
        var future = SERVICE.submit(task);
        Futures.addCallback(future, listener, Runnable::run);
    }

    @FunctionalInterface
    public interface SuccessCallback<V> extends FutureCallback<V> {

        default void onFailure(@NotNull Throwable throwable) {
            this.onSuccess(null);
        }

    }

}
