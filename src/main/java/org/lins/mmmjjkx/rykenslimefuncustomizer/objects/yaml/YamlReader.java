package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.yaml;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.CustomAddonConfig;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public abstract class YamlReader<T> {
    public static final int MAJOR_VERSION = PaperLib.getMinecraftVersion();
    public static final int MINOR_VERSION = PaperLib.getMinecraftPatchVersion();
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
                ExceptionHandler.debugLog("&a已预加载物品: " + item.getItemId());
            }
        }
    }

    @Nullable protected final SlimefunItemStack getPreloadItem(String itemId) {
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
                ExceptionHandler.debugLog("&aSUCCESS | 读取项" + key + "成功！");
            } else {
                ExceptionHandler.debugLog("&cFAILURE | 读取项" + key + "失败！");
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
            ExceptionHandler.debugLog("开始读取延迟项：" + key);
            var object = readEach(key);
            if (object != null) {
                objects.add(object);
                ExceptionHandler.debugLog("&aSUCCESS | 读取项" + key + "成功！");
            } else {
                ExceptionHandler.debugLog("&cFAILURE | 读取项" + key + "失败！");
            }
        });

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

                int targetMajor = 0;
                int targetMinor = 0;
                String[] versionSplit = splits[2].split("\\.");
                if (versionSplit.length == 2) {
                    try {
                        targetMajor = Integer.parseInt(versionSplit[1]);
                    } catch (NumberFormatException e) {
                        ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: 版本号" + splits[2] + "不是正常的版本号！");
                        continue;
                    }
                } else if (versionSplit.length == 3) {
                    try {
                        targetMajor = Integer.parseInt(versionSplit[1]);
                        targetMinor = Integer.parseInt(versionSplit[2]);
                    } catch (NumberFormatException e) {
                        ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: 版本号" + splits[2] + "不是正常的版本号！");
                        continue;
                    }
                } else {
                    ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: 版本号" + splits[2] + "不是正常的版本号！");
                }

                // ExceptionHandler.info("key: " + key + " condition: " + condition + " major: " + targetMajor + "
                // minor: " + targetMinor);
                boolean pass = false;
                switch (splits[1]) {
                    case ">" -> {
                        if (MAJOR_VERSION > targetMajor
                                || (MAJOR_VERSION == targetMajor && MINOR_VERSION > targetMinor)) {
                            pass = true;
                        }
                    }
                    case "<" -> {
                        if (MAJOR_VERSION < targetMajor
                                || (MAJOR_VERSION == targetMajor && MINOR_VERSION < targetMinor)) {
                            pass = true;
                        }
                    }
                    case ">=" -> {
                        if (MAJOR_VERSION > targetMajor
                                || (MAJOR_VERSION == targetMajor && MINOR_VERSION >= targetMinor)) {
                            pass = true;
                        }
                    }
                    case "<=" -> {
                        if (MAJOR_VERSION < targetMajor
                                || (MAJOR_VERSION == targetMajor && MINOR_VERSION <= targetMinor)) {
                            pass = true;
                        }
                    }
                    case "==" -> {
                        if (MAJOR_VERSION == targetMajor && MINOR_VERSION == targetMinor) {
                            pass = true;
                        }
                    }
                    case "!=" -> {
                        if (MAJOR_VERSION != targetMajor || MINOR_VERSION != targetMinor) {
                            pass = true;
                        }
                    }
                    default -> {
                        ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: version需要合法的比较符！");
                        pass = true;
                    }
                }
                if (!pass) {
                    if (warn) {
                        ExceptionHandler.handleError(key + "需要服务端版本" + splits[1] + " " + splits[2] + "才能被注册");
                    }
                    return false;
                }
            } else if (head.contains("config")) {
                CustomAddonConfig config = addon.getConfig();
                if (config == null) {
                    ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: 无法获取配置");
                    continue;
                }

                switch (head) {
                    case "config.boolean" -> {
                        if (splits.length != 2) {
                            ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: config.boolean需要一个参数");
                            continue;
                        }

                        if (!config.config().getBoolean(splits[1])) {
                            if (warn) {
                                ExceptionHandler.handleError(key + "需要配置选项" + splits[1] + "为true才能被注册");
                            }
                            return false;
                        }
                    }
                    case "config.string" -> {
                        if (splits.length != 3) {
                            ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: config.string需要两个参数");
                            continue;
                        }

                        if (!Objects.equals(config.config().getString(splits[1]), splits[2])) {
                            if (warn) {
                                ExceptionHandler.handleError(key + "需要配置选项" + splits[1] + "为" + splits[2] + "才能被注册");
                            }
                            return false;
                        }
                    }
                    case "config.int" -> {
                        if (splits.length != 4) {
                            ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: config.int需要三个参数");
                            continue;
                        }

                        String configKey = splits[1];
                        int current = config.config().getInt(splits[2]);
                        int destination = Integer.parseInt(splits[3]);

                        if (!intCheck(
                                splits[1],
                                key,
                                "config.int",
                                current,
                                destination,
                                (op) -> "需要配置选项" + configKey + op + splits[3] + "才能被注册",
                                warn)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean intCheck(
            String operator,
            String key,
            String regParam,
            int current,
            int destination,
            Function<String, String> msg,
            boolean warn) {
        String operation = "";
        boolean b =
                switch (operator) {
                    case ">" -> {
                        operation = "大于";
                        yield current > destination;
                    }
                    case "<" -> {
                        operation = "小于";
                        yield current < destination;
                    }
                    case ">=" -> {
                        operation = "大于或等于";
                        yield current >= destination;
                    }
                    case "<=" -> {
                        operation = "小于或等于";
                        yield current <= destination;
                    }
                    case "==" -> {
                        operation = "等于";
                        yield current == destination;
                    }
                    case "!=" -> {
                        operation = "不等于";
                        yield current != destination;
                    }
                    default -> {
                        ExceptionHandler.handleError("读取" + key + "的注册条件时发现问题: " + regParam + "需要合法的比较符！");
                        yield true;
                    }
                };

        if (!b) {
            if (warn) {
                ExceptionHandler.handleError(key + msg.apply(operation));
            }
        }

        return b;
    }
}
