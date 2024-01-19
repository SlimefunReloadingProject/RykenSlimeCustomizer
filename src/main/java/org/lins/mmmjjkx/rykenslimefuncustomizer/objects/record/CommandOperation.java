package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.record;

import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.util.List;

public record CommandOperation(String name, List<String> commands) {
    public void run(Player p) {
        for (String command : commands) {
            String[] split = command.split("::");
            String cmdHead = split[0];
            String cmd = parse(split.length == 2 ? split[1] : split[0], p);
            switch (cmdHead) {
                default:
                case "cmd":
                    p.performCommand(cmd);
                    break;
                case "console":
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    break;
                case "potion":
                    String content = command.substring(command.indexOf("::") + 2);
                    if (split.length == 3) {
                        Pair<ExceptionHandler.HandleResult, PotionEffectType> result = ExceptionHandler.handleValueOf(
                                "在指令执行器"+ name + "中有无效的药水效果类型 " + cmd, "",
                                PotionEffectType.class, cmd, "getByName"
                        );
                        if (result.getFirstValue() != ExceptionHandler.HandleResult.SUCCESS || result.getSecondValue() == null) continue;

                        int duration = Integer.parseInt(split[1]);
                        int lvl = Integer.parseInt(split[2]);
                        PotionEffect effect = new PotionEffect(result.getSecondValue(), duration, lvl);
                        p.addPotionEffect(effect);
                        break;
                    }
                    ExceptionHandler.handleError("在指令执行器"+ name +"中有错误的药水效果配置"+content+"，请检查是否有四个参数");
                    break;
                case "jump":
                    Location location = p.getLocation().add(0, 1.25, 0);
                    p.teleport(location);
                    break;
                case "msg":
                    p.sendMessage(CommonUtils.parseToComponent(cmd));
                    break;
            }
        }
    }

    private String parse(String s, Player p) {
        s = s.replaceAll("%player%", p.getName())
                .replaceAll("%uuid%", p.getUniqueId().toString());

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            s = PlaceholderAPI.setPlaceholders(p, s);
        }
        return s;
    }
}
