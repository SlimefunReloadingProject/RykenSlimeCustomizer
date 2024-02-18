package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.List;
import java.util.regex.Pattern;

public class ResearchReader extends YamlReader<Research> {
    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");

    public ResearchReader(YamlConfiguration config) {
        super(config);
    }

    @Override
    public Research readEach(String s, ProjectAddon addon) {
        ConfigurationSection section = configuration.getConfigurationSection(s);
        if (section == null) return null;

        if (!VALID_KEY.matcher(s).matches()) {
            ExceptionHandler.handleError("研究" + s + " 的ID无效，只能使用[a-z0-9._-]这些字符。");
            return null;
        }

        int researchId = section.getInt("id");
        String name = section.getString("name");
        int cost = section.getInt("levelCost");
        List<String> items = section.getStringList("items");

        if (researchId <= 0) {
            ExceptionHandler.handleError("无法加载研究 " + s + ": id必须大于0!");
            return null;
        }
        if (cost <= 0) {
            ExceptionHandler.handleError("无法加载研究 " + s + ": 等级花费必须大于0!");
            return null;
        }
        if (name == null) {
            ExceptionHandler.handleError("无法加载研究 " + s + ": 名称不能为空!");
            return null;
        }

        boolean hasCurrency = section.getBoolean("currencyCost");
        double currency = 0;
        if (hasCurrency) {
            currency = section.getDouble("currencyCost");
            if (currency < 0) {
                ExceptionHandler.handleWarning("加载研究时发生问题 " + s + ": 货币花费不能小于0! 已忽略.");
                hasCurrency = false;
            }
        }

        Research research;
        if (hasCurrency) {
            research = new Research(new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, s), researchId, name, cost, currency);
        } else {
            research = new Research(new NamespacedKey(RykenSlimefunCustomizer.INSTANCE, s), researchId, name, cost);
        }

        for (String item : items) {
            SlimefunItem sfItem = SlimefunItem.getById(item);
            if (sfItem == null) {
                ExceptionHandler.handleWarning("读取研究 " + s + " 的物品时发生问题: " + item + " 不是粘液科技物品! 已跳过.");
                continue;
            }
            research.addItems(sfItem);
        }
        return research;
    }
}
