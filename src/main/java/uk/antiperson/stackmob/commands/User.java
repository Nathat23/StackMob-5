package uk.antiperson.stackmob.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import uk.antiperson.stackmob.utils.Utilities;

public class User {

    private final Audience sender;
    private final CommandSender cmdSender;

    public User(Audience sender, CommandSender cmdSender) {
        this.sender = sender;
        this.cmdSender = cmdSender;
    }

    public void sendRawMessage(String message) {
        sender.sendMessage(Component.text(message));
    }

    public void sendInfo(String message) {
        sendMessage(MessageType.INFO, message);
    }

    public void sendError(String message) {
        sendMessage(MessageType.ERROR, message);
    }

    public void sendSuccess(String message) {
        sendMessage(MessageType.SUCCESS, message);
    }

    public CommandSender getSender() {
        return cmdSender;
    }

    private void sendMessage(MessageType type, String string) {
        sendMessage(type, Component.text(string));
    }

    private void sendMessage(MessageType type, Component component) {
        switch (type) {
            case INFO:
                component = component.color(NamedTextColor.YELLOW);
                break;
            case ERROR:
                component = component.color(NamedTextColor.RED);
                break;
            case SUCCESS:
                component = component.color(NamedTextColor.GREEN);
                break;
        }
        sender.sendMessage(Utilities.PREFIX.append(component));
    }

    enum MessageType {
        INFO,
        ERROR,
        SUCCESS
    }
}
