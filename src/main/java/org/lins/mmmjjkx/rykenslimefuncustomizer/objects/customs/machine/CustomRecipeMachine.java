package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.operations.CraftingOperation;
import io.github.thebusybiscuit.slimefun4.libraries.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import it.unimi.dsi.fastutil.ints.IntList;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.listeners.SingleItemRecipeGuideListener;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomCraftingOperation;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public class CustomRecipeMachine extends AContainer implements RecipeDisplayItem {
    private final MachineProcessor<CraftingOperation> processor;
    private final int[] input;
    private final int[] output;
    private final List<CustomMachineRecipe> raw_recipes;
    private final List<CustomMachineRecipe> recipes;
    private final int energyPerCraft;
    private final int capacity;
    private final boolean hideAllRecipes;

    public static final ItemStack RECIPE_INPUT =
            new CustomItemStack(Material.GREEN_STAINED_GLASS_PANE, "&a多物品输入", "", "&2> &a点击查看");
    public static final ItemStack RECIPE_OUTPUT =
            new CustomItemStack(Material.GREEN_STAINED_GLASS_PANE, "&a多物品输出", "", "&2> &a点击查看");

    @Getter
    @Nullable private final CustomMenu menu;

    public CustomRecipeMachine(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            int[] input,
            int[] output,
            List<CustomMachineRecipe> recipes,
            int energyPerCraft,
            int capacity,
            @Nullable CustomMenu menu,
            int speed,
            boolean hideAllRecipes) {
        super(itemGroup, item, recipeType, recipe);

        this.processor = new MachineProcessor<>(this);
        this.input = input;
        this.output = output;
        this.raw_recipes = recipes;
        this.recipes = new ArrayList<>(
                raw_recipes.stream().filter(r -> !r.isForDisplay()).toList());
        this.energyPerCraft = energyPerCraft;
        this.capacity = capacity;
        this.menu = menu;
        this.hideAllRecipes = hideAllRecipes;

        if (menu == null) {
            ExceptionHandler.handleWarning("未找到菜单 " + item.getItemId() + " 使用默认菜单");
            this.createPreset(this, this.getInventoryTitle(), super::constructMenu);
        }

        if (menu != null) {
            this.processor.setProgressBar(menu.getProgressBarItem());

            createPreset(this, menu::apply);
        }

        setProcessingSpeed(speed);

        setCapacity(capacity);
        setEnergyConsumption(energyPerCraft);

        register(RykenSlimefunCustomizer.INSTANCE);
    }

    @NotNull @Override
    protected BlockBreakHandler onBlockBreak() {
        return new SimpleBlockBreakHandler() {
            public void onBlockBreak(@NotNull Block b) {
                BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());
                if (inv != null) {
                    inv.dropItems(b.getLocation(), CustomRecipeMachine.this.getInputSlots());
                    inv.dropItems(b.getLocation(), CustomRecipeMachine.this.getOutputSlots());
                }

                CustomRecipeMachine.this.processor.endOperation(b);
            }
        };
    }

    @Override
    protected void registerDefaultRecipes() {
        if (recipes == null || recipes.isEmpty()) {
            return;
        }

        recipes.forEach(super::registerRecipe);
    }

    @Override
    public int getEnergyConsumption() {
        return energyPerCraft;
    }

    @Override
    public @NotNull MachineProcessor<CraftingOperation> getMachineProcessor() {
        return this.processor;
    }

    @Override
    // Outside init
    public ItemStack getProgressBar() {
        return new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    }

    @Override
    @NotNull public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();

        if (hideAllRecipes) {
            return displayRecipes;
        }

        int i = 0;
        for (CustomMachineRecipe recipe : raw_recipes) {
            if (recipe.isHide()) {
                continue;
            }

            ItemStack[] input = recipe.getInput();
            ItemStack[] output = recipe.getOutput();

            if (input.length == 1) {
                displayRecipes.add(input[0]);
            } else {
                ItemStack in = SingleItemRecipeGuideListener.tagItemRecipe(RECIPE_INPUT, i);
                displayRecipes.add(in);
            }

            if (output.length == 1) {
                int seconds = recipe.getTicks() / 2;
                ItemStack out = output[0].clone();
                String rawLore = "&e制作时间: &b" + seconds + "&es";
                if (seconds > 60) {
                    rawLore = rawLore.concat("(" + CommonUtils.formatSeconds(seconds) + "&e)");
                }
                CommonUtils.addLore(out, true, rawLore);
                displayRecipes.add(out);
            } else {
                ItemStack out = SingleItemRecipeGuideListener.tagItemRecipe(RECIPE_OUTPUT, i);
                displayRecipes.add(out);
            }

            i++;
        }

        return displayRecipes;
    }

    @Override
    public int[] getInputSlots() {
        return input;
    }

    @Override
    public int[] getOutputSlots() {
        return output;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @NotNull @Override
    public String getMachineIdentifier() {
        return getId();
    }

    @Override
    protected void constructMenu(BlockMenuPreset preset) {}

    @Override
    protected void tick(Block b) {
        BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());
        CustomCraftingOperation currentOperation = (CustomCraftingOperation) this.processor.getOperation(b);
        int progressSlot = this.menu == null || this.menu.getProgressSlot() == -1 ? 22 : this.menu.getProgressSlot();
        if (inv != null) {
            if (currentOperation != null) {
                if (takeCharge(b.getLocation())) {
                    if (!currentOperation.isFinished()) {
                        this.processor.updateProgressBar(inv, progressSlot, currentOperation);
                        currentOperation.addProgress(1);
                    } else {
                        CustomMachineRecipe currentRecipe = currentOperation.getRecipe();
                        if (currentRecipe != null) {
                            ItemStack[] outputs =
                                    currentRecipe.getMatchChanceResult().toArray(ItemStack[]::new);

                            if (!currentRecipe.isChooseOneIfHas()) {
                                for (ItemStack o : outputs) {
                                    if (o != null) {
                                        inv.pushItem(o.clone(), this.getOutputSlots());
                                    }
                                }
                            } else {
                                if (outputs.length > 0) {
                                    int index = new SecureRandom().nextInt(outputs.length);
                                    ItemStack is = outputs[index];
                                    if (is != null) {
                                        inv.pushItem(is.clone(), this.getOutputSlots());
                                    }
                                }
                            }
                        }

                        ItemStack progress;
                        if (this.menu == null) {
                            progress = ChestMenuUtils.getBackground();
                        } else {
                            progress = this.menu.getItems().getOrDefault(progressSlot, ChestMenuUtils.getBackground());
                        }
                        inv.replaceExistingItem(progressSlot, progress);

                        this.processor.endOperation(b);
                    }
                }
            } else {
                MachineRecipe next = this.findNextRecipe(inv);
                if (next != null) {
                    CustomMachineRecipe currentRecipe = (CustomMachineRecipe) next;
                    currentOperation = new CustomCraftingOperation(currentRecipe);
                    this.processor.startOperation(b, currentOperation);
                    this.processor.updateProgressBar(inv, progressSlot, currentOperation);
                }
            }
        }
    }

    protected MachineRecipe findNextRecipe(BlockMenu inv) {
        Map<Integer, ItemStack> inventory = new HashMap<>();

        for (int slot : this.getInputSlots()) {
            ItemStack item = inv.getItemInSlot(slot);
            if (item != null) {
                inventory.put(slot, ItemStackWrapper.wrap(item));
            }
        }

        Map<Integer, Integer> found = new HashMap<>();

        for (CustomMachineRecipe recipe : this.recipes) {
            for (ItemStack input : recipe.getInput()) {
                for (int slot : this.getInputSlots()) {
                    if (SlimefunUtils.isItemSimilar(inventory.get(slot), input, true)) {
                        found.put(slot, input.getAmount());
                        break;
                    }
                }
            }

            if (found.size() == recipe.getInput().length) {
                if (!InvUtils.fitAll(inv.toInventory(), recipe.getOutput(), this.getOutputSlots())) {
                    return null;
                }

                List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(found.entrySet());
                IntList ints = recipe.getNoConsume();

                for (Map.Entry<Integer, Integer> entry : entries) {
                    int i = entries.indexOf(entry);
                    if (!ints.contains(i)) {
                        inv.consumeItem(entry.getKey(), entry.getValue());
                    }
                }

                return recipe;
            }

            found.clear();
        }

        return null;
    }
}
