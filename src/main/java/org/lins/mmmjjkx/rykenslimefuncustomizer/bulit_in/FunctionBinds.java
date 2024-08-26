package org.lins.mmmjjkx.rykenslimefuncustomizer.bulit_in;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8Property;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueProxy;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import java.util.Random;
import me.clip.placeholderapi.PlaceholderAPI;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced.NBTAPIIntegration;

public class FunctionBinds {
    public FunctionBinds() {}

    @V8Function(name = "sendMessage")
    public void sendMessage(Player player, String message) {
        player.sendMessage(CMIChatColor.translate(parsePlaceholder(player, message)));
    }

    @V8Function(name = "runOpCommand")
    public void runOpCommand(Player player, String command) {
        boolean opBefore = player.isOp();
        if (!opBefore) {
            player.setOp(true);
        }
        player.performCommand(parsePlaceholder(player, command));
        if (!opBefore) {
            player.setOp(false);
        }
    }

    @V8Function(name = "runConsoleCommand")
    public void runConsoleCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsePlaceholder(null, command));
    }

    @V8Function(name = "isPluginLoaded")
    public boolean isPluginLoaded(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }

    @V8Function(name = "getSfItemById")
    public SlimefunItem getSfItemById(String id) {
        return SlimefunItem.getById(id);
    }

    @V8Function(name = "getSfItemByItem")
    public SlimefunItem getSfItemByItem(ItemStack item) {
        return SlimefunItem.getByItem(item);
    }

    @V8Function(name = "isItemSimilar")
    public boolean isItemSimilar(ItemStack item1, ItemStack item2, boolean checkLore, boolean checkAmount) {
        return SlimefunUtils.isItemSimilar(item1, item2, checkLore, checkAmount);
    }

    @V8Function(name = "isRadioactiveItem")
    public boolean isRadioactiveItem(ItemStack item) {
        return SlimefunUtils.isRadioactive(item);
    }

    @V8Function(name = "isSoulbound")
    public boolean isSoulbound(ItemStack item) {
        return SlimefunUtils.isSoulbound(item);
    }

    @V8Function(name = "canPlayerUseItem")
    public boolean canPlayerUseItem(Player player, ItemStack item, boolean sendMessage) {
        return SlimefunUtils.canPlayerUseItem(player, item, sendMessage);
    }

    @V8Function(name = "randintA")
    public int randintA(int a) {
        return randintB(a, false);
    }

    @V8Function(name = "randintB")
    public int randintB(int a, boolean containsA) {
        return new Random().nextInt(containsA ? a + 1 : a);
    }

    @V8Function(name = "randintC")
    public int randintC(int a, int b) {
        return randintD(a, b, false);
    }

    @V8Function(name = "randintD")
    public int randintD(int a, int b, boolean rangeClosed) {
        return new Random().nextInt(b - a + (rangeClosed ? 1 : 0)) + a;
    }

    @V8Function(name = "setData")
    public void setData(Location location, String key, String value) {
        BlockStorage.addBlockInfo(location, key, value);
    }

    @V8Function(name = "getData")
    public String getData(Location location, String key) {
        return BlockStorage.getLocationInfo(location, key);
    }

    @V8Function(name = "getBlockMenu")
    public BlockMenu getBlockMenu(Location loc) {
        return BlockStorage.getInventory(loc);
    }

    @V8Function(name = "getBlockData")
    public Config getBlockData(Location loc) {
        return BlockStorage.getLocationInfo(loc);
    }

    @V8Function(name = "isSlimefunBlock")
    public boolean isSlimefunBlock(Location loc) {
        return BlockStorage.hasBlockInfo(loc);
    }

    @V8Function(name = "isBlock")
    public boolean isBlock(Location loc, String id) {
        return BlockStorage.check(loc, id);
    }

    @V8Function(name = "getSfItemByBlock")
    public SlimefunItem getSfItemByBlock(Location loc) {
        return BlockStorage.check(loc);
    }

    @V8Function(name = "runLater")
    public void runLater(Runnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLater(RykenSlimefunCustomizer.INSTANCE, runnable, delay);
    }

    @V8Function(name = "runRepeating")
    public void runRepeating(Runnable runnable, int delay, int period) {
        Bukkit.getScheduler().runTaskTimer(RykenSlimefunCustomizer.INSTANCE, runnable, delay, period);
    }

    @V8Function(name = "runAsync")
    public void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(RykenSlimefunCustomizer.INSTANCE, runnable);
    }

    @V8Function(name = "runLaterAsync")
    public void runLaterAsync(Runnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(RykenSlimefunCustomizer.INSTANCE, runnable, delay);
    }

    @V8Function(name = "runRepeatingAsync")
    public void runRepeatingAsync(Runnable runnable, int delay, int period) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(RykenSlimefunCustomizer.INSTANCE, runnable, delay, period);
    }

    @V8Function(name = "isInstanceOf")
    public boolean isInstance(Object obj, Class<?> clazz) {
        if (obj instanceof V8ValueProxy v8p) {
            V8Runtime runtime = v8p.getV8Runtime();
            try {
                obj = runtime.getConverter().toObject(v8p);
            } catch (JavetException ignored) {
            }
        }
        return clazz.isInstance(obj);
    }

    @V8Function(name = "cast")
    public Object cast(Object obj, Class<?> clazz) {
        if (obj instanceof V8ValueProxy v8p) {
            V8Runtime runtime = v8p.getV8Runtime();
            try {
                obj = runtime.getConverter().toObject(v8p);
            } catch (JavetException ignored) {
            }
        }
        return clazz.cast(obj);
    }

    @V8Property(name = "NBTAPI")
    public NBTAPIIntegration getNBTAPIIntegration() {
        return isPluginLoaded("NBTAPI") ? NBTAPIIntegration.instance : null;
    }

    @V8Property(name = "server")
    public Server getServer() {
        return Bukkit.getServer();
    }

    private String parsePlaceholder(@Nullable Player p, String text) {
        if (p != null) {
            text = text.replaceAll("%player%", p.getName());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(p, text);
        }

        return text;
    }

    static class RunnableGetter {
        public static Runnable getRunnable(Runnable runnable) {
            return runnable;
        }
    }

    static class JavaObject {
        public static Class<?> type(String className) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    }
}
