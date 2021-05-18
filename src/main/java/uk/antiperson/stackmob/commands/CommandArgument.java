package uk.antiperson.stackmob.commands;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandArgument {

    private final ArgumentType type;
    private final boolean optional;
    private final List<String> expectedArguments;
    private final String name;
    private CommandArgument(ArgumentType type, boolean optional, List<String> expectedArguments, String name) {
        this.type = type;
        this.optional = optional;
        this.expectedArguments = expectedArguments;
        this.name = name;
    }

    public ArgumentType getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public List<String> getExpectedArguments() {
        if (expectedArguments != null) {
            return expectedArguments;
        }
        ArrayList<String> strings = new ArrayList<>();
        switch (type) {
            case ENTITY_TYPE:
                for (EntityType etype : EntityType.values()) {
                    if (etype.getEntityClass() == null) {
                        continue;
                    }
                    if (!Mob.class.isAssignableFrom(etype.getEntityClass())) {
                        continue;
                    }
                    strings.add(etype.toString());
                }
                return strings;
            case BOOLEAN:
                return Arrays.asList("true", "false");
        }
        return strings;
    }

    public String getName() {
        return name;
    }

    public static CommandArgument construct(ArgumentType type, boolean optional) {
        return new CommandArgument(type, optional, null, null);
    }

    public static CommandArgument construct(ArgumentType type, boolean optional, String name) {
        return new CommandArgument(type, optional,null, name);
    }

    public static CommandArgument construct(ArgumentType type, boolean optional, List<String> expectedArguments) {
        return new CommandArgument(type, optional, expectedArguments, null);
    }

}
