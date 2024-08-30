package com.covercorp.holosports.shared;

import com.covercorp.holosports.shared.concurrent.ExecutorProvider;
import com.covercorp.holosports.shared.config.SharedConfig;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.logging.Logger;

public final class HoloSportsShared {
    @Getter(AccessLevel.PUBLIC) private static HoloSportsShared coreShared;

    @Getter(AccessLevel.PUBLIC) private final SharedConfig sharedConfig;
    @Getter(AccessLevel.PUBLIC) private ExecutorProvider executorProvider;
    @Getter(AccessLevel.PUBLIC) private Logger logger;

    public HoloSportsShared(final SharedConfig sharedConfig) {
        coreShared = this;

        this.sharedConfig = sharedConfig;

        executorProvider = new ExecutorProvider();

        logger = Logger.getLogger("HoloSports-Shared");
    }

    public void shutdown() {
        executorProvider = null;
        logger = null;
    }
}
