package com.glodblock.github.extendedae.container;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.tileentities.TileCircuitCutter;
import com.glodblock.github.extendedae.container.helper.DirectionSet;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContainerCircuitCutter extends UpgradeableMenu<TileCircuitCutter> implements IProgressProvider, IActionHolder {

    @GuiSync(3)
    public int processingTime = -1;

    @GuiSync(8)
    public YesNo autoExport = YesNo.NO;

    @GuiSync(9)
    public DirectionSet outputSides = new DirectionSet(new ArrayList<>());

    private final ActionMap actions = ActionMap.create();

    public static final MenuType<ContainerCircuitCutter> TYPE = MenuTypeBuilder
            .create(ContainerCircuitCutter::new, TileCircuitCutter.class)
            .buildUnregistered(ExtendedAE.id("circuit_cutter"));

    public ContainerCircuitCutter(int id, Inventory ip, TileCircuitCutter host) {
        super(TYPE, id, ip, host);
        this.addSlot(new AppEngSlot(host.getInput(), 0), SlotSemantics.MACHINE_INPUT);
        this.addSlot(new OutputSlot(host.getOutput(), 0, null), SlotSemantics.MACHINE_OUTPUT);
        this.actions.put("set_side", o -> this.setOutputSide(o.get(0), o.get(1)));
    }

    private void setOutputSide(String name, boolean value) {
        var side = Direction.byName(name);
        if (value) {
            this.getHost().getOutputSides().add(side);
        } else {
            this.getHost().getOutputSides().remove(side);
        }
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.autoExport = cm.getSetting(Settings.AUTO_EXPORT);
        this.outputSides.clear();
        this.outputSides.addAll(this.getHost().getOutputSides());
    }

    @Override
    protected void standardDetectAndSendChanges() {
        if (isServerSide()) {
            this.processingTime = getHost().getProgress();
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public int getCurrentProgress() {
        return this.processingTime;
    }

    @Override
    public int getMaxProgress() {
        return TileCircuitCutter.MAX_PROGRESS;
    }

    public YesNo getAutoExport() {
        return autoExport;
    }

    public List<Direction> getOutputSides() {
        return outputSides.sides();
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

}
