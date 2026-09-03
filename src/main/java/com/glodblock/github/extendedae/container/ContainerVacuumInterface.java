package com.glodblock.github.extendedae.container;

import appeng.api.config.Settings;
import appeng.api.util.IConfigManager;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.tileentities.TileVacuumInterface;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerVacuumInterface extends UpgradeableMenu<TileVacuumInterface> implements IActionHolder {

    public static final MenuType<ContainerVacuumInterface> TYPE = MenuTypeBuilder
            .create(ContainerVacuumInterface::new, TileVacuumInterface.class)
            .withInitialData(
                    (tile, buf) -> {
                        buf.writeLong(tile.getSize().asLong());
                        buf.writeLong(tile.getOffset().asLong());
                        buf.writeBoolean(tile.isDisplayArea());
                    },
                    (tile, container, buf) -> {
                        container.size = buf.readLong();
                        container.offset = buf.readLong();
                        container.displayArea = buf.readBoolean();
                    }
            )
            .buildUnregistered(ExtendedAE.id("vacuum_interface"));

    @GuiSync(10)
    public long size;
    @GuiSync(11)
    public long offset;
    @GuiSync(12)
    public boolean displayArea;
    private final ActionMap actions = ActionMap.create();

    public ContainerVacuumInterface(int id, Inventory ip, TileVacuumInterface host) {
        super(TYPE, id, ip, host);
        this.actions.put("display", o -> this.setDisplay(o.get(0)));
        this.actions.put("config_data", o -> this.setData(o.get(0), o.get(1), o.get(2)));
    }

    private void setData(int delta, int seq, boolean size) {
        var lp =  size ? this.size : this.offset;
        BlockPos data = switch(seq) {
            case 0 -> BlockPos.of(lp).offset(delta, 0, 0);
            case 1 -> BlockPos.of(lp).offset(0, delta, 0);
            case 2 -> BlockPos.of(lp).offset(0, 0, delta);
            default -> null;
        };
        if (data != null) {
            if (size) {
                this.getHost().setSize(data);
            } else {
                this.getHost().setOffset(data);
            }
        }
    }

    public BlockPos getOffset() {
        return BlockPos.of(this.offset);
    }

    public BlockPos getSize() {
        return BlockPos.of(this.size);
    }

    private void setDisplay(boolean enable) {
        this.getHost().setDisplayArea(enable);
    }

    @Override
    public void broadcastChanges() {
        this.displayArea = this.getHost().isDisplayArea();
        this.size = this.getHost().getSize().asLong();
        this.offset = this.getHost().getOffset().asLong();
        super.broadcastChanges();
    }

    @Override
    protected void setupConfig() {
        this.addExpandableConfigSlots(getHost().getConfig(), 2, 9, 5);
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setFuzzyMode(cm.getSetting(Settings.FUZZY_MODE));
        this.setRedStoneMode(cm.getSetting(Settings.REDSTONE_CONTROLLED));
    }

    @Override
    public @NotNull ActionMap getActionMap() {
        return this.actions;
    }

}
