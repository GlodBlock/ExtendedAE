package com.glodblock.github.extendedae.mixins;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.networktool.NetworkStatusScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;
import appeng.menu.me.networktool.MachineGroup;
import appeng.menu.me.networktool.NetworkStatus;
import appeng.menu.me.networktool.NetworkStatusMenu;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CHighlightMachines;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(NetworkStatusScreen.class)
public class MixinNetworkStatusScreen extends AEBaseScreen<NetworkStatusMenu> {

    @Shadow(remap = false)
    @Final
    private static int ROWS;
    @Shadow(remap = false)
    @Final
    private static int COLUMNS;
    @Shadow(remap = false)
    @Final
    private static  int TABLE_X;
    @Shadow(remap = false)
    @Final
    private static  int TABLE_Y;
    @Shadow(remap = false)
    @Final
    private static int CELL_WIDTH;
    @Shadow(remap = false)
    @Final
    private static int CELL_HEIGHT;

    @Shadow(remap = false)
    @Final
    private Scrollbar scrollbar;

    @Shadow(remap = false)
    private NetworkStatus status;

    public MixinNetworkStatusScreen(NetworkStatusMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @SuppressWarnings("unchecked")
    @Redirect(
            method = "drawFG",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0),
            remap = false
    )
    private <E> boolean addHint(List<E> instance, E e) {
        instance.add(e);
        instance.add((E) Component.translatable("gui.extendedae.network_status.highlight_hint"));
        return false;
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (Screen.hasShiftDown()) {
            int x = 0;
            int y = 0;
            final int viewStart = this.scrollbar.getCurrentScroll() * COLUMNS;
            final int viewEnd = viewStart + COLUMNS * ROWS;
            List<MachineGroup> machines = new ArrayList<>(this.status.getGroupedMachines());
            machines.sort(MachineGroup.COMPARATOR);
            for (int i = viewStart; i < Math.min(viewEnd, machines.size()); i++) {
                MachineGroup entry = machines.get(i);
                int cellX = TABLE_X + x * CELL_WIDTH;
                int cellY = TABLE_Y + y * CELL_HEIGHT;
                if (isHovering(cellX, cellY, CELL_WIDTH, CELL_HEIGHT, xCoord, yCoord)) {
                    EAENetworkHandler.INSTANCE.sendToServer(new CHighlightMachines(entry.getDisplay(), entry.isMissingChannel()));
                    return true;
                }
                if (++x >= COLUMNS) {
                    y++;
                    x = 0;
                }
            }
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

}
