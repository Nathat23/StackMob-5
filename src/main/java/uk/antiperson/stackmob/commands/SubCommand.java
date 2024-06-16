package uk.antiperson.stackmob.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public abstract class SubCommand implements Command {

    private final CommandArgument[] arguments;
    public SubCommand(CommandArgument... arguments) {
        this.arguments = arguments;
    }

    public CommandArgument[] getArguments() {
        return arguments;
    }

    public String getCommand() {
        return getCommandMetadata().command();
    }

    public String getDescription() {
        return getCommandMetadata().desc();
    }

    public boolean isPlayerRequired() {
        return getCommandMetadata().playerReq();
    }

    private CommandMetadata getCommandMetadata() {
        return getClass().getAnnotation(CommandMetadata.class);
    }

    public Component buildComponent(String cmd) {
        StringBuilder args = new StringBuilder();
        for (CommandArgument argumentType : getArguments()) {
            String options = argumentType.buildString();
            if (argumentType.isOptional()) {
                args.append("(").append(options).append(") ");
                continue;
            }
            args.append("[").append(options).append("] ");
        }
        Component label = Component.text("/" + cmd + " " + getCommand() + " " + args).color(TextColor.color(60, 179, 113));
        Component separator = Component.text("- ").color(NamedTextColor.GRAY);
        Component desc = Component.text(getDescription()).color(TextColor.color(144, 238, 144));
        return label.append(separator).append(desc);
    }

}
