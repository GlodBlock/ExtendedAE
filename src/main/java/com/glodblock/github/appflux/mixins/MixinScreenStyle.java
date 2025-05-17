package com.glodblock.github.appflux.mixins;

import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import com.glodblock.github.appflux.util.helpers.IStyleAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ScreenStyle.class)
public abstract class MixinScreenStyle implements IStyleAccessor {

    @Final
    @Shadow(remap = false)
    private Map<String, Blitter> images;
    @Final
    @Shadow(remap = false)
    private Map<String, WidgetStyle> widgets;

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public Map<String, Blitter> getImages() {
        return this.images;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public Map<String, WidgetStyle> getWidgets() {
        return this.widgets;
    }
}
