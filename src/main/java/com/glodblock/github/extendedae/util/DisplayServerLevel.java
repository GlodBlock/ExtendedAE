package com.glodblock.github.extendedae.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.TickPriority;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings({"deprecation", "NullableProblems", "NonExtendableApiUsage"})
public class DisplayServerLevel extends Level implements ServerLevelAccessor {

    private final Level delegate;

    public static DisplayServerLevel create(Level world) {
        return new DisplayServerLevel(world);
    }

    private DisplayServerLevel(Level world) {
        super(
                (WritableLevelData) world.getLevelData(),
                world.dimension(),
                world.registryAccess(),
                world.dimensionTypeRegistration(),
                false,
                world.isDebug(),
                0,
                10000
                );
        this.delegate = world;
    }

    @Override
    public void sendBlockUpdated(@NotNull BlockPos blockPos, @NotNull BlockState blockState, @NotNull BlockState blockState1, int i) {
        delegate.sendBlockUpdated(blockPos, blockState, blockState1, i);
    }

    @Override
    public void playSeededSound(@Nullable Entity player, double v, double v1, double v2, @NotNull Holder<SoundEvent> holder, @NotNull SoundSource soundSource, float v3, float v4, long l) {
        delegate.playSeededSound(player, v, v1, v2, holder, soundSource, v3, v4, l);
    }

    @Override
    public void playSeededSound(@Nullable Entity player, @NotNull Entity entity, @NotNull Holder<SoundEvent> holder, @NotNull SoundSource soundSource, float v, float v1, long l) {
        delegate.playSeededSound(player, entity, holder, soundSource, v, v1, l);
    }

    @Override
    public void explode(@Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float r, boolean fire, ExplosionInteraction interactionType, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, WeightedList<ExplosionParticleInfo> blockParticles, Holder<SoundEvent> explosionSound) {
        delegate.explode(source, damageSource, damageCalculator, x, y, z, r, fire, interactionType);
    }

    @Override
    public @NotNull String gatherChunkSourceStats() {
        return delegate.gatherChunkSourceStats();
    }

    @Override
    public void setRespawnData(LevelData.RespawnData respawnData) {
        delegate.setRespawnData(respawnData);
    }

    @Override
    public LevelData.RespawnData getRespawnData() {
        return delegate.getRespawnData();
    }

    @Override
    public Entity getEntity(int i) {
        return delegate.getEntity(i);
    }

    @Override
    public Collection<? extends PartEntity<?>> dragonParts() {
        return delegate.dragonParts();
    }

    @Override
    public @NotNull TickRateManager tickRateManager() {
        return delegate.tickRateManager();
    }

    @Override
    public MapItemSavedData getMapData(@NotNull MapId mapId) {
        return delegate.getMapData(mapId);
    }

    @Override
    public void destroyBlockProgress(int i, @NotNull BlockPos blockPos, int i1) {
        delegate.destroyBlockProgress(i, blockPos, i1);
    }

    @Override
    public @NotNull Scoreboard getScoreboard() {
        return delegate.getScoreboard();
    }

    @Override
    public RecipeAccess recipeAccess() {
        return delegate.recipeAccess();
    }

    @Override
    public @NotNull LevelEntityGetter<Entity> getEntities() {
        return new EmptyLevelEntityGetter<>();
    }

    @Override
    public ClockManager clockManager() {
        return delegate.clockManager();
    }

    @Override
    public EnvironmentAttributeSystem environmentAttributes() {
        return delegate.environmentAttributes();
    }

    @Override
    public @NotNull PotionBrewing potionBrewing() {
        return delegate.potionBrewing();
    }

    @Override
    public FuelValues fuelValues() {
        return delegate.fuelValues();
    }

    @Override
    public @NotNull LevelTickAccess<Block> getBlockTicks() {
        return delegate.getBlockTicks();
    }

    @Override
    public @NotNull LevelTickAccess<Fluid> getFluidTicks() {
        return delegate.getFluidTicks();
    }

