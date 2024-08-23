package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockCraftEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
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
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ScriptEval;

public class CustomMultiBlockMachine extends MultiBlockMachine {
    private final SoundEffect craftSound;
    private final int workIndex;
    private final ScriptEval eval;
    private final BlockFace dispenserFace;

    public CustomMultiBlockMachine(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            ItemStack[] recipe,
            Map<ItemStack[], ItemStack> craftRecipes,
            int work,
            @Nullable SoundEffect soundEffect,
            @Nullable ScriptEval eval) {
        super(itemGroup, item, recipe, BlockFace.SELF);

        this.workIndex = work - 1;
        this.craftSound = soundEffect;
        this.eval = eval;
        this.dispenserFace = dispenserFaceGet();

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
        Material material = super.getRecipe()[workIndex].getType();
        if (block.getType().equals(material)) {
            if (eval != null) {
                eval.evalFunction("onWork", p, block);
            }

            Block disBlock = block.getRelative(dispenserFace);
            BlockState bs = PaperLib.getBlockState(disBlock, false).getState();
            if (bs instanceof Dispenser dispenser) {
                Inventory inv = dispenser.getInventory();

                List<ItemStack[]> inputs = RecipeType.getRecipeInputList(this);
                ItemStack[] contents = inv.getContents();

                for (ItemStack[] input : inputs) {
                    if (isCraftable(inv, input)) {
                        ItemStack output =
                                RecipeType.getRecipeOutputList(this, input).clone();
                        MultiBlockCraftEvent event = new MultiBlockCraftEvent(p, this, input, output);

                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled() && SlimefunUtils.canPlayerUseItem(p, output, true)) {
                            Inventory fakeInv = this.createVirtualInventory(inv);
                            Inventory outputInv = this.findOutputInventory(output, disBlock, inv, fakeInv);
                            if (outputInv != null) {
                                boolean waitCallback = false;

                                for (int j = 0; j < input.length; ++j) {
                                    ItemStack item = contents[j];
                                    if (item != null && item.getType() != Material.AIR) {
                                        ItemUtils.consumeItem(item, input[j].getAmount(), true);
                                    }
                                }

                                if (!waitCallback) {
                                    craftSound.playAt(block);
                                    outputInv.addItem(output);
                                }
                            } else {
                                Slimefun.getLocalization().sendMessage(p, "machines.full-inventory", true);
                            }
                        }
                        return;
                    }
                }

                if (inv.isEmpty()) {
                    Slimefun.getLocalization().sendMessage(p, "machines.inventory-empty", true);
                } else {
                    Slimefun.getLocalization().sendMessage(p, "machines.pattern-not-found", true);
                }
            }
        }
    }

    @Nonnull
    protected Inventory createVirtualInventory(@Nonnull Inventory inv) {
        Inventory fakeInv = Bukkit.createInventory(null, 9, Component.text("Fake Inventory"));

        for (int j = 0; j < inv.getContents().length; ++j) {
            ItemStack stack = inv.getContents()[j];
            if (stack != null) {
                stack = stack.clone();
                ItemUtils.consumeItem(stack, true);
            }

            fakeInv.setItem(j, stack);
        }

        return fakeInv;
    }

    private boolean isCraftable(Inventory inv, ItemStack[] recipe) {
        for (int j = 0; j < inv.getContents().length; ++j) {
            if (!SlimefunUtils.isItemSimilar(inv.getContents()[j], recipe[j], true, true, false)) {
                if (!(SlimefunItem.getByItem(recipe[j]) instanceof SlimefunBackpack)) {
                    return false;
                }

                if (!SlimefunUtils.isItemSimilar(inv.getContents()[j], recipe[j], false, true, false)) {
                    return false;
                }
            }
        }

        return true;
    }

    private BlockFace dispenserFaceGet() {
        int center = workIndex;
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

        if (center + 1 >= 9) {
            return BlockFace.SELF;
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
