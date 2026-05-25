package com.glodblock.github.extendedae.mixins;

import appeng.api.parts.IPartItem;
import appeng.client.hooks.RenderBlockOutlineHook;
import appeng.core.AEConfig;
import appeng.parts.PartPlacement;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.items.ItemPackedDevice;
import com.glodblock.github.extendedae.util.Ae2ReflectClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBlockOutlineHook.class)
public abstract class MixinRenderBlockOutlineHook {

    @Inject(
            method = "handleEvent",
            at = @At("TAIL"),
            remap = false
    )
    private static void renderPackedDevicePreview(ExtractBlockOutlineRenderStateEvent evt, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        assert player != null;
        var itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        var blockHitResult = evt.getHitResult();
        if (AEConfig.instance().isPlacementPreviewEnabled()) {
            if (!itemInHand.isEmpty() && itemInHand.getItem() instanceof ItemPackedDevice) {
                if (!itemInHand.has(EAESingletons.IS_PART)) {
                    return;
                }
                if (Boolean.TRUE.equals(itemInHand.get(EAESingletons.IS_PART))) {
                    var data = itemInHand.get(EAESingletons.TAPE_PART_DATA);
                    if (data == null) {
                        return;
                    }
                    BuiltInRegistries.ITEM.get(data.id()).ifPresent(item -> {
                        if (item instanceof IPartItem<?> partItem) {
                            var part = partItem.createPart();
                            var placement = PartPlacement.getPartPlacement(player, player.level(), itemInHand, blockHitResult.getBlockPos(), blockHitResult.getDirection(), blockHitResult.getLocation());
                            if (placement != null) {
                                var cameraRelativePos = new Vec3(
                                        placement.pos().getX() - evt.getCamera().position().x,
                                        placement.pos().getY() - evt.getCamera().position().y,
                                        placement.pos().getZ() - evt.getCamera().position().z);
                                evt.addCustomRenderer(Ae2ReflectClient.createPartPreviewRender(placement, part, cameraRelativePos));
                            }
                        }
                    });
                }
            }
        }
    }

}
