package com.covercorp.holosports.commons.commandcenter.autocomplete.autocompleters;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class CombiningAutocompleter<T> implements Autocompleter<T> {
    private final Collection<Autocompleter<? super T>> autocompleters = new ArrayList<>(2);

    // mutates
    public void addAutocompleter(final Autocompleter<? super T> autocompleter) {
        autocompleters.add(autocompleter);
    }

    // not mutating
    @Override
    public <U extends T> Autocompleter<U> and(final Autocompleter<U> other) {
        final CombiningAutocompleter<U> newCompleter = new CombiningAutocompleter<>();

        newCompleter.getAutocompleters().addAll(autocompleters);
        newCompleter.getAutocompleters().add(other);

        return newCompleter;
    }

    @Override
    public Collection<String> autocomplete(final AbstractCommandSender<? extends T> sender, final String str) {
        final List<String> completions = new ArrayList<>();

        autocompleters.forEach(all -> completions.addAll(all.autocomplete(sender, str)));

        return completions;
    }
}