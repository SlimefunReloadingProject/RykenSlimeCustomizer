package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import com.google.common.annotations.Beta;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomMenu;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomRecipeMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Beta
public class RecipeMachineReader extends YamlReader<CustomRecipeMachine> {
    public RecipeMachineReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<CustomRecipeMachine> readAll(ProjectAddon addon) {
        List<CustomRecipeMachine> machines = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var machine = readEach(key, addon);
            if (machine != null) {
                machines.add(machine);
            }
        }
        return machines;
    }

    @Override
    public CustomRecipeMachine readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载配方机器"+s+": 物品为空或格式错误");
            return null;
        }

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        String recipeType = section.getString("recipe_type", "NULL");

        Pair<ExceptionHandler.HandleResult, RecipeType> rt = ExceptionHandler.handleField(
                "错误的配方类型" + recipeType + "!", "", RecipeType.class, recipeType
        );

        if (rt.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);

        CustomMenu menu = CommonUtils.getIf(addon.getMenus(), m -> m.getID().equalsIgnoreCase(s));
        if (menu == null) {
            ExceptionHandler.handleError("无法加载配方机器"+s+": 对应菜单不存在");
            return null;
        }
        if (menu.getProgress() == null) {
            ExceptionHandler.handleError("无法加载配方机器"+s+": 对应菜单缺少进度条物品");
            return null;
        }

        List<Integer> input = section.getIntegerList("input");
        List<Integer> output = section.getIntegerList("output");

        if (input.isEmpty()) {
            ExceptionHandler.handleError("无法加载配方机器"+s+": 输入为空");
            return null;
        }

        if (output.isEmpty()) {
            ExceptionHandler.handleError("无法加载配方机器"+s+": 输出为空");
            return null;
        }

        ConfigurationSection recipes = section.getConfigurationSection("recipes");

        int capacity = section.getInt("capacity");

        if (capacity < 0) {
            ExceptionHandler.handleError("无法加载配方机器"+s+": 容量未设置或小于0");
            return null;
        }

        int energy = section.getInt("energyPerCraft");

        if (energy <= 0) {
            ExceptionHandler.handleError("无法加载配方机器"+s+": 合成一次的消耗能量未设置或小于等于0");
            return null;
        }

        int speed = section.getInt("speed");

        if (speed <= 0) {
            ExceptionHandler.handleError("无法加载配方机器"+s+": 合成速度未设置或小于等于0");
            return null;
        }

        List<MachineRecipe> mr = readRecipes(input.size(), output.size(), recipes, addon);

        return new CustomRecipeMachine(group.getSecondValue(), slimefunItemStack, rt.getSecondValue(), recipe, input, output, mr, capacity, energy, menu, speed);
    }

    private List<MachineRecipe> readRecipes(int inputSize, int outputSize, ConfigurationSection section, ProjectAddon addon) {
        List<MachineRecipe> list = new ArrayList<>();
        if (section == null) {
            return list;
        }

        for (String key: section.getKeys(false)) {
            ConfigurationSection recipes = section.getConfigurationSection(key);
            if (recipes == null) continue;
            int seconds = recipes.getInt("seconds", 0);
            if (seconds < 1) {
                ExceptionHandler.handleError("读取机器配方"+key+"时发生错误: 秒数不能小于1");
                continue;
            }
            ConfigurationSection inputs = recipes.getConfigurationSection("input");
            if (inputs == null) {
                ExceptionHandler.handleError("读取机器配方"+key+"时发生错误: 没有输入物品");
                continue;
            }
            ItemStack[] input = CommonUtils.readRecipe(inputs, addon, inputSize);
            if (input == null) {
                ExceptionHandler.handleError("读取机器配方"+key+"时发生错误: 输入物品为空或格式错误");
                continue;
            }
            ConfigurationSection outputs = recipes.getConfigurationSection("output");
            if (outputs == null) {
                ExceptionHandler.handleError("读取机器配方"+key+"时发生错误: 没有输出物品");
                continue;
            }
            ItemStack[] output = CommonUtils.readRecipe(outputs, addon, outputSize);
            if (output == null) {
                ExceptionHandler.handleError("读取机器配方"+key+"时发生错误: 输出物品为空或格式错误");
                continue;
            }

            input = removeNulls(input);
            output = removeNulls(output);

            list.add(new MachineRecipe(seconds, input, output));
        }
        return list;
    }

    private ItemStack[] removeNulls(ItemStack[] origin) {
        int count = 0;
        for (ItemStack element : origin) {
            if (element != null) {
                count++;
            }
        }
        ItemStack[] newArray = new ItemStack[count];

        int index = 0;
        for (ItemStack element : origin) {
            if (element != null) {
                newArray[index++] = element;
            }
        }
        return newArray;
    }
}
