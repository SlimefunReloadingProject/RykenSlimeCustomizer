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
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.listeners.SingleItemRecipeGuideListener;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomLinkedMachineOperation;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.CustomLinkedMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.BlockMenuUtil;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.StackUtils;

public class CustomLinkedRecipeMachine extends AContainer implements RecipeDisplayItem {
    private static final Map<Location, Integer> lastMatch = new HashMap<>();
    private final MachineProcessor<CraftingOperation> processor;
    private final int[] input;
    private final int[] output;
    private final List<CustomLinkedMachineRecipe> raw_recipes;
    private final List<CustomLinkedMachineRecipe> recipes;
    private final int energyPerCraft;
    private final int capacity;
    private final boolean hideAllRecipes;
    private final int saveAmount;

    public static final ItemStack RECIPE_INPUT =
            new CustomItemStack(Material.GREEN_STAINED_GLASS_PANE, "&a多物品输入", "", "&2> &a点击查看");
    public static final ItemStack RECIPE_OUTPUT =
            new CustomItemStack(Material.GREEN_STAINED_GLASS_PANE, "&a多物品输出", "", "&2> &a点击查看");

    @Getter
    @Nullable private final CustomMenu menu;

    public CustomLinkedRecipeMachine(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            int[] input,
            int[] output,
            List<CustomLinkedMachineRecipe> recipes,
            int energyPerCraft,
            int capacity,
            @Nullable CustomMenu menu,
            int speed,
            boolean hideAllRecipes,
            int saveAmount) {
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
        this.saveAmount = saveAmount;

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
                    inv.dropItems(b.getLocation(), CustomLinkedRecipeMachine.this.getInputSlots());
                    inv.dropItems(b.getLocation(), CustomLinkedRecipeMachine.this.getOutputSlots());
                }

                CustomLinkedRecipeMachine.this.processor.endOperation(b);
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
        for (CustomLinkedMachineRecipe recipe : raw_recipes) {
            if (recipe.isHide()) {
                continue;
            }

            ItemStack[] input = recipe.getInput();
            ItemStack[] output = recipe.getOutput();

            if (input.length == 1) {
                displayRecipes.add(input[0]);
            } else {
                ItemStack in = SingleItemRecipeGuideListener.tagItemLinkedRecipe(RECIPE_INPUT, i);
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
                ItemStack out = SingleItemRecipeGuideListener.tagItemLinkedRecipe(RECIPE_OUTPUT, i);
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
        CustomLinkedMachineOperation currentOperation = (CustomLinkedMachineOperation) this.processor.getOperation(b);
        int progressSlot = this.menu == null || this.menu.getProgressSlot() == -1 ? 22 : this.menu.getProgressSlot();
        if (inv != null) {
            if (currentOperation != null) {
                if (takeCharge(b.getLocation())) {
                    if (!currentOperation.isFinished()) {
                        if (inv.hasViewer()) {
                            this.processor.updateProgressBar(inv, progressSlot, currentOperation);
                        }
                        currentOperation.addProgress(1);
                    } else {
                        CustomLinkedMachineRecipe currentRecipe = currentOperation.getRecipe();
                        if (currentRecipe != null) {
                            BlockMenuUtil.pushItem(inv, currentRecipe.getLinkedOutput(), currentRecipe.isChooseOneIfHas());
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
                CustomLinkedMachineRecipe next = this.findNextLinkedRecipe(inv);
                if (next == null) {
                    return;
                }
                currentOperation = new CustomLinkedMachineOperation(next);
                this.processor.startOperation(b, currentOperation);
                if (inv.hasViewer()) {
                    this.processor.updateProgressBar(inv, progressSlot, currentOperation);
                }
            }
        }
    }

    @Nullable
    public CustomLinkedMachineRecipe findNextLinkedRecipe(BlockMenu blockMenu) {
        Location location = blockMenu.getLocation();
        Integer lastMatchIndex = lastMatch.get(blockMenu.getLocation());
        if (lastMatchIndex != null) {
            CustomLinkedMachineRecipe recipe = recipes.get(lastMatchIndex);
            if (matchRecipe(blockMenu, recipe)) {
                return recipe;
            } else {
                lastMatch.remove(location);
            }
        }

        for (int i = 0; i < recipes.size(); i++) {
            if (lastMatchIndex != null && i == lastMatchIndex) {
                continue;
            }

            CustomLinkedMachineRecipe recipe = recipes.get(i);
            if (matchRecipe(blockMenu, recipe)) {
                lastMatch.put(location, i);
                return recipe;
            }
        }
        return null;
    }

    private boolean matchRecipe(BlockMenu blockMenu, CustomLinkedMachineRecipe recipe) {
        Map<Integer, ItemStack> inputMap = recipe.getLinkedInput();
        boolean matched = true;
        if (!BlockMenuUtil.fits(blockMenu, recipe.getLinkedOutput())) {
            return false;
        }
        for (int slot : inputMap.keySet()) {
            ItemStack item = blockMenu.getItemInSlot(slot);

            if (saveAmount > 0) {
                if (item != null) {
                    ItemStack clone;
                    if (item.getMaxStackSize() == 1) {
                        clone = item.clone();
                    } else {
                        if (item.getAmount() <= saveAmount) {
                            matched = false;
                            break;
                        }
                        clone = item.clone();
                        clone.setAmount(clone.getAmount() - saveAmount);
                    }
                    if (!StackUtils.itemsMatch(clone, inputMap.get(slot), false, true)) {
                        matched = false;
                        break;
                    }
                } else {
                    if (inputMap.get(slot) != null) {
                        matched = false;
                        break;
                    }
                }
            } else {
                if (!StackUtils.itemsMatch(item, inputMap.get(slot), false, true)) {
                    matched = false;
                    break;
                }
            }
        }
        if (!matched) {
            return false;
        }

        for (int slot : inputMap.keySet()) {
            blockMenu.consumeItem(slot, inputMap.get(slot).getAmount());
        }

        return true;
    }
}
