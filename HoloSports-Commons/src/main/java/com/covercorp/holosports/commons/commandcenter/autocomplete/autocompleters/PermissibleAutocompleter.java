package com.covercorp.holosports.commons.commandcenter.autocomplete.autocompleters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Getter(AccessLevel.PUBLIC)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class PermissibleAutocompleter<T> implements Autocompleter<T> {
    private final Autocompleter<T> backingAutocompleter;
    private final Collection<String> permissions = new HashSet<>();

    /**
     * Attaches a permission to the autocompleter.
     *
     * @param permission permission to use this autocompleter
     */
    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    @Override
    public Collection<String> autocomplete(final AbstractCommandSender<? extends T> sender, String str) {
        for (final String permission : permissions) {
            if (!sender.hasPermission(permission)) return new ArrayList<>();
        }
        return backingAutocompleter.autocomplete(sender, str);
    }
}
