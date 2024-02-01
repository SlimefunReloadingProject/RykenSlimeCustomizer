package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs;

import com.google.common.annotations.Beta;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Beta
public class CustomMultiBlockMachine extends MultiBlockMachine {
    protected CustomMultiBlockMachine(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe, ItemStack[] machineRecipes, BlockFace trigger) {
        super(itemGroup, item, recipe, machineRecipes, trigger);
    }

    @Override
    public void onInteract(Player player, Block block) {

    }
}
