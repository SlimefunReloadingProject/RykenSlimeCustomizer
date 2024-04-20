package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

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

            ExceptionHandler.debugLog("开始读取项: " + key);

            ConfigurationSection register = section.getConfigurationSection("register");
            if (!checkForRegistration(register)) return objects;

            ExceptionHandler.debugLog("检查延迟加载...");

            if (section.getBoolean("lateInit", false)) {
                putLateInit(key);
                continue;
            }

            ExceptionHandler.debugLog("开始读取...");

            var object = readEach(key, addon);
            if (object != null) {
                objects.add(object);
            }

            ExceptionHandler.debugLog("读取项 " + key + " 完成, 添加结果： " + (object != null));
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
        boolean warn = section.getBoolean("warn");
        boolean unfinished = section.getBoolean("unfinished", false);

        if (unfinished) return false;

        for (String condition : conditions) {
            String[] splits = condition.split(" ");
            String head = splits[0];
            if (head.equalsIgnoreCase("hasplugin")) {
                if (splits.length != 2) {
                    ExceptionHandler.handleError("读取"+section.getName()+"的注册条件时发现问题: hasplugin仅需要一个参数");
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
                    ExceptionHandler.handleError("读取"+section.getName()+"的注册条件时发现问题: !hasplugin仅需要一个参数");
                    continue;
                }
                boolean b = Bukkit.getPluginManager().isPluginEnabled(splits[1]);
                if (!b) {
                    if (warn) {
                        ExceptionHandler.handleError(section.getName()+"需要卸载服务端插件"+splits[1]+"才能被注册(可能与其冲突？)");
                    }
                    return false;
                }
            } else if (head.equalsIgnoreCase("version")) {
                if (splits.length != 3) {
                    ExceptionHandler.handleError("读取"+section.getName()+"的注册条件时发现问题: version需要两个参数");
                    continue;
                }

                int current = versionToCode(Bukkit.getMinecraftVersion());
                int destination = versionToCode(splits[2]);

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
                    case "=" -> {
                        operation = "等于";
                        match = current == destination;
                    }
                    case ">=" -> {
                        operation = "大于或等于";
                        match = current >= destination;
                    }
                    case "<=" -> {
                        operation = "小于或等于";
                        match = current <= destination;
                    }
                    case "!=" -> {
                        operation = "不等于";
                        match = current != destination;
                    }
                    default -> {
                        ExceptionHandler.handleError("读取"+section.getName()+"的注册条件时发现问题: version需要合法的比较符！");
                        continue;
                    }
                }

                if (!match) {
                    if (warn) {
                        ExceptionHandler.handleError(section.getName()+"需要版本"+operation+splits[1]+"才能被注册");
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private int versionToCode(String s) {
        String[] ver = s.split("\\.");
        String ver2 = "";
        for (String v : ver) {
            ver2 = ver2.concat(v);
        }

        if (ver.length == 2) {
            ver2 = ver2.concat("0");
        }

        return Integer.parseInt(ver2);
    }
}
