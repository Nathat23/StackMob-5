package uk.antiperson.stackmob.commands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.commands.subcommands.*;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.*;

public class Commands implements CommandExecutor, TabCompleter {

    private final StackMob sm;
    private final Set<SubCommand> subCommands;
    public Commands(StackMob sm) {
        this.sm = sm;
        this.subCommands = new HashSet<>();
    }

    public void registerSubCommands() {
        subCommands.add(new About(sm));
        subCommands.add(new SpawnStack(sm));
        subCommands.add(new Remove(sm));
        subCommands.add(new CheckUpdate(sm));
        subCommands.add(new Upgrade(sm));
        subCommands.add(new GiveTool(sm));
        subCommands.add(new Reload(sm));
        subCommands.add(new ForceStack(sm));
        subCommands.add(new Stats(sm));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender.hasPermission("stackmob.admin"))) {
            commandSender.sendMessage(Utilities.PREFIX + ChatColor.RED + "You do not have permission!");
            return false;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(Utilities.PREFIX + ChatColor.GOLD + "Commands: ");
            for (SubCommand subCommand : subCommands) {
                StringBuilder args = new StringBuilder();
                for (CommandArgument argumentType : subCommand.getArguments()) {
                    StringBuilder options = new StringBuilder();
                    if (argumentType.getExpectedArguments().size() <= 3 && argumentType.getExpectedArguments().size() > 0) {
                        argumentType.getExpectedArguments().forEach(argument -> options.append(argument).append("/"));
                        options.deleteCharAt(options.length() - 1);
                    } else if (argumentType.getName() != null) {
                        options.append(argumentType.getName());
                    } else {
                        options.append(argumentType.getType());
                    }
                    if (argumentType.isOptional()) {
                        args.append("(").append(options).append(") ");
                        continue;
                    }
                    args.append("[").append(options).append("] ");
                }
                commandSender.sendMessage(ChatColor.AQUA + "/sm " + subCommand.getCommand() + " " + args + ChatColor.GRAY + "- " + ChatColor.YELLOW + subCommand.getDescription());
            }
            commandSender.sendMessage(ChatColor.GOLD + "Key: () = Optional argument, [] = Mandatory argument.");
            return false;
        }
        for (SubCommand subCommand : subCommands) {
            if (!subCommand.getCommand().equalsIgnoreCase(strings[0])) {
                continue;
            }
            if (subCommand.isPlayerRequired() && !(commandSender instanceof Player)) {
                commandSender.sendMessage(Utilities.PREFIX + ChatColor.RED + "You are not a player!");
                return false;
            }
            if (!validateArgs(subCommand.getArguments(), (String[]) ArrayUtils.remove(strings, 0))) {
                commandSender.sendMessage(Utilities.PREFIX + ChatColor.RED + "Invalid arguments!");
                return false;
            }
            subCommand.onCommand(new User(commandSender), (String[]) ArrayUtils.remove(strings, 0));
            return false;
        }
        commandSender.sendMessage(Utilities.PREFIX + ChatColor.RED + "Invalid subcommand!");
        return false;
    }

    public boolean validateArgs(CommandArgument[] argumentTypes, String[] args) {
        if (args.length < argumentTypes.length) {
            if (argumentTypes.length == (args.length + 1)) {
                CommandArgument argument = argumentTypes[argumentTypes.length - 1];
                return argument.isOptional();
            }
            return false;
        }
        for (int i = 0; i < argumentTypes.length; i++) {
            CommandArgument argument = argumentTypes[i];
            switch (argument.getType()) {
                case BOOLEAN:
                    if (!(args[i].equals("true") || args[i+1].equals("false"))) return false;
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
                case STRING:
                    if (!argument.getExpectedArguments().contains(args[i])) {
                        return false;
                    }
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            List<String> args = new ArrayList<>();
            for (SubCommand subCommand : subCommands) {
                String commandString = subCommand.getCommand();

                if (!commandString.toLowerCase().startsWith(strings[0].toLowerCase())) {
                    continue;
                }
                args.add(subCommand.getCommand());
            }
            return args;
        }
        for (SubCommand subCommand : subCommands) {
            if (!subCommand.getCommand().equalsIgnoreCase(strings[0])) {
                continue;
            }
            if (subCommand.getArguments().length < strings.length - 1) {
                return null;
            }
            CommandArgument commandArgument = subCommand.getArguments()[strings.length - 2];

            List<String> expectedArguments = new LinkedList<>(commandArgument.getExpectedArguments());
            expectedArguments.removeIf(possibleArgument -> !possibleArgument.toLowerCase().startsWith(strings[strings.length - 1].toLowerCase()));
            Collections.sort(expectedArguments);

            return expectedArguments;
        }
        return null;
    }
}
