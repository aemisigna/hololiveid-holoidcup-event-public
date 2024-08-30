package com.covercorp.holosports.game.team;

import com.covercorp.holosports.game.player.IBaseParticipant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.bukkit.ChatColor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
public abstract class BaseTeam<T extends IBaseTeam, P extends IBaseParticipant> {
    private final String identifier;
    private final String name;
    private final String displayName;

    private final String baseColor;
    private final ChatColor color;

    private T teamType;
    private List<P> participants;

    public abstract P addParticipant(final P participant);
    public abstract P removeParticipant(final P participant);
}
