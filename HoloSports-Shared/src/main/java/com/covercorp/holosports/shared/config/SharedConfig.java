package com.covercorp.holosports.shared.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
public final class SharedConfig {
    public final String databaseAddress;
    public final int databasePort;
    public final String database;
    public final String username;
    public final String password;
}
