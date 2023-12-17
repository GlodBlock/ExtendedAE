package com.github.glodblock.epp.mixins;

import com.github.glodblock.epp.common.me.taglist.TagExpParser;
import com.google.common.collect.Interner;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TagKey.class)
public class MixinTagKey {

    @Redirect(
            method = "create",
            at = @At(value = "INVOKE", target = "com/google/common/collect/Interner.intern (Ljava/lang/Object;)Ljava/lang/Object;"),
            remap = false
    )
    private static <E> E callback(Interner<E> instance, E e) {
        if (e instanceof TagKey<?> key) {
            if (key.registry() == Registries.ITEM || key.registry() == Registries.FLUID) {
                TagExpParser.recordTag(key);
            }
        }
        return instance.intern(e);
    }

}
