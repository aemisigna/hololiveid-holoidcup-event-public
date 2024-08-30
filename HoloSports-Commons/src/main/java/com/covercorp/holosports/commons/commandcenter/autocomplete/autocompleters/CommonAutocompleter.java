package com.covercorp.holosports.commons.commandcenter.autocomplete.autocompleters;

import com.covercorp.holosports.commons.commandcenter.autocomplete.Autocompleter;
import com.covercorp.holosports.commons.commandcenter.sender.AbstractCommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class CommonAutocompleter<T> implements Autocompleter {
    private final Supplier<Collection<T>> supplier;
    private final Function<T, String> mapper;

    @Nullable private Predicate<T> filters;

    public CommonAutocompleter(final Supplier<Collection<T>> supplier) {
        this(supplier, Object::toString);
    }

    public CommonAutocompleter(final Supplier<Collection<T>> supplier, final Function<T, String> mapper) {
        this.supplier = supplier;
        this.mapper = mapper;
    }

    public CommonAutocompleter(final Supplier<Collection<T>> supplier, final Function<T, String> mapper, final Predicate<T>... filters) {
        this(supplier, mapper, Arrays.asList(filters));
    }

    public CommonAutocompleter(final Supplier<Collection<T>> supplier, final Function<T, String> mapper, final Collection<Predicate<T>> filters) {
        this.supplier = supplier;
        this.mapper = mapper;

        this.filters = filters.stream().reduce(x -> true, Predicate::and);
    }

    @Override
    public Collection<String> autocomplete(final AbstractCommandSender sender, final String startsWith) {
        final Collection<T> completableObjects = this.supplier.get();

        Stream<T> completableStream = completableObjects.stream();

        if (filters != null) completableStream = completableStream.filter(filters);

        final Stream<String> completableStringsStream = completableStream.map(mapper);
        final Stream<String> suggestionsStream = completableStringsStream.filter(startsWith.toLowerCase()::startsWith);

        return suggestionsStream.collect(Collectors.toList());
    }
}