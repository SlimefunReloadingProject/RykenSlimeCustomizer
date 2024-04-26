package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.slimefun;

import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ban.CommandSafe;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.List;

public class ItemGroupButton extends SubItemGroup {
    private final List<String> actions;

    public ItemGroupButton(NamespacedKey key, NestedItemGroup parent, ItemStack item, int tier, @Nullable List<String> actions) {
        super(key, parent, item, tier);

        this.actions = actions;
    }

    public void run(Player p) {
        if (actions != null) {
            for (String action : actions) {
                String content = action.split(" ")[1];
                switch (action) {
                    case "link" -> {
                        p.sendMessage(CommonUtils.parseToComponent("&e单击此处: "));
                        Component link = CommonUtils.parseToComponent("&7" + content);
                        ClickEvent event = ClickEvent.openUrl(content);
                        link = link.clickEvent(event);
                        p.sendMessage(link);
                    }
                    case "console" -> {
                        if (CommandSafe.isBadCommand(content)) {
                            ExceptionHandler.handleDanger("在"+getKey().getKey()+"物品组按钮中发现执行服务器高危操作,请联系附属对应作者进行处理！！！！！");
                            continue;
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), content.replaceAll("%player%", p.getName()));
                    }
                    default -> ExceptionHandler.handleWarning("在"+getKey().getKey()+"物品组按钮中发现未知的操作类型: " + action);
                }
            }
        }
    }
}
