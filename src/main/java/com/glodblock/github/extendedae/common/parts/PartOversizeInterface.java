package com.glodblock.github.extendedae.common.parts;

import appeng.api.AECapabilities;
import appeng.api.parts.IPartItem;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.stacks.AEKeyTypes;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import com.glodblock.github.extendedae.common.inventory.OversizeConfigInv;
import com.glodblock.github.extendedae.container.ContainerExInterface;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import net.minecraft.world.entity.player.Player;

public class PartOversizeInterface extends PartExInterface {

    public PartOversizeInterface(IPartItem<?> partItem) {
        super(partItem);
        var logic = this.getInterfaceLogic();
        Ae2Reflect.setInterfaceConfig(logic, new OversizeConfigInv(AEKeyTypes.getAll(), null, GenericStackInv.Mode.CONFIG_STACKS, 36, () -> Ae2Reflect.onInterfaceConfigChange(logic), false));
        Ae2Reflect.setInterfaceStorage(logic, new OversizeConfigInv(AEKeyTypes.getAll(), (slot, key) -> Ae2Reflect.isInterfaceSlotAllowed(logic, slot, key), GenericStackInv.Mode.STORAGE, 36, () -> Ae2Reflect.onInterfaceStorageChange(logic), false));
        this.getConfig().useRegisteredCapacities();
        this.getStorage().useRegisteredCapacities();
    }

    @Override
    public void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(ContainerExInterface.TYPE_OVERSIZE, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExInterface.TYPE_OVERSIZE, player, subMenu.getLocator());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void registerCapability(RegisterPartCapabilitiesEvent event) {
        event.register(
                AECapabilities.GENERIC_INTERNAL_INV,
                (part, _) -> part.getInterfaceLogic().getStorage(),
                PartOversizeInterface.class
        );
        event.register(
                AECapabilities.ME_STORAGE,
                (part, _) -> part.getInterfaceLogic().getInventory(),
                PartOversizeInterface.class
        );
    }

}
