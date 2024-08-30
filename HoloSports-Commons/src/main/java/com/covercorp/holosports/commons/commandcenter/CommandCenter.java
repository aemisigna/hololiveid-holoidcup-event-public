package com.covercorp.holosports.commons.commandcenter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.covercorp.holosports.commons.commandcenter.identity.resolver.MinecraftIdentityResolver;
import com.covercorp.holosports.commons.commandcenter.identity.resolver.PlayerIdentityResolver;

public final class CommandCenter {
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private static PlayerIdentityResolver identityResolver = new MinecraftIdentityResolver();
}
