package com.covercorp.holosports.commons.commandcenter.autocomplete;

import com.covercorp.holosports.commons.commandcenter.autocomplete.autocompleters.CommonAutocompleter;
import com.covercorp.holosports.commons.commandcenter.autocomplete.autocompleters.PlayerAutocompleter;
import com.covercorp.holosports.commons.commandcenter.autocomplete.autocompleters.TextAutocompleter;
import com.covercorp.holosports.commons.commandcenter.autocomplete.autocompleters.CombiningAutocompleter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Autocomplete {

    @SuppressWarnings("rawtypes")
    public static final Autocompleter NONE = (t, s) -> Collections.emptyList();

    private Autocomplete() {
    }

    public static <T> Autocompleter<T> text(String... text) {
        return new TextAutocompleter<T>(text);
    }

    public static <T> Autocompleter<T> text(List<String> text) {
        return new TextAutocompleter<T>(text);
    }

    public static <T> Autocompleter<T> array(Collection<T> array) {
        return new CommonAutocompleter<>(() -> array);
    }

    public static <T, R> Autocompleter<T> mapped(Collection<R> array, Function<R, String> mapFunction) {
        return new CommonAutocompleter<>(() -> array, mapFunction);
    }

    public static <T, R> Autocompleter<T> supply(Supplier<Collection<R>> supplier) {
        return new CommonAutocompleter<>(supplier);
    }

    public static <T, R> Autocompleter<T> supplyMapped(Supplier<Collection<R>> supplier, Function<R, String> mapFunction) {
        return new CommonAutocompleter<>(supplier, mapFunction);
    }

    public static <T> Autocompleter<T> combine(Autocompleter<? super T>... completors) {
        final CombiningAutocompleter<T> combined = new CombiningAutocompleter<>();

        for (Autocompleter<? super T> all : completors) combined.addAutocompleter(all);

        return combined;
    }

    @SuppressWarnings("rawtypes")
    public static <T> Autocompleter<T> players() {
        return new PlayerAutocompleter();
    }

    public static <T> Autocompleter<T> none() {
        return NONE;
    }
}