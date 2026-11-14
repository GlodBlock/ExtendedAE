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
import com.glodblock.github.extendedae.common.me.DirectionSet;
import com.glodblock.github.extendedae.common.tileentities.TilePulverizer;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContainerPulverizer extends UpgradeableMenu<TilePulverizer> implements IProgressProvider, IActionHolder {

    @GuiSync(3)
    public double processing = -1;
    @GuiSync(4)
    public double maxProcess = 1;

    @GuiSync(8)
    public YesNo autoExport = YesNo.NO;

    @GuiSync(9)
    public DirectionSet outputSides = new DirectionSet(new ArrayList<>());

    private final ActionMap actions = ActionMap.create();

    public static final MenuType<@NotNull ContainerPulverizer> TYPE = MenuTypeBuilder
            .create(ContainerPulverizer::new, TilePulverizer.class)
            .buildUnregistered(ExtendedAE.id("pulverizer"));

    public ContainerPulverizer(int id, Inventory ip, TilePulverizer host) {
        super(TYPE, id, ip, host);
        this.addSlot(new AppEngSlot(host.getInput(), 0), SlotSemantics.MACHINE_INPUT);
        for (int x = 0; x < TilePulverizer.OUTPUT_SLOTS; x ++) {
            this.addSlot(new OutputSlot(host.getOutput(), x, null), SlotSemantics.MACHINE_OUTPUT);
        }
        this.actions.put("set_side", o -> this.setOutputSide(o.get(Direction.class), o.getBoolean()));
    }

    private void setOutputSide(Direction side, boolean value) {
        if (value) {
            this.getHost().getOutputSides().add(side);
        } else {
            this.getHost().getOutputSides().remove(side);
        }
        this.getHost().saveChanges();
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.autoExport = cm.getSetting(Settings.AUTO_EXPORT);
        this.outputSides.reload(this.getHost().getOutputSides());
    }

    @Override
    protected void standardDetectAndSendChanges() {
        if (this.isServerSide()) {
            this.processing = this.getHost().getProgress();
            this.maxProcess = this.getHost().getMaxProcess();
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public int getCurrentProgress() {
        return (int) this.processing;
    }

    @Override
    public int getMaxProgress() {
        return (int) this.maxProcess;
    }

    public YesNo getAutoExport() {
        return autoExport;
    }

    public List<Direction> getOutputSides() {
        return outputSides.asList();
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

}
