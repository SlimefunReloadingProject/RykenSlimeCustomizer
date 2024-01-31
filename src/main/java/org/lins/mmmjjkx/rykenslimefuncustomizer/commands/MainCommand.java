package org.lins.mmmjjkx.rykenslimefuncustomizer.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("rsc.command")) {
            if (args.length == 0) {
                sender.sendMessage(CommonUtils.parseToComponent("""
                        &aRykenSlimeCustomizer帮助
                        &a/rsc (help) 显示帮助
                        &a/rsc reload 重载插件
                        &a/rsc list 显示加载成功的附属
                        """));
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(CommonUtils.parseToComponent("""
                        &aRykenSlimeCustomizer帮助
                        &a/rsc (help) 显示帮助
                        &a/rsc reload 重载插件
                        &a/rsc list 显示加载成功的附属
                        """));
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
                }
            }
            return true;
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
}
