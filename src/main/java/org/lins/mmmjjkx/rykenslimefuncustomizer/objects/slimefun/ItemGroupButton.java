package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun;

import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.Colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ban.CommandSafe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

@SuppressWarnings("deprecation")
public class ItemGroupButton extends SubItemGroup {
    private final List<String> actions;

    public ItemGroupButton(
            NamespacedKey key, NestedItemGroup parent, ItemStack item, int tier, @Nullable List<String> actions) {
        super(key, parent, item, tier);

        this.actions = actions;
    }

    public void run(Player p) {
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
                                    "在" + getKey().getKey() + "物品组按钮中发现执行服务器高危操作,请联系附属对应作者进行处理！！！！！");
                            continue;
                        }
                        content = action.replace(type + " ", "");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), content.replaceAll("%player%", p.getName()));
                    }
                    default -> ExceptionHandler.handleWarning("在" + getKey().getKey() + "物品组按钮中发现未知的操作类型: " + action);
                }
            }
        }
    }
}
