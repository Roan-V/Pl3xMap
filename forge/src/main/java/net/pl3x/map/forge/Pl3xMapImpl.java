package net.pl3x.map.forge;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.WorldConfig;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Pl3xMapImpl extends Pl3xMap {
    private final Pl3xMapForge mod;

    @SuppressWarnings("deprecation")
    private final RandomSource randomSource = RandomSource.createThreadSafe();

    public Pl3xMapImpl(@NonNull Pl3xMapForge mod) {
        super();

        this.mod = mod;

        try {
            Field api = Provider.class.getDeclaredField("api");
            api.setAccessible(true);
            api.set(null, this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        init();
    }

    @Override
    public void useJar(@NonNull Consumer<Path> consumer) {
        try {
            FileUtil.openJar(ModList.get().getModContainerById("pl3xmap").orElseThrow().getModInfo().getOwningFile().getFile().getFilePath(), fs -> consumer.accept(fs.getPath("/")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NonNull
    public Path getMainDir() {
        return FMLPaths.GAMEDIR.get().resolve("config").resolve("pl3xmap");
    }

    @Override
    public int getColorForPower(byte power) {
        return RedStoneWireBlock.getColorForPower(power);
    }

    @Override
    public net.pl3x.map.core.world.@Nullable Block getFlower(@NonNull World world, net.pl3x.map.core.world.@NonNull Biome biome, int blockX, int blockY, int blockZ) {
        // https://github.com/Draradech/FlowerMap (CC0-1.0 license)
        Biome nms = world.<ServerLevel>getLevel().registryAccess().registryOrThrow(Registries.BIOME).get(new ResourceLocation(biome.id()));
        if (nms == null) {
            return null;
        }
        List<ConfiguredFeature<?, ?>> flowers = nms.getGenerationSettings().getFlowerFeatures();
        if (flowers.isEmpty()) {
            return null;
        }
        RandomPatchConfiguration config = (RandomPatchConfiguration) flowers.get(0).config();
        SimpleBlockConfiguration flower = (SimpleBlockConfiguration) config.feature().value().feature().value().config();
        Block block = flower.toPlace().getState(this.randomSource, new BlockPos(blockX, blockY, blockZ)).getBlock();
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
        return key == null ? null : getBlockRegistry().get(key.toString());
    }

    @Override
    public void loadBlocks() {
        for (Map.Entry<ResourceKey<Block>, Block> entry : this.mod.getServer().registryAccess().registryOrThrow(Registries.BLOCK).entrySet()) {
            String id = entry.getKey().location().toString();
            int color = entry.getValue().defaultMaterialColor().col;
            getBlockRegistry().register(id, color);
        }
        getBlockRegistry().saveToDisk();
    }

    @Override
    public void loadWorlds() {
        this.mod.getServer().getAllLevels().forEach(level -> {
            String name = level.dimension().location().toString();
            WorldConfig worldConfig = new WorldConfig(name);
            if (worldConfig.ENABLED) {
                getWorldRegistry().register(new ForgeWorld(level, name, worldConfig));
            }
        });
    }

    @Override
    public void loadPlayers() {
        this.mod.getServer().getPlayerList().getPlayers().forEach(player ->
                getPlayerRegistry().register(player.getUUID().toString(), new ForgePlayer(player)));
    }

    @Override
    public int getMaxPlayers() {
        return this.mod.getServer().getMaxPlayers();
    }
}
