package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;

import java.io.File;
import java.util.List;
import java.util.Optional;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in.JavaScriptEval;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ban.CommandSafe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

@SuppressWarnings("deprecation")
public class ItemGroupButton extends SubItemGroup {
    private final List<String> actions;
    private final ProjectAddon addon;

    public ItemGroupButton(
            ProjectAddon addon, NamespacedKey key, NestedItemGroup parent, ItemStack item, int tier, @Nullable List<String> actions) {
        super(key, parent, item, tier);

        this.addon = addon;
        this.actions = actions;
    }

    public void run(Player p, int slot, ItemStack clickedItem, ClickAction clickAction, SlimefunGuideMode mode) {
        if (actions != null) {
            for (String action : actions) {
                if (action.split(" ").length < 2) {
                    ExceptionHandler.handleWarning("在" + getKey().getKey() + "物品组按钮中发现未知的操作格式: " + action);
                    continue;
                }

                String type = action.split(" ")[0];
                String content = action.split(" ")[1];
                switch (type) {
                    case "link" -> {
                        p.sendMessage(CMIChatColor.translate("&e单击此处: "));
                        TextComponent link = new TextComponent(content);
                        link.setColor(ChatColor.GRAY);

                        ClickEvent spigotClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, content);
                        link.setClickEvent(spigotClickEvent);

                        p.sendMessage(link);
                    }
                    case "console" -> {
                        if (CommandSafe.isBadCommand(content)) {
                            ExceptionHandler.handleDanger(
                                    "在" + getKey().getKey() + "物品组按钮中发现执行服务器高危操作,请联系附属对应作者进行处理！！！");
                            continue;
                        }
                        content = action.replace(type + " ", "");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), content.replaceAll("%player%", p.getName()));
                    }
                    case "open_itemgroup" -> {
                        if (content.split(":").length < 2) {
                            ExceptionHandler.handleWarning("在" + getKey().getKey() + "物品组按钮中发现未知的物品组 NamespacedKey: " + content);
                            continue;
                        }
                        String namespace = content.split(":")[0];
                        String key = content.split(":")[1];
                        int page = 1;
                        if (content.split(":").length > 2) {
                            try {
                                page = Integer.parseInt(content.split(":")[2]);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        Optional<PlayerProfile> Oprofile = PlayerProfile.find(p);
                        if (Oprofile.isEmpty()) {
                            ExceptionHandler.handleWarning("在" + getKey().getKey() + "物品组按钮中发现无法获取 PlayerProfile: " + p.getName());
                            continue;
                        }
                        PlayerProfile profile = Oprofile.get();
                        for (ItemGroup group : Slimefun.getRegistry().getAllItemGroups()) {
                            if (group.getKey().getNamespace().equals(namespace) && group.getKey().getKey().equals(key)) {
                                SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(mode);
                                implementation.openItemGroup(profile, group, page);
                            }
                        }
                    }
                    case "display_slimefunitem" -> {
                        Optional<PlayerProfile> Oprofile = PlayerProfile.find(p);
                        if (Oprofile.isEmpty()) {
                            ExceptionHandler.handleWarning("在" + getKey().getKey() + "物品组按钮中发现无法获取 PlayerProfile: " + p.getName());
                            continue;
                        }
                        SlimefunItem item = SlimefunItem.getById(content);
                        if (item == null) {
                            ExceptionHandler.handleWarning("在" + getKey().getKey() + "物品组按钮中发现未知的 SlimefunItem ID: " + content);
                            continue;
                        }
                        PlayerProfile profile = Oprofile.get();
                        SlimefunGuideImplementation implementation = Slimefun.getRegistry().getSlimefunGuide(mode);
                        implementation.displayItem(profile, item, true);
                    }
                    case "script" -> {
                        JavaScriptEval eval = null;
                        File file = new File(addon.getScriptsFolder(), content + ".js");
                        if (!file.exists()) {
                            ExceptionHandler.handleWarning(
                                    "在" + getKey().getKey() + "物品组按钮中发现执行脚本时遇到了问题: " + "找不到脚本文件 " + file.getName());
                        } else {
                            eval = new JavaScriptEval(file, addon);
                        }

                        if (eval != null) {
                            eval.evalFunction("onButtonGroupClick", p, slot, clickedItem, clickAction, mode);
                        }
                    }
                    default -> ExceptionHandler.handleWarning("在" + getKey().getKey() + "物品组按钮中发现未知的操作类型: " + action);
                }
            }
        }
    }
}
