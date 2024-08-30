package com.covercorp.holosports.commons.commandcenter.visibility;

public enum CommandVisibility {

    SHOW_ALL(true, true), HIDE_HELP(false, true), HIDE_AUTOCOMPLETE(true, false), HIDDEN(false, false);

    private final boolean helpVisible;
    private final boolean autocompleteVisible;

    CommandVisibility(boolean help, boolean autocomplete) {
        this.helpVisible = help;
        this.autocompleteVisible = autocomplete;
    }

    public final boolean isShowAutocomplete() {
        return autocompleteVisible;
    }

    public final boolean isShowInHelp() {
        return helpVisible;
    }
}