    @Override
    public @NotNull ChunkSource getChunkSource() {
        return delegate.getChunkSource();
    }

    @Override
    public void levelEvent(@Nullable Entity player, int i, @NotNull BlockPos blockPos, int i1) {
        delegate.levelEvent(player, i, blockPos, i1);
    }

    @Override
    public void gameEvent(@NotNull Holder<GameEvent> holder, @NotNull Vec3 vec3, GameEvent.@NotNull Context context) {
        delegate.gameEvent(holder, vec3, context);
    }

    @Override
    public @NotNull List<? extends Player> players() {
        return delegate.players();
    }

    @Override
    public @NotNull Holder<Biome> getUncachedNoiseBiome(int i, int i1, int i2) {
        return delegate.getUncachedNoiseBiome(i, i1, i2);
    }

    @Override
    public int getSeaLevel() {
        return delegate.getSeaLevel();
    }

    @Override
    public @NotNull FeatureFlagSet enabledFeatures() {
        return delegate.enabledFeatures();
    }

    @Override
    public ServerLevel getLevel() {
        // No way
        return null;
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos pos) {
        return new DifficultyInstance(this.getDifficulty(), 0, 0, 0);
    }

    @Override
    public void scheduleTick(@NotNull BlockPos pos, @NotNull Block block, int delay, @NotNull TickPriority priority) {
        delegate.scheduleTick(pos, block, delay, priority);
    }

    @Override
    public void scheduleTick(@NotNull BlockPos pos, @NotNull Block block, int delay) {
        delegate.scheduleTick(pos, block, delay);
    }

    @Override
    public void scheduleTick(@NotNull BlockPos pos, @NotNull Fluid fluid, int delay, @NotNull TickPriority priority) {
        delegate.scheduleTick(pos, fluid, delay, priority);
    }

    @Override
    public void scheduleTick(@NotNull BlockPos pos, @NotNull Fluid fluid, int delay) {
        delegate.scheduleTick(pos, fluid, delay);
    }

    @Override
    public @NotNull Difficulty getDifficulty() {
        return delegate.getDifficulty();
    }

    @Override
    public boolean hasChunk(int chunkX, int chunkZ) {
        return delegate.hasChunk(chunkX, chunkZ);
    }

    @Override
    public void neighborShapeChanged(Direction direction, BlockPos pos, BlockPos neighborPos, BlockState neighborState, @Block.UpdateFlags int updateFlags, int updateLimit) {
        delegate.neighborShapeChanged(direction, pos, neighborPos, neighborState, updateFlags, updateLimit);
    }

    @Override
    public void playSound(@Nullable Entity player, @NotNull BlockPos pos, @NotNull SoundEvent sound, @NotNull SoundSource source) {
        delegate.playSound(player, pos, sound, source);
    }

    @Override
    public void levelEvent(int type, @NotNull BlockPos pos, int data) {
        delegate.levelEvent(type, pos, data);
    }

    @Override
    public void gameEvent(@Nullable Entity entity, @NotNull Holder<GameEvent> event, @NotNull Vec3 position) {
        delegate.gameEvent(entity, event, position);
    }

    @Override
    public void gameEvent(@Nullable Entity entity, @NotNull Holder<GameEvent> event, @NotNull BlockPos pos) {
        delegate.gameEvent(entity, event, pos);
    }

    @Override
    public void gameEvent(@NotNull Holder<GameEvent> event, @NotNull BlockPos pos, GameEvent.@NotNull Context context) {
        delegate.gameEvent(event, pos, context);
    }

    @Override
    public <T extends BlockEntity> @NotNull Optional<T> getBlockEntity(@NotNull BlockPos pos, @NotNull BlockEntityType<T> type) {
        return delegate.getBlockEntity(pos, type);
    }

    @Override
    public @NotNull List<VoxelShape> getEntityCollisions(@Nullable Entity entity, @NotNull AABB collisionBox) {
        return delegate.getEntityCollisions(entity, collisionBox);
    }

