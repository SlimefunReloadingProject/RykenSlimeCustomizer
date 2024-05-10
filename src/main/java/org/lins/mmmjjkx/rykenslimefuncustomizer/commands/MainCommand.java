package org.lins.mmmjjkx.rykenslimefuncustomizer.commands;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.ProjectAddonManager;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.Colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddonLoader;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;

public class MainCommand implements TabExecutor {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(sender);
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.reload")) {
                    sender.sendMessage(CMIChatColor.translate("&4你没有权限去做这些！"));
                    return false;
                }

                RykenSlimefunCustomizer.reload();
                sender.sendMessage(CMIChatColor.translate("&a重载成功！"));
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.list")) {
                    sender.sendMessage(CMIChatColor.translate("&4你没有权限去做这些！"));
                    return false;
                }

                List<ProjectAddon> addons = RykenSlimefunCustomizer.addonManager.getAllValues();
                List<String> nameWithId = addons.stream()
                        .map(a -> a.getAddonName() + "(id: " + a.getAddonId() + ")")
                        .toList();
                String component = CMIChatColor.translate("&a已加载的附属: ");
                for (String nwi : nameWithId) {
                    component = component.concat(CMIChatColor.translate("&a" + nwi));
                    if (nameWithId.indexOf(nwi) != (nameWithId.size() - 1)) {
                        component = component.concat(CMIChatColor.translate("&6, "));
                    }
                }
                sender.sendMessage(component);
                return true;
            } else if (args[0].equalsIgnoreCase("reloadPlugin")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.reloadPlugin")) {
                    sender.sendMessage(CMIChatColor.translate("&4你没有权限去做这些！"));
                    return false;
                }

                RykenSlimefunCustomizer.INSTANCE.reloadConfig();
                if (RykenSlimefunCustomizer.INSTANCE.getConfig().getBoolean("saveExample")) {
                    RykenSlimefunCustomizer.saveExample();
                }
                sender.sendMessage(CMIChatColor.translate("&a重载插件成功！"));
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("enable")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.enable")) {
                    sender.sendMessage(CMIChatColor.translate("&4你没有权限去做这些！"));
                    return false;
                }

                File file = new File(ProjectAddonManager.ADDONS_DIRECTORY, args[1]);

                if (!file.exists() || !file.isDirectory()) {
                    sender.sendMessage(CMIChatColor.translate("&4没有这个文件夹！"));
                    return false;
                }

                YamlConfiguration forId = YamlConfiguration.loadConfiguration(new File(file, "info.yml"));
                if (forId.getString("id", null) == null) {
                    sender.sendMessage(CMIChatColor.translate("&4没有在info.yml里找到ID，无法加载！"));
                    return false;
                }

                String id = forId.getString("id");
                if (RykenSlimefunCustomizer.addonManager.isLoaded(id)) {
                    sender.sendMessage(CMIChatColor.translate("&4此附属已经被加载了！"));
                    return false;
                }

                ProjectAddonLoader loader =
                        new ProjectAddonLoader(file, RykenSlimefunCustomizer.addonManager.getProjectIds());
                ProjectAddon addon = loader.load();
                RykenSlimefunCustomizer.addonManager.pushProjectAddon(addon);

                sender.sendMessage(CMIChatColor.translate("&a加载此附属成功！"));
                return true;
            } else if (args[0].equalsIgnoreCase("disable")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.disable")) {
                    sender.sendMessage(CMIChatColor.translate("&4你没有权限去做这些！"));
                    return false;
                }

                String id = args[1];
                ProjectAddon addon = RykenSlimefunCustomizer.addonManager.get(id);
                if (addon == null) {
                    sender.sendMessage(CMIChatColor.translate("&4没有这个附属！"));
                    return false;
                }

                addon.unregister();
                RykenSlimefunCustomizer.addonManager.removeProjectAddon(addon);

                sender.sendMessage(CMIChatColor.translate("&a卸载此附属成功！"));
                return true;
            } else if (args[0].equalsIgnoreCase("info")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.info")) {
                    sender.sendMessage(CMIChatColor.translate("&4你没有权限去做这些！"));
                    return false;
                }

                String id = args[1];
                ProjectAddon addon = RykenSlimefunCustomizer.addonManager.get(id);
                if (addon == null) {
                    sender.sendMessage(CMIChatColor.translate("&4没有这个附属！"));
                    return false;
                }

                String authors = addon.getAuthors().toString();
                String authorsRemoveBrackets = authors.substring(1, authors.length() - 1);

                StringBuilder builder = new StringBuilder()
                        .append("名称: &a")
                        .append(addon.getAddonName())
                        .append("\n&f")
                        .append("ID: &a")
                        .append(addon.getAddonId())
                        .append("\n&f")
                        .append("作者(们): &a")
                        .append(authorsRemoveBrackets)
                        .append("\n&f")
                        .append("版本: &a")
                        .append(addon.getAddonVersion())
                        .append("\n&f")
                        .append("依赖: &a")
                        .append(addon.getDepends())
                        .append("\n&f")
                        .append("插件依赖: &a")
                        .append(addon.getPluginDepends())
                        .append("\n&f")
                        .append("描述: &a")
                        .append(addon.getDescription());

                if (addon.getGithubRepo() != null && !addon.getGithubRepo().isBlank()) {
                    builder.append("\n&f").append("Github仓库: &e").append(addon.getGithubRepo());
                }

                sender.sendMessage(CMIChatColor.translate(builder.toString()));
                return true;
            } else if (args[0].equalsIgnoreCase("menupreview")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.menupreview")) {
                    sender.sendMessage(CMIChatColor.translate("&4你没有权限去做这些！"));
                    return false;
                }

                String menuPresetId = args[1];
                BlockMenuPreset bmp = Slimefun.getRegistry().getMenuPresets().get(menuPresetId);
                if (bmp == null) {
                    sender.sendMessage(CMIChatColor.translate("&4没有这个菜单！"));
                    return false;
                }
                if (sender instanceof Player p) {
                    bmp.open(p);
                    return true;
                } else {
                    sender.sendMessage(CMIChatColor.translate("&4你不能在控制台使用此指令！"));
                    return false;
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("saveitem")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.saveitem")) {
                    sender.sendMessage(CMIChatColor.translate("&4你没有权限去做这些！"));
                    return false;
                }

                String prjId = args[1];
                String itemId = args[2];
                ProjectAddon addon = RykenSlimefunCustomizer.addonManager.get(prjId);
                if (addon == null) {
                    sender.sendMessage(CMIChatColor.translate("&4没有这个附属！"));
                    return false;
                }
                if (sender instanceof Player p) {
                    ItemStack itemStack = p.getInventory().getItemInMainHand();
                    if (itemStack.getType() == Material.AIR) {
                        sender.sendMessage(CMIChatColor.translate("&4你不能保存空气！"));
                        return false;
                    }
                    CommonUtils.saveItem(itemStack, itemId, addon);
                    sender.sendMessage(CMIChatColor.translate("&a保存成功！"));
                    return true;
                } else {
                    sender.sendMessage(CMIChatColor.translate("&4你不能在控制台使用此指令！"));
                    return false;
                }
            }
        } else {
            sender.sendMessage(CMIChatColor.translate("&4找不到此子指令！"));
            return false;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> raw = onTabCompleteRaw(args);
        return StringUtil.copyPartialMatches(args[args.length - 1], raw, new ArrayList<>());
    }

    public @NotNull List<String> onTabCompleteRaw(@NotNull String[] args) {
        if (args.length == 1) {
            return List.of("list", "reload", "reloadPlugin", "list", "enable", "disable", "saveitem", "menupreview");
        } else if (args.length == 2) {
            return switch (args[0]) {
                case "enable" -> Arrays.stream(Objects.requireNonNull(ProjectAddonManager.ADDONS_DIRECTORY.listFiles()))
                        .map(File::getName)
                        .toList();
                case "disable", "saveitem" -> RykenSlimefunCustomizer.addonManager.getAllValues().stream()
                        .map(ProjectAddon::getAddonId)
                        .toList();
                case "menupreview" -> Slimefun.getRegistry().getMenuPresets().keySet().stream()
                        .toList();
                default -> new ArrayList<>();
            };
        }
        return new ArrayList<>();
    }

    private void sendHelp(CommandSender sender) {
        if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.help")) {
            sender.sendMessage(CMIChatColor.translate("&4你没有权限去做这些！"));
            return;
        }
        sender.sendMessage(
                CMIChatColor.translate(
                        """
                        &aRykenSlimeCustomizer帮助
                        &e/rsc (help) 显示帮助
                        &e/rsc reload 重载插件及附属
                        &e/rsc reloadPlugin 重载插件
                        &e/rsc list 显示加载成功的附属
                        &e/rsc enable <addons里的文件夹名称> 加载某个附属
                        &e/rsc disable <附属ID> 卸载某个附属
                        &e/rsc saveitem <附属ID> <ID> 保存物品
                        &e/rsc menupreview <ID> 预览机器菜单"""));
    }
}
