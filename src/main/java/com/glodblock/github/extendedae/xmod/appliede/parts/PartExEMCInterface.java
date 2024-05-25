package com.glodblock.github.extendedae.xmod.appliede.parts;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEngBase;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.IPage;
import com.glodblock.github.extendedae.xmod.appliede.containers.ContainerExEMCInterface;
import gripe._90.appliede.me.misc.EMCInterfaceLogic;
import gripe._90.appliede.part.EMCInterfacePart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

public class PartExEMCInterface extends EMCInterfacePart implements IPage {

    public static List<ResourceLocation> MODELS = Arrays.asList(
            new ResourceLocation(ExtendedAE.MODID, "part/ex_emc_interface"),
            new ResourceLocation(AppEngBase.MOD_ID, "part/interface_on"),
            new ResourceLocation(AppEngBase.MOD_ID, "part/interface_off"),
            new ResourceLocation(AppEngBase.MOD_ID, "part/interface_has_channel")
    );

    public static final PartModel MODELS_OFF = new PartModel(MODELS.get(0), MODELS.get(2));
    public static final PartModel MODELS_ON = new PartModel(MODELS.get(0), MODELS.get(1));
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODELS.get(0), MODELS.get(3));

    private int page = 0;

    public PartExEMCInterface(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(ContainerExEMCInterface.TYPE, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExEMCInterface.TYPE, player, subMenu.getLocator());
    }

    @Override
    protected EMCInterfaceLogic createLogic() {
        return new EMCInterfaceLogic(this.getMainNode(), this, this.getPartItem().asItem(), 36);
    }

    @Override
    public boolean onPartActivate(Player p, InteractionHand hand, Vec3 pos) {
        if (!p.getCommandSenderWorld().isClientSide()) {
            openMenu(p, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return this.page;
    }
}
