package com.glodblock.github.extendedae.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("all")
public class DisplayServerLevel extends Level implements ServerLevelAccessor {

    private final Level delegate;

    public static DisplayServerLevel create(Level world) {
        return new DisplayServerLevel(world);
    }

    @Override
    public boolean isClientSide() {
        return delegate.isClientSide();
    }

    @Override
    public void sendBlockUpdated(BlockPos p_46612_, BlockState p_46613_, BlockState p_46614_, int p_46615_) {
        delegate.sendBlockUpdated(p_46612_, p_46613_, p_46614_, p_46615_);
    }

    @Override
    public void playSeededSound(@Nullable Player p_262953_, double p_263004_, double p_263398_, double p_263376_, Holder<SoundEvent> p_263359_, SoundSource p_263020_, float p_263055_, float p_262914_, long p_262991_) {
        delegate.playSeededSound(p_262953_, p_263004_, p_263398_, p_263376_, p_263359_, p_263020_, p_263055_, p_262914_, p_262991_);
    }

    @Override
    public void playSeededSound(@Nullable Player p_220372_, Entity p_220373_, Holder<SoundEvent> p_263500_, SoundSource p_220375_, float p_220376_, float p_220377_, long p_220378_) {
        delegate.playSeededSound(p_220372_, p_220373_, p_263500_, p_220375_, p_220376_, p_220377_, p_220378_);
    }

    @Override
    public String gatherChunkSourceStats() {
        return delegate.gatherChunkSourceStats();
    }

    @Override
    @javax.annotation.Nullable
    public Entity getEntity(int p_46492_) {
        return delegate.getEntity(p_46492_);
    }

    @Override
    @javax.annotation.Nullable
    public MapItemSavedData getMapData(String p_46650_) {
        return delegate.getMapData(p_46650_);
    }

    @Override
    public void setMapData(String p_151533_, MapItemSavedData p_151534_) {
        delegate.setMapData(p_151533_, p_151534_);
    }

    @Override
    public int getFreeMapId() {
        return delegate.getFreeMapId();
    }

    @Override
    public void destroyBlockProgress(int p_46506_, BlockPos p_46507_, int p_46508_) {
        delegate.destroyBlockProgress(p_46506_, p_46507_, p_46508_);
    }

    @Override
    public Scoreboard getScoreboard() {
        return delegate.getScoreboard();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return delegate.getRecipeManager();
    }

    @Override
    public LevelEntityGetter<Entity> getEntities() {
        return new EmptyLevelEntityGetter<>();
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return delegate.getBlockTicks();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return delegate.getFluidTicks();
    }

    @Override
    public ChunkSource getChunkSource() {
        return delegate.getChunkSource();
    }

    @Override
    public void levelEvent(@Nullable Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {
        delegate.levelEvent(p_46771_, p_46772_, p_46773_, p_46774_);
    }

    @Override
    public void gameEvent(GameEvent p_220404_, Vec3 p_220405_, GameEvent.Context p_220406_) {
        delegate.gameEvent(p_220404_, p_220405_, p_220406_);
    }

    @Override
    public List<? extends Player> players() {
        return delegate.players();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int p_204159_, int p_204160_, int p_204161_) {
        return delegate.getUncachedNoiseBiome(p_204159_, p_204160_, p_204161_);
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return delegate.enabledFeatures();
    }

    @Override
    public float getShade(Direction p_45522_, boolean p_45523_) {
        return delegate.getShade(p_45522_, p_45523_);
    }

    private DisplayServerLevel(Level world) {
        super(
                (WritableLevelData) world.getLevelData(),
                world.dimension(),
                world.registryAccess(),
                world.dimensionTypeRegistration(),
                world.getProfilerSupplier(),
                false,
                world.isDebug(),
                0,
                10000
        );
        this.delegate = world;
    }

    @Override
    public ServerLevel getLevel() {
        throw new UnsupportedOperationException();
    }

}
