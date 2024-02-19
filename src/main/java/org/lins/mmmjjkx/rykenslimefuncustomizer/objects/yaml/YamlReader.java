package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class YamlReader<T> {
    private final List<String> lateInits;
    protected YamlConfiguration configuration;

    public YamlReader(YamlConfiguration config) {
        configuration = config;
        lateInits = new ArrayList<>();
    }

    public abstract T readEach(String section, ProjectAddon addon);

    public final List<T> readAll(ProjectAddon addon) {
        List<T> objects = new ArrayList<>();
        for (String key : configuration.getKeys(false)) {
            ConfigurationSection section = configuration.getConfigurationSection(key);
            if (section == null) return objects;

            ConfigurationSection register = section.getConfigurationSection("register");
            if (!checkForRegistration(register)) return objects;

            if (section.getBoolean("lateInit", false)) {
                putLateInit(key);
                continue;
            }

            var object = readEach(key, addon);
            if (object != null) {
                objects.add(object);
            }
        }
        return objects;
    }

    protected void putLateInit(String key) {
        lateInits.add(key);
    }

    public List<T> loadLateInits(ProjectAddon addon) {
        List<T> objects = new ArrayList<>();
        lateInits.forEach(s -> objects.add(readEach(s, addon)));

        lateInits.clear();

        return objects;
    }

    private boolean checkForRegistration(ConfigurationSection section) {
        if (section == null) return true;

        List<String> conditions = section.getStringList("conditions");
        boolean warn = section.getBoolean("warnIfFailed");
        boolean unfinished = section.getBoolean("unfinished", false);

        if (unfinished) return false;

        for (String condition : conditions) {
            String[] splits = condition.split(" ");
            String head = splits[0];
            if (head.equalsIgnoreCase("hasplugin")) {
                if (splits.length != 2) {
                    ExceptionHandler.handleError("读取"+section.getName()+"的注册条件时发现问题: hasplugin只需要一个参数");
                    continue;
                }
                boolean b = Bukkit.getPluginManager().isPluginEnabled(splits[1]);
                if (!b) {
                    if (warn) {
                        ExceptionHandler.handleError(section.getName()+"需要服务端插件"+splits[1]+"才能被注册");
                    }
                    return false;
                }
            } else if (head.equalsIgnoreCase("!hasplugin")) {
                if (splits.length != 2) {
                    ExceptionHandler.handleError("读取"+section.getName()+"的注册条件时发现问题: !hasplugin只需要一个参数");
                    continue;
                }
                boolean b = Bukkit.getPluginManager().isPluginEnabled(splits[1]);
                if (!b) {
                    if (warn) {
                        ExceptionHandler.handleError(section.getName()+"需要卸载服务端插件"+splits[1]+"才能被注册");
                    }
                    return false;
                }
            }
        }
        return true;
    }
}
