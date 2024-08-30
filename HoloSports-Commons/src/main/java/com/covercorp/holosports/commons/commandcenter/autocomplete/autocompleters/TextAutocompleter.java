package com.covercorp.holosports.commons.commandcenter.autocomplete.autocompleters;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class TextAutocompleter<T> implements Autocompleter<T> {
    private String[] suggestions;

    public TextAutocompleter(final List<String> suggestions) {
        setSuggestions(suggestions.toArray(new String[] {}));
    }

    public TextAutocompleter(final String... suggestions) {
        setSuggestions(suggestions);
    }

    private void setSuggestions(final String[] str) {
        suggestions = Arrays.copyOf(str, str.length);
    }

    @Override
    public Collection<String> autocomplete(@SuppressWarnings("rawtypes") final AbstractCommandSender sender, final String startsWith) {
        return Arrays.stream(suggestions)
                .filter(str -> str.toLowerCase()
                        .startsWith(startsWith.toLowerCase()))
                .collect(Collectors.toList());
    }
}