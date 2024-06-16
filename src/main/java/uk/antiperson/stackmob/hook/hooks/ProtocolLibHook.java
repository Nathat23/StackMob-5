package uk.antiperson.stackmob.hook.hooks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.hook.Hook;
import uk.antiperson.stackmob.hook.HookMetadata;
import uk.antiperson.stackmob.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@HookMetadata(name = "ProtocolLib", config = "protocollib")
public class ProtocolLibHook extends Hook {

    private ProtocolManager protocolManager;
    private int entityIdCounter;
    public ProtocolLibHook(StackMob sm) {
        super(sm);
        this.entityIdCounter = Integer.MAX_VALUE / 2;
    }

    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /* https://github.com/Ste3et/FurnitureLib/commit/2f7c9adbe90716811ecc620c021bed0c727b10f0#diff-0f3b41bd8ab636343d5689cbcc1e2d008aa3b65454e5af09cba8059a4ac51bed */
    private void writeWatchableObjects(WrappedDataWatcher watcher, PacketContainer packetContainer) {
        if (Utilities.isVersionAtLeast(Utilities.MinecraftVersion.V1_19_4)) {
            List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
            watcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
                WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
            });
            packetContainer.getDataValueCollectionModifier().write(0, wrappedDataValueList);
            return;
        }
        packetContainer.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
    }

    public void sendPacket(Player player, Entity entity, boolean visible) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataWatcher watcher = new WrappedDataWatcher(entity);
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), visible);
        packetContainer.getEntityModifier(entity.getWorld()).write(0, entity);
        writeWatchableObjects(watcher, packetContainer);
        protocolManager.sendServerPacket(player, packetContainer);
    }

    public int spawnFakeArmorStand(Player player, Location location, Component name) {
        // spawn packet
        entityIdCounter = Utilities.isPaper() ? Bukkit.getUnsafe().nextEntityId() : entityIdCounter + 1;
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        packetContainer.getIntegers().write(0, entityIdCounter);
        packetContainer.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        packetContainer.getUUIDs().write(0, UUID.randomUUID());
        packetContainer.getDoubles().write(0, location.getX());
        packetContainer.getDoubles().write(1, location.getY());
        packetContainer.getDoubles().write(2, location.getZ());
        // metadata packet
        PacketContainer packetContainer1 = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20);
        // #getHandle() req on older than 1.19
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(name)).getHandle()));
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true);
        int markerPacketId = Utilities.getMinecraftVersion() == Utilities.MinecraftVersion.V1_16 ? 14 : 15;
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(markerPacketId, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x10);
        packetContainer1.getIntegers().write(0, entityIdCounter);
        writeWatchableObjects(watcher, packetContainer1);
        protocolManager.sendServerPacket(player, packetContainer);
        protocolManager.sendServerPacket(player, packetContainer1);
        return entityIdCounter;
    }

    public void updateTag(Player player, int id, Component newName) {
        PacketContainer packetContainer1 = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(newName)).getHandle()));
        packetContainer1.getIntegers().write(0, id);
        writeWatchableObjects(watcher, packetContainer1);
        protocolManager.sendServerPacket(player, packetContainer1);
    }

    public void teleport(Player player, int id, Location location) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packetContainer.getIntegers().write(0, id);
        packetContainer.getDoubles().write(0, location.getX());
        packetContainer.getDoubles().write(1, location.getY());
        packetContainer.getDoubles().write(2, location.getZ());
        protocolManager.sendServerPacket(player, packetContainer);
    }

    public void removeFakeArmorStand(Player player, int id) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        if (Utilities.isVersionAtLeast(Utilities.MinecraftVersion.V1_17)) {
            packetContainer.getIntLists().write(0, List.of(id));
        } else {
            packetContainer.getIntegerArrays().write(0, new int[]{id});
        }
        protocolManager.sendServerPacket(player, packetContainer);
    }
}
