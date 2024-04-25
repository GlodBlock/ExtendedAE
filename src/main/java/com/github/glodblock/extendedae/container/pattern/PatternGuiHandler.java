package com.github.glodblock.extendedae.container.pattern;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PatternGuiHandler {

    private static final Object2ObjectMap<String, IContainerPattern> factory = new Object2ObjectOpenHashMap<>();
    private static final Object2ReferenceMap<String, MenuType<?>> types = new Object2ReferenceOpenHashMap<>();
    private static final BiMap<Integer, String> internal = HashBiMap.create();
    private static int IDZ = 0;

    public static void open(Player player, String id, ItemStack pattern) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        var title = Component.translatable("epp.pattern." + id);
        MenuProvider menu = warp(internal.inverse().get(id), pattern, (wnd, p, pl) -> {
            var f = factory.get(id);
            var t = types.get(id);
            return f.create(t, wnd, player.level(), pattern);
        }, title);
        player.openMenu(menu);
    }

    private static ExtendedScreenHandlerFactory warp(int id, ItemStack pattern, MenuConstructor con, Component name) {
        return new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeVarInt(id);
                buf.writeItem(pattern);
            }

            @Override
            public @NotNull Component getDisplayName() {
                return name;
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inv, Player player) {
                return con.createMenu(i, inv, player);
            }
        };
    }

    private static AbstractContainerMenu from(int containerId, Inventory inv, FriendlyByteBuf packetBuf) {
        var id = packetBuf.readVarInt();
        var world = inv.player.level();
        var stack = packetBuf.readItem();
        var f = factory.get(internal.get(id));
        var t = types.get(internal.get(id));
        return f.create(t, containerId, world, stack);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractContainerMenu> MenuType<T> register(String id, IContainerPattern containerFactory) {
        factory.put(id, containerFactory);
        internal.put(IDZ, id);
        IDZ++;
        var type = new ExtendedScreenHandlerType<>(PatternGuiHandler::from);
        types.put(id, type);
        return (MenuType<T>) type;
    }

    @FunctionalInterface
    public interface IContainerPattern {

        ContainerPattern create(MenuType<?> menuType, int id, Level world, ItemStack pattern);

    }

}
