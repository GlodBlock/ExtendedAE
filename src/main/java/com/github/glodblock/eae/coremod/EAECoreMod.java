package com.github.glodblock.eae.coremod;

import com.github.glodblock.eae.EAETags;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@IFMLLoadingPlugin.Name(EAETags.MOD_ID)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.TransformerExclusions("com.github.glodblock.eae.coremod")
public class EAECoreMod implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { EAECoreMod.class.getPackage().getName() + ".EAEClassTransformer" };
    }

    @Override
    public @Nullable String getModContainerClass() {
        return null;
    }


    @Override
    public @Nullable String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public @Nullable String getAccessTransformerClass() {
        return null;
    }
}
