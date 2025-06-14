package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.generations;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.BlockDataController;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.Range;

public class BlockPopulator extends org.bukkit.generator.BlockPopulator {

    @Override
    public void populate(@Nonnull World world, @Nonnull Random random, @Nonnull Chunk source) {
        List<ProjectAddon> addons = RykenSlimefunCustomizer.addonManager.getAllValues();

        for (ProjectAddon addon : addons) {
            List<GenerationInfo> generationInfos = addon.getGenerationInfos();

            for (GenerationInfo generationInfo : generationInfos) {
                List<GenerationArea> areas = generationInfo.getAreas();

                for (GenerationArea area : areas) {
                    if (area.getEnvironment() != world.getEnvironment()) continue;

                    for (int i = 0; i < area.getAmount(); i++)
                        generateNext(source.getX(), source.getZ(), world, random, generationInfo, area);
                }
            }
        }
    }

    private void generateNext(
            int chunkX,
            int chunkZ,
            @Nonnull World world,
            @Nonnull Random random,
            @Nonnull GenerationInfo generationInfo,
            @Nonnull GenerationArea area) {
        Range height = area.getHeight();
        int h = height.getDistance() + 1;
        int r;
        double s2 = random.nextDouble(0, h);

        double sTop = (height.getMax() - area.getMost() + 1);
        if (s2 < sTop) {
            int h2MaxHeight = (int) (s2 * 2);
            r = height.getMax() - h2MaxHeight;
        } else {
            s2 -= sTop;
            int h2MinHeight = (int) (s2 * 2);
            r = height.getMin() + h2MinHeight;
        }

        int centerX = (chunkX << 4) + random.nextInt(16);
        int centerY = r;
        int centerZ = (chunkZ << 4) + random.nextInt(16);

        for (int i = 0; i < area.getSize().getRandomBetween(random); i++) {
            Location location = new Location(world, centerX, centerY, centerZ);
            Block block = world.getBlockAt(centerX, centerY, centerZ);
            if (!(centerX >= (chunkX << 4)
                    && centerX < (chunkX << 4) + 16
                    && centerZ >= (chunkZ << 4)
                    && centerZ < (chunkZ << 4) + 16)) {
                break;
            }
            if (block.getType() != area.getReplacement()) break;

            SlimefunItemStack slimefunItemStack = generationInfo.getSlimefunItemStack();

            block.setType(slimefunItemStack.getType(), false);
            if (slimefunItemStack.getType() == Material.PLAYER_HEAD) {
                slimefunItemStack.getSkullTexture().ifPresent(
                        skull -> PlayerHead.setSkin(block, PlayerSkin.fromBase64(skull), false)
                );
            }

            BlockDataController controller = Slimefun.getDatabaseManager().getBlockDataController();
            controller.createBlock(
                    location, generationInfo.getSlimefunItemStack().getItemId());

            r = random.nextInt(0, 3);
            if (r == 0) {
                centerX++;
            } else if (r == 1) {
                centerY++;
            } else if (r == 2) {
                centerZ++;
            }
        }
    }
}
