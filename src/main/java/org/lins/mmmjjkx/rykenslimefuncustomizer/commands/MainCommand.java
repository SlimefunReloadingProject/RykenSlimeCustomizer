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
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
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
                    sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
                    return false;
                }

                RykenSlimefunCustomizer.reload();
                sender.sendMessage(CMIChatColor.translate("&aReloaded successfully！"));
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.list")) {
                    sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
                    return false;
                }

                List<ProjectAddon> addons = RykenSlimefunCustomizer.addonManager.getAllValues();
                List<String> nameWithId = addons.stream()
                        .map(a -> a.getAddonName() + "(id: " + a.getAddonId() + ")")
                        .toList();
                String component = CMIChatColor.translate("&aLoaded addons: ");
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
                    sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
                    return false;
                }

                RykenSlimefunCustomizer.INSTANCE.reloadConfig();
                if (RykenSlimefunCustomizer.INSTANCE.getConfig().getBoolean("saveExample")) {
                    RykenSlimefunCustomizer.saveExample();
                }
                sender.sendMessage(CMIChatColor.translate("&aReloaded the plugin successfully！"));
                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("enable")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.enable")) {
                    sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
                    return false;
                }

                File file = new File(ProjectAddonManager.ADDONS_DIRECTORY, args[1]);

                if (!file.exists() || !file.isDirectory()) {
                    sender.sendMessage(CMIChatColor.translate("&4The folder doesn't exist or is not a directory!"));
                    return false;
                }

                YamlConfiguration forId = YamlConfiguration.loadConfiguration(new File(file, "info.yml"));
                if (forId.getString("id", null) == null) {
                    sender.sendMessage(CMIChatColor.translate(
                            "&4Couldn't find the id in the info.yml! Check if the addon is valid!"));
                    return false;
                }

                String id = forId.getString("id");
                if (RykenSlimefunCustomizer.addonManager.isLoaded(id)) {
                    sender.sendMessage(CMIChatColor.translate("&4This addon is already loaded!"));
                    return false;
                }

                ProjectAddonLoader loader =
                        new ProjectAddonLoader(file, RykenSlimefunCustomizer.addonManager.getProjectIds());
                ProjectAddon addon = loader.load();
                RykenSlimefunCustomizer.addonManager.pushProjectAddon(addon);

                sender.sendMessage(CMIChatColor.translate("&aLoaded the addon successfully！"));
                return true;
            } else if (args[0].equalsIgnoreCase("disable")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.disable")) {
                    sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
                    return false;
                }

                String id = args[1];
                ProjectAddon addon = RykenSlimefunCustomizer.addonManager.get(id);
                if (addon == null) {
                    sender.sendMessage(CMIChatColor.translate("&4The addon is not exists!"));
                    return false;
                }

                addon.unregister();
                RykenSlimefunCustomizer.addonManager.removeProjectAddon(addon);

                sender.sendMessage(CMIChatColor.translate("&aDisabled the addon successfully！"));
                return true;
            } else if (args[0].equalsIgnoreCase("info")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.info")) {
                    sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
                    return false;
                }

                String id = args[1];
                ProjectAddon addon = RykenSlimefunCustomizer.addonManager.get(id);
                if (addon == null) {
                    sender.sendMessage(CMIChatColor.translate("&4The addon is not exists!"));
                    return false;
                }

                String authors = addon.getAuthors().toString();
                String authorsRemoveBrackets = authors.substring(1, authors.length() - 1);

                StringBuilder builder = new StringBuilder()
                        .append("Name: &a")
                        .append(addon.getAddonName())
                        .append("\n&f")
                        .append("ID: &a")
                        .append(addon.getAddonId())
                        .append("\n&f")
                        .append("Author(s): &a")
                        .append(authorsRemoveBrackets)
                        .append("\n&f")
                        .append("Version: &a")
                        .append(addon.getAddonVersion())
                        .append("\n&f")
                        .append("Depends: &a")
                        .append(addon.getDepends())
                        .append("\n&f")
                        .append("Plugin Depends: &a")
                        .append(addon.getPluginDepends())
                        .append("\n&f")
                        .append("Description: &a")
                        .append(addon.getDescription());

                if (addon.getGithubRepo() != null && !addon.getGithubRepo().isBlank()) {
                    builder.append("\n&f").append("Github Repo: &e").append(addon.getGithubRepo());
                }

                sender.sendMessage(CMIChatColor.translate(builder.toString()));
                return true;
            } else if (args[0].equalsIgnoreCase("menupreview")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.menupreview")) {
                    sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
                    return false;
                }

                String menuPresetId = args[1];
                BlockMenuPreset bmp = Slimefun.getRegistry().getMenuPresets().get(menuPresetId);
                if (bmp == null) {
                    sender.sendMessage(CMIChatColor.translate("&4The menu is not exists!"));
                    return false;
                }
                if (sender instanceof Player p) {
                    bmp.open(p);
                    return true;
                } else {
                    sender.sendMessage(CMIChatColor.translate("&4You cannot use this command in console!"));
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.reload")) {
                    sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
                    return false;
                }

                String prjId = args[1];
                ProjectAddon addon = RykenSlimefunCustomizer.addonManager.get(prjId);
                if (addon == null) {
                    sender.sendMessage(CMIChatColor.translate("&4The addon is not exists!"));
                    return false;
                }

                addon.unregister();
                RykenSlimefunCustomizer.addonManager.removeProjectAddon(addon);

                File folder = addon.getFolder();
                ProjectAddonLoader pal =
                        new ProjectAddonLoader(folder, RykenSlimefunCustomizer.addonManager.getProjectIds());
                ProjectAddon addonNew = pal.load();

                RykenSlimefunCustomizer.addonManager.pushProjectAddon(addonNew);

                sender.sendMessage(CMIChatColor.translate("&aReloaded the addon successfully！"));
                return true;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("saveitem")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.saveitem")) {
                    sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
                    return false;
                }

                String prjId = args[1];
                String itemId = args[2];
                ProjectAddon addon = RykenSlimefunCustomizer.addonManager.get(prjId);
                if (addon == null) {
                    sender.sendMessage(CMIChatColor.translate("&4The addon is not exists!"));
                    return false;
                }
                if (sender instanceof Player p) {
                    ItemStack itemStack = p.getInventory().getItemInMainHand();
                    if (itemStack.getType() == Material.AIR) {
                        sender.sendMessage(CMIChatColor.translate("&4You cannot save an air item!"));
                        return false;
                    }
                    CommonUtils.saveItem(itemStack, itemId, addon);
                    sender.sendMessage(CMIChatColor.translate("&aItem saved successfully！"));
                    return true;
                } else {
                    sender.sendMessage(CMIChatColor.translate("&4You cannot use this command in console!"));
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("getsaveitem")) {
                if (!sender.hasPermission("rsc.command") || !sender.hasPermission("rsc.command.getsaveitem")) {
                    sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
                    return false;
                }

                String prjId = args[1];
                String itemId = args[2];
                ProjectAddon addon = RykenSlimefunCustomizer.addonManager.get(prjId);
                if (addon == null) {
                    sender.sendMessage(CMIChatColor.translate("&4The addon is not exists!"));
                    return false;
                }

                File file = new File(
                        RykenSlimefunCustomizer.addonManager.getAddonFolder(prjId), "items/" + itemId + ".yml");
                if (!file.exists() || file.length() == 0) {
                    sender.sendMessage(CMIChatColor.translate("&4The item file doesn't exist or is empty!"));
                    return false;
                }

                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                ItemStack item = config.getItemStack("item");
                if (item == null) {
                    sender.sendMessage(CMIChatColor.translate(
                            "&4Couldn't find the item in the file! Check if the file is valid!"));
                    return false;
                }

                if (sender instanceof Player p) {
                    ItemStack itemStack = p.getInventory().getItemInMainHand();
                    if (itemStack.getType() == Material.AIR) {
                        p.getInventory().setItemInMainHand(item);
                        sender.sendMessage(CMIChatColor.translate("&aThe item has been added to your hand!"));
                        return true;
                    }
                    p.getInventory().addItem(item);
                    sender.sendMessage(CMIChatColor.translate("&aThe item has been added to your inventory!"));
                    return true;
                } else {
                    sender.sendMessage(CMIChatColor.translate("&4You cannot use this command in console!"));
                    return false;
                }
            }
        } else {
            sender.sendMessage(CMIChatColor.translate("&4Subcommand not found!"));
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
                case "disable", "saveitem", "getsaveitem" -> RykenSlimefunCustomizer.addonManager
                        .getAllValues()
                        .stream()
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
            sender.sendMessage(CMIChatColor.translate("&4You don't have permission to do that!"));
            return;
        }
        sender.sendMessage(
                CMIChatColor.translate(
                        """
                        &aRykenSlimeCustomizer Help:
                        &e/rsc (help) show this help message
                        &e/rsc reload [addonID] reload the plugin and specified addon or all addons
                        &e/rsc reloadPlugin reload the plugin config
                        &e/rsc list show loaded addons
                        &e/rsc enable <the folder name in addons folder> load an addon
                        &e/rsc disable <Addon ID> disable an addon
                        &e/rsc saveitem <Addon ID> <ID> save item
                        &e/rsc menupreview <ID> preview a menu preset
                        &e/rsc getsaveitem <Addon ID> <ID> get saved item"""));
    }
}
