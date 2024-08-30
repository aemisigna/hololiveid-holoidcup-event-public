package com.covercorp.holosports.game.minigame.tug.npc.custom;

import dev.sergiferry.playernpc.api.NPC;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
public final class CustomNpc {
    private final NPC.Global npc;
    private final ClickType clickType;
}
