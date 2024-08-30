package com.covercorp.holosports.commons.commandcenter.autocomplete.autocompleters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;

import java.util.Collection;
import java.util.function.BiPredicate;

@Getter(AccessLevel.PUBLIC)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class FilteredAutocompleter<T> implements Autocompleter<T> {
    private final BiPredicate<T, String> filter;

    private Autocompleter<T> backingCompleter;

    public void addAutocompleter(final Autocompleter<T> backingAutocompleter) {
        backingCompleter = backingAutocompleter;
    }

    @Override
    public Collection<String> autocomplete(final AbstractCommandSender<? extends T> sender, final String str) {
        return backingCompleter.autocomplete(sender, str)
                .stream()
                .filter(arg -> filter.test(sender.getSender(), arg))
                .toList();
    }
}