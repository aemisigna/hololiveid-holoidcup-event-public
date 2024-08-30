package com.covercorp.holosports.shared.concurrent;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;

import java.util.concurrent.Executors;

public final class ExecutorProvider {
    @Getter private final ListeningExecutorService executorService;

    public ExecutorProvider() {
        executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }
}
