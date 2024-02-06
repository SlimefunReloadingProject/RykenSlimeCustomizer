package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMultiBlockMachine;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml.YamlReader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiBlockMachineReader extends YamlReader<CustomMultiBlockMachine> {
    public MultiBlockMachineReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public List<CustomMultiBlockMachine> readAll(ProjectAddon addon) {
        List<CustomMultiBlockMachine> machines = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            var machine = readEach(key, addon);
            if (machine != null) {
                machines.add(machine);
            }
        }
        return machines;
    }

    @Override
    public CustomMultiBlockMachine readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;
        ExceptionHandler.HandleResult result = ExceptionHandler.handleIdConflict(s);

        if (result == ExceptionHandler.HandleResult.FAILED) return null;

        String igId = section.getString("item_group");
        ConfigurationSection item = section.getConfigurationSection("item");
        ItemStack stack = CommonUtils.readItem(item, false, addon);

        if (stack == null) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载多方块机器"+s+": 物品为空或格式错误导致无法加载");
            return null;
        }

        Pair<ExceptionHandler.HandleResult, ItemGroup> group = ExceptionHandler.handleItemGroupGet(addon, igId);
        if (group.getFirstValue() == ExceptionHandler.HandleResult.FAILED) return null;
        ItemStack[] recipe = CommonUtils.readRecipe(section.getConfigurationSection("recipe"), addon);
        SlimefunItemStack slimefunItemStack = new SlimefunItemStack(s, stack);

        ConfigurationSection recipesSection = section.getConfigurationSection("recipes");

        int workSlot = section.getInt("work", -1);
        if (workSlot < 0) {
            ExceptionHandler.handleError("无法在附属"+addon.getAddonName()+"中加载多方块机器"+s+": 没有设置工作槽");
            return null;
        }

        Map<ItemStack[], ItemStack> recipes = readRecipes(recipesSection, addon);
        SoundEffect sound = null;
        if (section.contains("sound")) {
            String soundString = section.getString("sound");
            Pair<ExceptionHandler.HandleResult, SoundEffect> soundEffectPair =
                    ExceptionHandler.handleEnumValueOf("无法在多方块机器"+s+"获取声音类型"+soundString,
                                    "多方块机器的声音"+s+"类型为空", SoundEffect.class, soundString);
            ExceptionHandler.HandleResult result1 = soundEffectPair.getFirstValue();
            if (result1 != ExceptionHandler.HandleResult.FAILED && soundEffectPair.getSecondValue() != null) {
                sound = soundEffectPair.getSecondValue();
            }
        }
        
        return new CustomMultiBlockMachine(group.getSecondValue(), slimefunItemStack, recipe, recipes, workSlot, sound);
    }

    private Map<ItemStack[], ItemStack> readRecipes(ConfigurationSection section, ProjectAddon addon) {
        Map<ItemStack[], ItemStack> map = new HashMap<>();
        if (section == null) return map;

        for (String key : section.getKeys(false)) {
            ConfigurationSection recipe = section.getConfigurationSection(key);
            if (recipe == null) continue;
            ConfigurationSection inputs = recipe.getConfigurationSection("input");
            if (inputs == null) {
                ExceptionHandler.handleError("读取多方块机器配方"+key+"时发生错误: 没有输入物品");
                continue;
            }
            ItemStack[] input = CommonUtils.readRecipe(inputs, addon);
            if (input == null) {
                ExceptionHandler.handleError("读取多方块机器配方"+key+"时发生错误: 输出物品为空或格式错误");
                continue;
            }
            ConfigurationSection outputs = recipe.getConfigurationSection("output");
            if (outputs == null) {
                ExceptionHandler.handleError("读取多方块机器配方"+key+"时发生错误: 没有输出物品");
                continue;
            }
            ItemStack output = CommonUtils.readItem(outputs, true, addon);
            if (output == null) {
                ExceptionHandler.handleError("读取多方块机器配方"+key+"时发生错误: 输出物品为空或格式错误");
                continue;
            }
            map.put(input, output);
        }
        return map;
    }
}
