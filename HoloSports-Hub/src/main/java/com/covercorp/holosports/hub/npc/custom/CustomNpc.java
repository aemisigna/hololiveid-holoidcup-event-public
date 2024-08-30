package com.covercorp.holosports.hub.npc.custom;

import dev.sergiferry.playernpc.api.NPC;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
public final class CustomNpc {
    private final NPC.Global npc;
    private final String display;
    private final String serverId;
}