    @Override
    public boolean isUnobstructed(@Nullable Entity entity, @NotNull VoxelShape shape) {
        return delegate.isUnobstructed(entity, shape);
    }

    @Override
    public @NotNull BlockPos getHeightmapPos(Heightmap.@NotNull Types heightmapType, @NotNull BlockPos pos) {
        return delegate.getHeightmapPos(heightmapType, pos);
    }

    @Override
    public <T extends Entity> @NotNull List<T> getEntitiesOfClass(@NotNull Class<T> clazz, @NotNull AABB area, @NotNull Predicate<? super T> filter) {
        return delegate.getEntitiesOfClass(clazz, area, filter);
    }

    @Override
    public @NotNull List<Entity> getEntities(@Nullable Entity entity, @NotNull AABB area) {
        return delegate.getEntities(entity, area);
    }

    @Override
    public <T extends Entity> @NotNull List<T> getEntitiesOfClass(@NotNull Class<T> entityClass, @NotNull AABB area) {
        return delegate.getEntitiesOfClass(entityClass, area);
    }

    @Override
    @Nullable
    public Player getNearestPlayer(double x, double y, double z, double distance, @Nullable Predicate<Entity> predicate) {
        return delegate.getNearestPlayer(x, y, z, distance, predicate);
    }

    @Override
    @Nullable
    public Player getNearestPlayer(@NotNull Entity entity, double distance) {
        return delegate.getNearestPlayer(entity, distance);
    }

    @Override
    @Nullable
    public Player getNearestPlayer(double x, double y, double z, double distance, boolean creativePlayers) {
        return delegate.getNearestPlayer(x, y, z, distance, creativePlayers);
    }

    @Override
    public boolean hasNearbyAlivePlayer(double x, double y, double z, double distance) {
        return delegate.hasNearbyAlivePlayer(x, y, z, distance);
    }

    @Override
    @Nullable
    public Player getPlayerByUUID(@NotNull UUID uniqueId) {
        return delegate.getPlayerByUUID(uniqueId);
    }

    @Override
    public @NotNull Holder<Biome> getBiome(@NotNull BlockPos pos) {
        return delegate.getBiome(pos);
    }

    @Override
    public @NotNull Stream<BlockState> getBlockStatesIfLoaded(@NotNull AABB aabb) {
        return delegate.getBlockStatesIfLoaded(aabb);
    }

    @Override
    public @NotNull Holder<Biome> getNoiseBiome(int i, int j, int k) {
        return delegate.getNoiseBiome(i, j, k);
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public boolean isEmptyBlock(BlockPos pos) {
        return delegate.isEmptyBlock(pos);
    }

    @Override
    public boolean canSeeSkyFromBelowWater(BlockPos pos) {
        return delegate.canSeeSkyFromBelowWater(pos);
    }

    @Override
    public float getPathfindingCostFromLightLevels(BlockPos blockPos) {
        return delegate.getPathfindingCostFromLightLevels(blockPos);
    }

    @Override
    @Deprecated
    public float getLightLevelDependentMagicValue(BlockPos blockPos) {
        return delegate.getLightLevelDependentMagicValue(blockPos);
    }

    @Override
    public int getDirectSignal(BlockPos pos, Direction direction) {
        return delegate.getDirectSignal(pos, direction);
    }

    @Override
    public ChunkAccess getChunk(BlockPos pos) {
        return delegate.getChunk(pos);
    }

    @Override
    public LevelChunk getChunk(int chunkX, int chunkZ) {
        return delegate.getChunk(chunkX, chunkZ);
    }

    @Override
    public ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus) {
        return delegate.getChunk(chunkX, chunkZ, requiredStatus);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return delegate.getWorldBorder();
    }

    @Override
    @Nullable
    public BlockGetter getChunkForCollisions(int chunkX, int chunkZ) {
        return delegate.getChunkForCollisions(chunkX, chunkZ);
    }

