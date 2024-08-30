package com.covercorp.holosports.game.minigame.potato.arena.listener.event;

import com.covercorp.holosports.game.minigame.potato.arena.PotatoArena;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PotatoMatchTickEvent extends Event {
    @Getter(AccessLevel.PUBLIC) private final PotatoArena arena;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public PotatoMatchTickEvent(final PotatoArena arena) {
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
