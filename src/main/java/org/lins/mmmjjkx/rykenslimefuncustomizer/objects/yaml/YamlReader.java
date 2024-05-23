package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public abstract class YamlReader<T> {
    private final List<String> lateInits;
    protected final ProjectAddon addon;
    protected final YamlConfiguration configuration;

    public YamlReader(YamlConfiguration config, ProjectAddon addon) {
        this.configuration = config;
        this.lateInits = new ArrayList<>();
        this.addon = addon;
    }

    public abstract T readEach(String section);

    public final void preload() {
        for (String key : configuration.getKeys(false)) {
            ConfigurationSection section = configuration.getConfigurationSection(key);
            if (section == null) continue;
            ConfigurationSection register = section.getConfigurationSection("register");
            if (!checkForRegistration(key, register)) continue;

            List<SlimefunItemStack> items = preloadItems(key);

            if (items == null || items.isEmpty()) continue;

            for (SlimefunItemStack item : items) {
                addon.getPreloadItems().put(item.getItemId(), item);
            }
        }
    }

    protected final SlimefunItemStack getPreloadItem(String itemId) {
        return addon.getPreloadItems().get(itemId);
    }

    public final List<T> readAll() {
        List<T> objects = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            ConfigurationSection section = configuration.getConfigurationSection(key);
            if (section == null) continue;

            ExceptionHandler.debugLog("开始读取项: " + key);

            ConfigurationSection register = section.getConfigurationSection("register");
            if (!checkForRegistration(key, register)) continue;

            ExceptionHandler.debugLog("检查延迟加载...");

            if (section.getBoolean("lateInit", false)) {
                putLateInit(key);
                ExceptionHandler.debugLog("检查结果：延迟加载");
                continue;
            }

            ExceptionHandler.debugLog("开始读取...");

            var object = readEach(key);
            if (object != null) {
                objects.add(object);
                ExceptionHandler.debugLog("SUCCESS | 读取项" + key + "成功！");
            } else {
                ExceptionHandler.debugLog("FAILURE | 读取项" + key + "失败！");
            }
            
        }
        return objects;
    }

    protected void putLateInit(String key) {
        lateInits.add(key);
    }

    public List<T> loadLateInits() {
        List<T> objects = new ArrayList<>();
        lateInits.forEach(key -> {
                ExceptionHandler.debugLog("开始读取延迟项："+ key);
                var object = readEach(key);
                if (object != null) {
                    objects.add(object);
                    ExceptionHandler.debugLog("SUCCESS | 读取项" + key + "成功！");
                } else {
                    ExceptionHandler.debugLog("FAILURE | 读取项" + key + "失败！");
                }
            }
        );

        lateInits.clear();

        return objects;
    }

    public abstract List<SlimefunItemStack> preloadItems(String s);

    private boolean checkForRegistration(String key, ConfigurationSection section) {
        if (section == null) return true;

        List<String> conditions = section.getStringList("conditions");
        boolean warn = section.getBoolean("warn", false);
        boolean unfinished = section.getBoolean("unfinished", false);

        if (unfinished) return false;

        for (String condition : conditions) {
            String[] splits = condition.split(" ");
            String head = splits[0];
            if (head.equalsIgnoreCase("hasplugin")) {
                if (splits.length != 2) {
                    ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: hasplugin仅需要一个参数");
                    continue;
                }
                boolean b = Bukkit.getPluginManager().isPluginEnabled(splits[1]);
                if (!b) {
                    if (warn) {
                        ExceptionHandler.handleError(key + "需要服务端插件" + splits[1] + "才能被注册");
                    }
                    return false;
                }
            } else if (head.equalsIgnoreCase("!hasplugin")) {
                if (splits.length != 2) {
                    ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: !hasplugin仅需要一个参数");
                    continue;
                }
                boolean b = Bukkit.getPluginManager().isPluginEnabled(splits[1]);
                if (b) {
                    if (warn) {
                        ExceptionHandler.handleError(key + "需要卸载服务端插件" + splits[1] + "才能被注册(可能与其冲突？)");
                    }
                    return false;
                }
            } else if (head.equalsIgnoreCase("version")) {
                if (splits.length != 3) {
                    ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: version需要两个参数");
                    continue;
                }

                int current = CommonUtils.versionToCode(Bukkit.getMinecraftVersion());
                int destination = CommonUtils.versionToCode(splits[2]);

                String operation;
                boolean match;
                switch (splits[1]) {
                    case ">" -> {
                        operation = "大于";
                        match = current > destination;
                    }
                    case "<" -> {
                        operation = "小于";
                        match = current < destination;
                    }
                    case ">=" -> {
                        operation = "大于或等于";
                        match = current >= destination;
                    }
                    case "<=" -> {
                        operation = "小于或等于";
                        match = current <= destination;
                    }
                    default -> {
                        ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: version需要合法的比较符！");
                        continue;
                    }
                }

                if (!match) {
                    if (warn) {
                        ExceptionHandler.handleError(key + "需要版本" + operation + splits[2] + "才能被注册");
                    }
                    return false;
                }
            }
        }
        return true;
    }
}
