package com.covercorp.holosports.commons.commandcenter;

public abstract class CommandChild<T> extends CommandNode<T> {
    private CommandNode<? super T> parent;

    public CommandChild(final Class<T> senderType, final String name, final String hint) {
        super(senderType, name, hint);
    }

    protected CommandNode<? super T> getParent() {
        return parent;
    }

    protected void setParent(final CommandNode<? super T> parent) {
        this.parent = parent;
    }

    public CommandRoot<? super T> getRoot() {
        CommandNode<? super T> node = this;

        while (node instanceof CommandChild) node = ((CommandChild<? super T>) node).getParent();

        return (CommandRoot<? super T>) node;
    }
    @Override
    public String getAbsoluteName() {
        return parent.getAbsoluteName() + " " + getCommandIdentifier();
    }

    @Override
    protected CommandContext context() {
        return parent.context();
    }
}