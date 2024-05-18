package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockCraftEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent.ScriptEval;

public class CustomMultiBlockMachine extends MultiBlockMachine {
    private final SoundEffect craftSound;
    private final int work;
    private final ScriptEval eval;

    public CustomMultiBlockMachine(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            ItemStack[] recipe,
            Map<ItemStack[], ItemStack> craftRecipes,
            int work,
            @Nullable SoundEffect soundEffect,
            @Nullable ScriptEval eval) {
        super(itemGroup, item, recipe, BlockFace.SELF);

        this.work = work;
        this.craftSound = soundEffect;
        this.eval = eval;

        for (Map.Entry<ItemStack[], ItemStack> e : craftRecipes.entrySet()) {
            addRecipe(e.getKey(), e.getValue());
        }

        register(RykenSlimefunCustomizer.INSTANCE);
    }

    @Nonnull
    public List<ItemStack> getDisplayRecipes() {
        return new ArrayList<>();
    }

    @Override
    public void onInteract(Player p, Block block) {
        Material material = super.getRecipe()[work - 1].getType();
        if (block.getType().equals(material)) {
            if (eval != null) {
                eval.evalFunction("onWork", p, block);
            }

            BlockFace dis = dispenserFaceGet();
            Block disBlock = block.getRelative(dis);
            BlockState bs = PaperLib.getBlockState(disBlock, false).getState();
            if (bs instanceof Dispenser dispenser) {
                Inventory inv = dispenser.getInventory();
                ItemStack[] contents = inv.getContents();

                for (ItemStack current : contents) {
                    for (ItemStack convert : RecipeType.getRecipeInputs(this)) {
                        if (convert != null && SlimefunUtils.isItemSimilar(current, convert, true)) {
                            ItemStack output = RecipeType.getRecipeOutput(this, convert);
                            Inventory outputInv = this.findOutputInventory(output, disBlock, inv);
                            MultiBlockCraftEvent event = new MultiBlockCraftEvent(p, this, current, output);
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                return;
                            }

                            if (outputInv != null) {
                                ItemStack removing = current.clone();
                                removing.setAmount(1);
                                inv.removeItem(removing);
                                outputInv.addItem(event.getOutput());

                                if (craftSound != null) {
                                    craftSound.playAt(block);
                                }
                            } else {
                                Slimefun.getLocalization().sendMessage(p, "machines.full-inventory", true);
                            }
                            return;
                        }
                    }

                    Slimefun.getLocalization().sendMessage(p, "machines.unknown-material", true);
                }
            }
        }
    }

    private BlockFace dispenserFaceGet() {
        int center = work - 1;
        ItemStack[] is = getRecipe();
        if (center - 3 > 0) {
            ItemStack o1 = is[center - 3];
            if (o1 != null && o1.getType().equals(Material.DISPENSER)) {
                return BlockFace.UP;
            }
        }
        ItemStack o2 = is[center - 1];
        if (o2 != null && o2.getType().equals(Material.DISPENSER)) {
            return BlockFace.EAST;
        }
        ItemStack o3 = is[center + 1];
        if (o3 != null && o3.getType().equals(Material.DISPENSER)) {
            return BlockFace.WEST;
        }
        if ((center + 3) < 8) {
            ItemStack o4 = is[center + 3];
            if (o4 != null && o4.getType().equals(Material.DISPENSER)) {
                return BlockFace.DOWN;
            }
        }
        return BlockFace.SELF;
    }
}