    @Override
    public boolean isWaterAt(BlockPos pos) {
        return delegate.isWaterAt(pos);
    }

    @Override
    public boolean containsAnyLiquid(AABB bb) {
        return delegate.containsAnyLiquid(bb);
    }

    @Override
    public int getMaxLocalRawBrightness(BlockPos pos) {
        return delegate.getMaxLocalRawBrightness(pos);
    }

    @Override
    public int getMaxLocalRawBrightness(BlockPos pos, int amount) {
        return delegate.getMaxLocalRawBrightness(pos, amount);
    }

    @Override
    @Deprecated
    public boolean hasChunkAt(int x, int z) {
        return delegate.hasChunkAt(x, z);
    }

    @Override
    @Deprecated
    public boolean hasChunkAt(BlockPos pos) {
        return delegate.hasChunkAt(pos);
    }

    @Override
    @Deprecated
    public boolean hasChunksAt(BlockPos from, BlockPos to) {
        return delegate.hasChunksAt(from, to);
    }

    @Override
    @Deprecated
    public boolean hasChunksAt(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return delegate.hasChunksAt(fromX, fromY, fromZ, toX, toY, toZ);
    }

    @Override
    @Deprecated
    public boolean hasChunksAt(int fromX, int fromZ, int toX, int toZ) {
        return delegate.hasChunksAt(fromX, fromZ, toX, toZ);
    }

