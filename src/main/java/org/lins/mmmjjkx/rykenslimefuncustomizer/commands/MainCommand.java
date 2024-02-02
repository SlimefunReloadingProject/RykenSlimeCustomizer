package org.lins.mmmjjkx.rykenslimefuncustomizer.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("rsc.command")) {
            if (args.length == 0) {
                sendHelp(sender);
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    sendHelp(sender);
                    return true;
                } else if (args[0].equalsIgnoreCase("reload")) {
                    RykenSlimefunCustomizer.reload();
                    sender.sendMessage(CommonUtils.parseToComponent("&a重载成功！"));
                    return true;
                } else if (args[0].equalsIgnoreCase("list")) {
                    List<ProjectAddon> addons = RykenSlimefunCustomizer.addonManager.getAllValues();
                    List<String> nameWithId = addons.stream().map(a -> a.getAddonName() + "(id: "+a.getAddonId()+")").toList();
                    Component component = CommonUtils.parseToComponent("&a已加载的附属: ");
                    for (String nwi : nameWithId) {
                        component = component.append(CommonUtils.parseToComponent("&a" + nwi));
                        if (nameWithId.indexOf(nwi) != (nameWithId.size() - 1)) {
                            component = component.append(CommonUtils.parseToComponent("&6, "));
                        }
                    }
                    sender.sendMessage(component);
                    return true;
                } else if (args[0].equalsIgnoreCase("reloadPlugin")) {
                    RykenSlimefunCustomizer.INSTANCE.reloadConfig();
                    sender.sendMessage(CommonUtils.parseToComponent("&a重载插件成功！"));
                    return true;
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("saveitem")) {
                    String prjId = args[1];
                    String itemId = args[2];
                    ProjectAddon addon = RykenSlimefunCustomizer.addonManager.get(prjId);
                    if (addon == null) {
                        sender.sendMessage(CommonUtils.parseToComponent("&4没有这个附属！"));
                        return false;
                    }
                    File save = new File(addon.getSavedItemsFolder(), itemId + ".yml");
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(save);

                    sender.sendMessage(CommonUtils.parseToComponent("&4这个指令还没写完!(abab)"));
                }
            } else {
                sender.sendMessage(CommonUtils.parseToComponent("&4找不到此子指令！"));
                return false;
            }
        } else {
            sender.sendMessage(CommonUtils.parseToComponent("&4你没有权限去做这些！"));
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("list", "reload");
        }
        return new ArrayList<>();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(CommonUtils.parseToComponent("""
                        &aRykenSlimeCustomizer帮助
                        &e/rsc (help) 显示帮助
                        &e/rsc reload 重载插件及附属
                        &e/rsc reloadPlugin 重载插件
                        &e/rsc list 显示加载成功的附属
                        
                        &4&l<未完成的指令>
                        &e/rsc disable <附属ID> 卸载某个附属
                        &e/rsc saveitem <附属ID> <ID> 保存物品
                        """));
    }
}
