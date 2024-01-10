package uk.antiperson.stackmob.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.commands.subcommands.*;
import uk.antiperson.stackmob.utils.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Commands implements CommandExecutor, TabCompleter {

    private final StackMob sm;
    private final TreeMap<String, SubCommand> subCommands;

    public Commands(StackMob sm) {
        this.sm = sm;
        this.subCommands = new TreeMap<>();
    }

    public void registerSubCommands() {
        register(About.class);
        register(SpawnStack.class);
        register(Remove.class);
        register(CheckUpdate.class);
        register(Upgrade.class);
        register(GiveTool.class);
        register(Reload.class);
        register(ForceStack.class);
        register(Stats.class);
    }

    private void register(Class<? extends SubCommand> subCommandClass) {
        SubCommand subCommand;
        try {
            subCommand = subCommandClass.getConstructor(StackMob.class).newInstance(sm);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            sm.getLogger().info("Error occurred while trying to register " + subCommandClass.getSimpleName() + " sub command!");
            e.printStackTrace();
            return;
        }
        subCommands.put(subCommand.getCommand(), subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Audience sender = sm.getAdventure().sender(commandSender);
        String cmd = Bukkit.getServer().getPluginCommand("sm").getPlugin().equals(sm) ? "sm" : "stackmob";
        if (!(commandSender.hasPermission("stackmob.admin"))) {
            sendError(sender, "You do not have permission!");
            return false;
        }
        if (strings.length == 0) {
            Component commands = Utilities.PREFIX.append(Component.text("Commands:").color(TextColor.color(255, 127, 80)));
            sender.sendMessage(commands);
            for (SubCommand subCommand : subCommands.values()) {
                sender.sendMessage(subCommand.buildComponent(cmd));
            }
            Component key = Component.text("Key: () = Optional argument, [] = Mandatory argument.").color(TextColor.color(255, 127, 80));
            sender.sendMessage(key);
            return false;
        }
        SubCommand subCommand = subCommands.get(strings[0].toLowerCase());
        if (subCommand == null) {
            sendError(sender, "Invalid subcommand!");
            return false;
        }
        if (subCommand.isPlayerRequired() && !(commandSender instanceof Player)) {
            sendError(sender, "This subcommand requires a player!");
            return false;
        }
        String[] subCmdArgs = Utilities.removeFirst(strings);
        if (!validateArgs(subCommand.getArguments(), subCmdArgs)) {
            sendError(sender, "Invalid arguments for '" + subCommand.getCommand() + "'. Usage:");
            sender.sendMessage(subCommand.buildComponent(cmd));
            return false;
        }
        subCommand.onCommand(new User(sm.getAdventure().sender(commandSender), commandSender), subCmdArgs);
        return false;
    }

    private void sendError(Audience audience, String message) {
        Component noPerm = Utilities.PREFIX.append(Component.text(message).color(NamedTextColor.RED));
        audience.sendMessage(noPerm);
    }

    public boolean validateArgs(CommandArgument[] argumentTypes, String[] args) {
        for (int i = 0; i < args.length; i++) {
            CommandArgument argument = argumentTypes[i];
            switch (argument.getType()) {
                case BOOLEAN:
                    if (!(args[i].equals("true") || args[i].equals("false"))) return false;
                    break;
                case INTEGER:
                    try {
                        Integer.valueOf(args[i]);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    break;
                case ENTITY_TYPE:
                    try {
                        EntityType.valueOf(args[i].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                    break;
                case WORLD:
                    if (Bukkit.getWorld(args[i]) == null) {
                        return false;
                    }
                    break;
                case STRING:
                    if (!argument.getExpectedArguments().contains(args[i])) {
                        return false;
                    }
            }
        }
        if (args.length < argumentTypes.length) {
            if (argumentTypes.length == (args.length + 1)) {
                CommandArgument argument = argumentTypes[argumentTypes.length - 1];
                return argument.isOptional();
            }
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Set<String> expectedArguments = getExpectedArguments(strings);
        expectedArguments.removeIf(possibleArgument -> !possibleArgument.toLowerCase().startsWith(strings[strings.length - 1].toLowerCase()));
        return new ArrayList<>(expectedArguments);
    }

    private Set<String> getExpectedArguments(String[] strings) {
        SubCommand subCommand = subCommands.get(strings[0].toLowerCase());
        if (subCommand == null) {
            if (strings.length > 1) {
                return Collections.emptySet();
            }
            return new HashSet<>(subCommands.keySet());
        }
        // the subcommand is correct, so length of arguments is length of string array - 1
        int subCmdArgsLength = strings.length - 1;
        // if the command has no arguments or the arguments exceed cmd length
        if (subCmdArgsLength == 0 || subCmdArgsLength > subCommand.getArguments().length) {
            return Collections.emptySet();
        }
        // get the argument at this position in the cmd
        CommandArgument commandArgument = subCommand.getArguments()[subCmdArgsLength - 1];
        return new TreeSet<>(commandArgument.getExpectedArguments());
    }
}