    @Override
    public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<? extends T>> resourceKey) {
        return delegate.holderLookup(resourceKey);
    }

    @Override
    public int getBrightness(LightLayer lightType, BlockPos blockPos) {
        return delegate.getBrightness(lightType, blockPos);
    }

    @Override
    public int getRawBrightness(BlockPos blockPos, int amount) {
        return delegate.getRawBrightness(blockPos, amount);
    }

    @Override
    public boolean canSeeSky(BlockPos blockPos) {
        return delegate.canSeeSky(blockPos);
    }

    @Override
    public int getLightEmission(BlockPos pos) {
        return delegate.getLightEmission(pos);
    }

    @Override
    public Stream<BlockState> getBlockStates(AABB area) {
        return delegate.getBlockStates(area);
    }

    @Override
    public BlockHitResult isBlockInLine(ClipBlockStateContext context) {
        return delegate.isBlockInLine(context);
    }

    @Override
    public BlockHitResult clip(ClipContext context) {
        return delegate.clip(context);
    }

    @Override
    @Nullable
    public BlockHitResult clipWithInteractionOverride(Vec3 startVec, Vec3 endVec, BlockPos pos, VoxelShape shape,
                                                      BlockState state) {
        return delegate.clipWithInteractionOverride(startVec, endVec, pos, shape, state);
    }

    @Override
    public double getBlockFloorHeight(VoxelShape shape, Supplier<VoxelShape> belowShapeSupplier) {
        return delegate.getBlockFloorHeight(shape, belowShapeSupplier);
    }

    @Override
    public double getBlockFloorHeight(BlockPos pos) {
        return delegate.getBlockFloorHeight(pos);
    }

    @Override
    public int getSectionsCount() {
        return delegate.getSectionsCount();
    }

    @Override
    public boolean isOutsideBuildHeight(BlockPos pos) {
        return delegate.isOutsideBuildHeight(pos);
    }

    @Override
    public boolean isOutsideBuildHeight(int y) {
        return delegate.isOutsideBuildHeight(y);
    }

    @Override
    public int getSectionIndex(int y) {
        return delegate.getSectionIndex(y);
    }

    @Override
    public int getSectionIndexFromSectionY(int sectionIndex) {
        return delegate.getSectionIndexFromSectionY(sectionIndex);
    }

    @Override
    public int getSectionYFromSectionIndex(int sectionIndex) {
        return delegate.getSectionYFromSectionIndex(sectionIndex);
    }

    public static LevelHeightAccessor create(int minBuildHeight, int height) {
        return LevelHeightAccessor.create(minBuildHeight, height);
    }

    @Override
    public boolean isUnobstructed(BlockState state, BlockPos pos, CollisionContext context) {
        return delegate.isUnobstructed(state, pos, context);
    }

    @Override
    public boolean isUnobstructed(Entity entity) {
        return delegate.isUnobstructed(entity);
    }

    @Override
    public boolean noCollision(AABB collisionBox) {
        return delegate.noCollision(collisionBox);
    }

    @Override
    public boolean noCollision(Entity entity) {
        return delegate.noCollision(entity);
    }

    @Override
    public boolean noCollision(@Nullable Entity entity, AABB collisionBox) {
        return delegate.noCollision(entity, collisionBox);
    }

    @Override
    public Iterable<VoxelShape> getCollisions(@Nullable Entity entity, AABB collisionBox) {
        return delegate.getCollisions(entity, collisionBox);
    }

    @Override
    public Iterable<VoxelShape> getBlockCollisions(@Nullable Entity entity, AABB collisionBox) {
        return delegate.getBlockCollisions(entity, collisionBox);
    }

    @Override
    public boolean collidesWithSuffocatingBlock(@Nullable Entity entity, AABB box) {
        return delegate.collidesWithSuffocatingBlock(entity, box);
    }

    @Override
    public Optional<Vec3> findFreePosition(@Nullable Entity entity, VoxelShape shape, Vec3 pos, double x, double y,
                                           double z) {
        return delegate.findFreePosition(entity, shape, pos, x, y, z);
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState newState, int flags) {
        return delegate.setBlock(pos, newState, flags);
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        return delegate.destroyBlock(pos, dropBlock);
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity) {
        return delegate.destroyBlock(pos, dropBlock, entity);
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        return delegate.addFreshEntity(entity);
    }

    @Override
    public void gameEvent(ResourceKey<GameEvent> p_316780_, BlockPos p_316509_, GameEvent.Context p_316524_) {
        delegate.gameEvent(p_316780_, p_316509_, p_316524_);
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int range) {
        return delegate.isAreaLoaded(center, range);
    }

    @Override
    @ApiStatus.NonExtendable
    public @Nullable AuxiliaryLightManager getAuxLightManager(BlockPos pos) {
        return delegate.getAuxLightManager(pos);
    }

    @Override
    public @Nullable AuxiliaryLightManager getAuxLightManager(ChunkPos pos) {
        return delegate.getAuxLightManager(pos);
    }

    @Override
    public ModelData getModelData(BlockPos pos) {
        return delegate.getModelData(pos);
    }

    @Override
    public boolean noBlockCollision(@Nullable Entity pEntity, AABB pBoundingBox) {
        return delegate.noBlockCollision(pEntity, pBoundingBox);
    }

    @Override
    public Optional<BlockPos> findSupportingBlock(Entity pEntity, AABB pBox) {
        return delegate.findSupportingBlock(pEntity, pBox);
    }

    @Override
    public int getDirectSignalTo(BlockPos pPos) {
        return delegate.getDirectSignalTo(pPos);
    }

    @Override
    public int getControlInputSignal(BlockPos pPos, Direction pDirection, boolean pDiodesOnly) {
        return delegate.getControlInputSignal(pPos, pDirection, pDiodesOnly);
    }

    @Override
    public boolean hasSignal(BlockPos pPos, Direction pDirection) {
        return delegate.hasSignal(pPos, pDirection);
    }

    @Override
    public int getSignal(BlockPos pPos, Direction pDirection) {
        return delegate.getSignal(pPos, pDirection);
    }

    @Override
    public boolean hasNeighborSignal(BlockPos pPos) {
        return delegate.hasNeighborSignal(pPos);
    }

    @Override
    public int getBestNeighborSignal(BlockPos pPos) {
        return delegate.getBestNeighborSignal(pPos);
    }

}
