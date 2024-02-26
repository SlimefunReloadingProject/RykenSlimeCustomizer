package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine;

import com.google.common.annotations.Beta;
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
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.lambda.EiFunction;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.machine.RecipeMachineRecipe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Beta
public class CustomRecipeMachine extends AContainer implements RecipeDisplayItem {
    private final ItemStack RECIPE_SPLITTER = new CustomItemStack(Material.WHITE_STAINED_GLASS_PANE, "&7配方分割符");
    private final ItemStack AIR = new ItemStack(Material.AIR);
    private final MachineProcessor<CraftingOperation> processor;
    private final List<Integer> input;
    private final List<Integer> output;
    private List<RecipeMachineRecipe> recipes;
    private final int energyPerCraft;
    private final int capacity;
    private final CustomMenu menu;
    private volatile RecipeMachineRecipe currentRecipe;

    public CustomRecipeMachine(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
                               List<Integer> input, List<Integer> output, List<RecipeMachineRecipe> recipes, int energyPerCraft,
                               int capacity, @Nullable CustomMenu menu, int speed, @Nullable JavaScriptEval eval) {
        super(itemGroup, item, recipeType, recipe);

        this.processor = new MachineProcessor<>(this);
        this.input = input;
        this.output = output;
        this.recipes = recipes;
        this.energyPerCraft = energyPerCraft;
        this.capacity = capacity;
        this.menu = menu;

        if (menu == null) {
            this.createPreset(this, this.getInventoryTitle(), super::constructMenu);
        }

        if (menu != null) {
            menu.setInvb(this);
            menu.reInit();
            this.processor.setProgressBar(menu.getProgress());
        }

        setProcessingSpeed(speed);

        setCapacity(capacity);
        setEnergyConsumption(energyPerCraft);

        if (eval != null) {
            eval.addThing("newRecipe", (EiFunction<Integer, ItemStack[], ItemStack[], List<Integer>, Boolean, RecipeMachineRecipe>) RecipeMachineRecipe::new);

            if (eval.hasFunction("getRecipes")) {
                Object result = eval.evalFunction("getRecipes", this.recipes);
                if (result instanceof List<?> list) {
                    this.recipes = (List<RecipeMachineRecipe>) list;
                }
            }
        }

        registerDefaultRecipes();

        register(RykenSlimefunCustomizer.INSTANCE);
    }

    @NotNull
    @Override
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

        recipes.forEach(this::registerRecipe);
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
    //Outside init
    public ItemStack getProgressBar() {
        return new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
    }

    @Override
    @NotNull
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();

        for (RecipeMachineRecipe recipe : recipes) {
            ItemStack[] input = recipe.getInput();
            ItemStack[] output = recipe.getOutput();
            int max = Math.max(input.length, output.length);

            for (int i = 0; i < max; i++) {
                try {
                    ItemStack in = input[i].clone();
                    displayRecipes.add(in);
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    displayRecipes.add(AIR);
                }

                try {
                    Integer chance = recipe.getChances().get(i);
                    if (chance != null) {
                        ItemStack out = output[i].clone();
                        ItemMeta meta = out.getItemMeta();

                        List<Component> lore = meta.lore();
                        if (lore == null) {
                            meta.lore(Collections.singletonList(CommonUtils.parseToComponent("&a有&b " + chance + "% &a的概率产出")));
                        } else {
                            lore.add(Component.newline());
                            lore.add(CommonUtils.parseToComponent("&a有&b " + chance + "% &a的概率产出"));
                        }

                        out.setItemMeta(meta);
                        displayRecipes.add(out);
                    }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    displayRecipes.add(AIR);
                }
            }

            if (recipes.size() > 1 && recipes.indexOf(recipe) != (recipes.size() - 1)) {
                displayRecipes.add(RECIPE_SPLITTER);
                displayRecipes.add(RECIPE_SPLITTER);
            }
        }

        return displayRecipes;
    }

    @Override
    public int[] getInputSlots() {
        int[] input = new int[this.input.size()];
        for (int i = 0; i < this.input.size(); i ++) {
            input[i] = this.input.get(i);
        }
        return input;
    }

    @Override
    public int[] getOutputSlots() {
        int[] output = new int[this.output.size()];
        for (int i = 0; i < this.output.size(); i ++) {
            output[i] = this.output.get(i);
        }
        return output;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @NotNull
    @Override
    public String getMachineIdentifier() {
        return getId();
    }

    @Override
    protected void constructMenu(BlockMenuPreset preset) {}

    @Override
    protected void tick(Block b) {
        BlockMenu inv = StorageCacheUtils.getMenu(b.getLocation());
        CraftingOperation currentOperation = this.processor.getOperation(b);
        if (inv != null) {
            if (currentOperation != null) {
                if (takeCharge(b.getLocation())) {
                    if (!currentOperation.isFinished()) {
                        this.processor.updateProgressBar(inv, menu.getProgressSlot(), currentOperation);
                        currentOperation.addProgress(1);
                    } else {
                        inv.replaceExistingItem(menu.getProgressSlot(), menu.getProgress());

                        if (currentRecipe != null) {
                            ItemStack[] outputs = currentRecipe.getMatchChanceResult().toArray(new ItemStack[]{});

                            if (!currentRecipe.isChooseOneIfHas()) {
                                for (ItemStack o : outputs) {
                                    if (o != null) {
                                        inv.pushItem(o.clone(), this.getOutputSlots());
                                    }
                                }
                            } else {
                                int index = new Random().nextInt(outputs.length);
                                ItemStack is = outputs[index];
                                if (is != null) {
                                    inv.pushItem(is.clone(), this.getOutputSlots());
                                }
                            }
                        }

                        currentRecipe = null;
                        this.processor.endOperation(b);
                    }
                }
            } else {
                MachineRecipe next = this.findNextRecipe(inv);
                if (next != null) {
                    currentRecipe = (RecipeMachineRecipe) next;
                    currentOperation = new CraftingOperation(next);
                    this.processor.startOperation(b, currentOperation);
                    this.processor.updateProgressBar(inv, menu.getProgressSlot(), currentOperation);
                }
            }
        }
    }
}