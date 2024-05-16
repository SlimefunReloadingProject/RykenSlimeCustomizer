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
import java.util.*;
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
import org.lins.mmmjjkx.rykenslimefuncustomizer.listeners.SingleItemRecipeGuide;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.RecipeMachineRecipe;

public class CustomRecipeMachine extends AContainer implements RecipeDisplayItem {
    private final ItemStack RECIPE_SPLITTER =
            new CustomItemStack(Material.GRAY_STAINED_GLASS_PANE, "&a").setCustomModel(2200002);
    private final ItemStack AIR = new ItemStack(Material.AIR);
    private final MachineProcessor<CraftingOperation> processor;
    private final List<Integer> input;
    private final List<Integer> output;
    private final List<RecipeMachineRecipe> raw_recipes;
    private final List<RecipeMachineRecipe> recipes;
    private final int energyPerCraft;
    private final int capacity;

    private final ItemStack RECIPE_INPUT = new CustomItemStack(Material.GREEN_STAINED_GLASS_PANE, "&a多物品输入", "", "&2> &a点击查看");
    private final ItemStack RECIPE_OUTPUT = new CustomItemStack(Material.GREEN_STAINED_GLASS_PANE, "&a多物品输出", "", "&2> &a点击查看");

    @Getter
    @Nullable private final CustomMenu menu;

    private volatile RecipeMachineRecipe currentRecipe;

    public CustomRecipeMachine(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            List<Integer> input,
            List<Integer> output,
            List<RecipeMachineRecipe> recipes,
            int energyPerCraft,
            int capacity,
            @Nullable CustomMenu menu,
            int speed) {
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

        if (menu == null) {
            this.createPreset(this, this.getInventoryTitle(), super::constructMenu);
        }

        if (menu != null) {
            menu.setInvb(this);
            this.processor.setProgressBar(menu.getProgressBarItem());

            createPreset(this, menu::apply);
        }

        setProcessingSpeed(speed);

        setCapacity(capacity);
        setEnergyConsumption(energyPerCraft);

        registerDefaultRecipes();

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
        return new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
    }

    @Override
    @NotNull public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();

        int i = 0;
        for (RecipeMachineRecipe recipe : raw_recipes) {
            ItemStack[] input = recipe.getInput();
            ItemStack[] output = recipe.getOutput();

            if (input.length == 1) {
                displayRecipes.add(input[0]);
            } else {
                ItemStack in = SingleItemRecipeGuide.tagItemRecipe(RECIPE_INPUT, i);
                displayRecipes.add(in);
            }

            if (output.length == 1) {
                displayRecipes.add(output[0]);
            } else {
                ItemStack out = SingleItemRecipeGuide.tagItemRecipe(RECIPE_OUTPUT, i);
                displayRecipes.add(out);
            }

            i++;
        }

        return displayRecipes;
    }

    @Override
    public int[] getInputSlots() {
        return input.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public int[] getOutputSlots() {
        return output.stream().mapToInt(i -> i).toArray();
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
        CraftingOperation currentOperation = this.processor.getOperation(b);
        int progressSlot = menu == null || menu.getProgressSlot() == -1 ? 22 : menu.getProgressSlot();
        if (inv != null) {
            if (currentOperation != null) {
                if (takeCharge(b.getLocation())) {
                    if (!currentOperation.isFinished()) {
                        this.processor.updateProgressBar(inv, progressSlot, currentOperation);
                        currentOperation.addProgress(1);
                    } else {
                        inv.replaceExistingItem(progressSlot, processor.getProgressBar());

                        if (currentRecipe != null) {
                            ItemStack[] outputs =
                                    currentRecipe.getMatchChanceResult().toArray(new ItemStack[] {});

                            if (!currentRecipe.isChooseOneIfHas()) {
                                for (ItemStack o : outputs) {
                                    if (o != null) {
                                        inv.pushItem(o.clone(), this.getOutputSlots());
                                    }
                                }
                            } else {
                                if (outputs.length > 0) {
                                    int index = new Random().nextInt(outputs.length);
                                    ItemStack is = outputs[index];
                                    if (is != null) {
                                        inv.pushItem(is.clone(), this.getOutputSlots());
                                    }
                                }
                            }
                        }

                        currentRecipe = null;
                        this.processor.endOperation(b);
                    }
                }
            } else {
                inv.reload();
                MachineRecipe next = this.findNextRecipe(inv);
                if (next != null) {
                    currentRecipe = (RecipeMachineRecipe) next;
                    currentOperation = new CraftingOperation(currentRecipe);
                    this.processor.startOperation(b, currentOperation);
                    this.processor.updateProgressBar(inv, progressSlot, currentOperation);
                }
            }
        }
    }
}
