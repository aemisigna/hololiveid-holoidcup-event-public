package com.covercorp.holosports.game.minigame.bentengan.arena.listener.event;

import com.covercorp.holosports.game.minigame.bentengan.arena.BentenganArena;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class BentenganMatchTickEvent extends Event {
    @Getter(AccessLevel.PUBLIC) private final BentenganArena arena;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public BentenganMatchTickEvent(final BentenganArena arena) {
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
