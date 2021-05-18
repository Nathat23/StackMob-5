package uk.antiperson.stackmob.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.commands.User;
import uk.antiperson.stackmob.entity.StackEntity;

import java.util.HashSet;
import java.util.Set;

public class StackingTool {

    private final Player player;
    private final ItemStack itemStack;
    private final StackMob sm;

    public StackingTool(StackMob sm, Player player) {
        this.sm = sm;
        this.player = player;
        this.itemStack = player.getInventory().getItemInMainHand();
    }

    public int getModeId() {
        return itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(sm.getToolKey(), PersistentDataType.INTEGER, 1);
    }

    public ToolMode getMode() {
        for (ToolMode t : ToolMode.values()) {
            if (t.ordinal() == getModeId()) {
                return t;
            }
        }
        throw new UnsupportedOperationException("No matching tool mode for given id!");
    }

    public void shiftMode() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        int nextMode = (getModeId() + 1) >= ToolMode.values().length ? 0 : getModeId() + 1;
        itemMeta.getPersistentDataContainer().set(sm.getToolKey(), PersistentDataType.INTEGER, nextMode);
        itemStack.setItemMeta(itemMeta);
        player.getInventory().setItemInMainHand(itemStack);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Shifted mode to " + getMode()));
    }

    public void performAction(LivingEntity clicked) {
        User user = new User(player);
        if (!sm.getEntityManager().isStackedEntity(clicked)) {
            if (getMode() != ToolMode.MODIFY) {
                user.sendError("You cannot use " + getMode() + " on an unstacked entity!");
                return;
            }
            startConversation(clicked);
            return;
        }
        StackEntity stackEntity = sm.getEntityManager().getStackEntity(clicked);
        switch (getMode()) {
            case MODIFY:
                startConversation(clicked);
                return;
            case SLICE:
            case SLICE_ALL:
                if (stackEntity.isSingle()) {
                    user.sendError("Entity is single, therefore it cannot be sliced!");
                    return;
                }
                int sliceNo = getMode() == ToolMode.SLICE ? 1 : stackEntity.getSize() - 1;
                StackEntity slice = stackEntity;
                Set<StackEntity> slices = new HashSet<>();
                for (int i = 0; i < sliceNo; i++) {
                    slice = slice.slice();
                    slices.add(slice);
                }
                stackEntity.removeStackData();
                if (getMode() == ToolMode.SLICE_ALL) {
                    slices.forEach(sliced -> sliced.setForgetOnSpawn(true));
                }
                break;
            case REMOVE_CHUNK:
                for (Entity e : clicked.getChunk().getEntities()) {
                    if (!(e instanceof Mob)) {
                        continue;
                    }
                    if (!sm.getEntityManager().isStackedEntity((LivingEntity) e)) {
                        continue;
                    }
                    StackEntity nearby = sm.getEntityManager().getStackEntity((LivingEntity) e);
                    nearby.removeStackData();
                }
                break;
            case REMOVE_SINGLE:
                stackEntity.removeStackData();
                break;
        }
        user.sendSuccess("Action performed successfully.");
    }

    private void startConversation(LivingEntity stackEntity) {
        ConversationFactory factory = new ConversationFactory(sm)
                .withTimeout(25)
                .withFirstPrompt(new ModifyPrompt(stackEntity))
                .withLocalEcho(false)
                .withPrefix(conversationContext -> Utilities.PREFIX)
                .addConversationAbandonedListener(new ExitPrompt());
        Conversation conversation = factory.buildConversation(player);
        conversation.begin();
    }

    enum ToolMode {
        MODIFY,
        SLICE,
        SLICE_ALL,
        REMOVE_SINGLE,
        REMOVE_CHUNK
    }

    private class ModifyPrompt extends NumericPrompt {

        private final LivingEntity livingEntity;
        private final int maxSize;

        public ModifyPrompt(LivingEntity livingEntity) {
            this.livingEntity = livingEntity;
            this.maxSize = sm.getMainConfig().getMaxStack(livingEntity.getType());
        }

        @Nullable
        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull Number number) {
            if (livingEntity.isDead()) {
                conversationContext.getForWhom().sendRawMessage(Utilities.PREFIX + ChatColor.RED + "Entity is no longer valid. Modification has been cancelled.");
                return Prompt.END_OF_CONVERSATION;
            }
            StackEntity stackEntity = sm.getEntityManager().getStackEntity(livingEntity);
            if (stackEntity == null) {
                stackEntity = sm.getEntityManager().registerStackedEntity(livingEntity);
            }
            stackEntity.setSize(number.intValue());
            conversationContext.getForWhom().sendRawMessage(Utilities.PREFIX + ChatColor.GREEN + "Stack value has been updated.");
            return Prompt.END_OF_CONVERSATION;
        }

        @NotNull
        @Override
        public String getPromptText(@NotNull ConversationContext conversationContext) {
            return ChatColor.YELLOW +  "Enter stack size: ";
        }

        @Nullable
        @Override
        protected String getFailedValidationText(@NotNull ConversationContext context, @NotNull String invalidInput) {
            return Utilities.PREFIX + ChatColor.RED + "Invalid input. Accepted sizes: 1-" + maxSize;
        }

        @Override
        protected boolean isNumberValid(@NotNull ConversationContext context, @NotNull Number input) {
            if (input.intValue() > maxSize) {
                return false;
            }
            return !(input.intValue() < 1);
        }
    }

    private class ExitPrompt implements ConversationAbandonedListener {

        @Override
        public void conversationAbandoned(@NotNull ConversationAbandonedEvent conversationAbandonedEvent) {
            if (conversationAbandonedEvent.gracefulExit()) {
                return;
            }
            conversationAbandonedEvent.getContext().getForWhom().sendRawMessage(Utilities.PREFIX + ChatColor.RED + "Stack modification has timed out.");
        }

    }
}
