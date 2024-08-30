package com.covercorp.holosports.commons.commandcenter.autocomplete.autocompleters;

import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.Collection;
import java.util.stream.Collectors;

public final class PlayerAutocompleter<T extends CommandSender> implements Autocompleter<T> {
    @Override
    public Collection<String> autocomplete(AbstractCommandSender<? extends T> sender, String startsWith) {
        String lowercase = startsWith.toLowerCase();
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(HumanEntity::getName)
                .filter(n -> n.toLowerCase().startsWith(lowercase))
                .collect(Collectors.toList());
    }
}
