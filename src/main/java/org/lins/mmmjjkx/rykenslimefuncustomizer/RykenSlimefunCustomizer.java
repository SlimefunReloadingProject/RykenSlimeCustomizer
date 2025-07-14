package org.lins.mmmjjkx.rykenslimefuncustomizer;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import net.guizhanss.guizhanlib.updater.GuizhanBuildsUpdater;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.commands.MainCommand;
import org.lins.mmmjjkx.rykenslimefuncustomizer.listeners.BlockListener;
import org.lins.mmmjjkx.rykenslimefuncustomizer.listeners.SingleItemRecipeGuideListener;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.generations.BlockPopulator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.CommonUtils;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

public final class RykenSlimefunCustomizer extends JavaPlugin implements SlimefunAddon {
    private static boolean runtime = false;

    public static RykenSlimefunCustomizer INSTANCE;
    public static ProjectAddonManager addonManager;

    @Override
    public void onLoad() {
        setupLibraries();
        INSTANCE = this;
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");

        File cache = new File(getDataFolder(), "cache");
        System.setProperty("XDG_CACHE_HOME", cache.getAbsolutePath());
        System.setProperty("TRUFFLE_CACHE_DIR", cache.getAbsolutePath());
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        CommonUtils.completeFile("config.yml");

        addonManager = new ProjectAddonManager();

        if (getConfig().getBoolean("saveExample", false)) {
            saveExample();
        }

        Objects.requireNonNull(getCommand("rykenslimecustomizer")).setExecutor(new MainCommand());

        addonManager.setup(this);

        new BlockListener();
        new SingleItemRecipeGuideListener();

        for (World world : Bukkit.getWorlds()) {
            world.getPopulators().add(new BlockPopulator());
        }

        ExceptionHandler.info("RykenSlimeCustomizer加载成功！");

        if (getConfig().getBoolean("pluginUpdate", false)
                && getDescription().getVersion().startsWith("b")
                && getServer().getPluginManager().isPluginEnabled("GuizhanLibPlugin")) {
            GuizhanBuildsUpdater.start(this, getFile(), "SlimefunReloadingProject", "RykenSlimeCustomizer", "main");
        }

        getServer().getScheduler().runTaskLater(this, () -> runtime = true, 1);
    }

    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) {
            world.getPopulators().removeIf(x -> x instanceof BlockPopulator);
        }

        // Plugin shutdown logic
        getLogger().info("RykenSlimeCustomizer已卸载!");
    }

    public static void reload() {
        INSTANCE.reloadConfig();
        addonManager.reload(INSTANCE);

        if (INSTANCE.getConfig().getBoolean("saveExample")) {
            saveExample();
        }
    }

    @NotNull @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/SlimefunReloadingProject/RykenSlimeCustomizer/issues";
    }

    private void setupLibraries() {
        String graalVersion = "24.1.0";
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(this);

        for (String repo : getConfig().getStringList("repositories")) {
            libraryManager.addRepository(repo);
        }

        libraryManager.addMavenCentral();

        Library byteBuddy = Library.builder()
                .groupId("net{}bytebuddy")
                .artifactId("byte-buddy")
                .version("1.17.6")
                .build();
        Library graalJS = Library.builder()
                .groupId("org{}graalvm{}js")
                .artifactId("js-language")
                .version(graalVersion)
                .build();
        Library shadowedIcu4j = Library.builder()
                .groupId("org{}graalvm{}shadowed")
                .artifactId("icu4j")
                .version(graalVersion)
                .build();
        Library graalJSEngine = Library.builder()
                .groupId("org{}graalvm{}js")
                .artifactId("js-scriptengine")
                .version(graalVersion)
                .build();
        Library truffleAPI = Library.builder()
                .groupId("org{}graalvm{}truffle")
                .artifactId("truffle-api")
                .version(graalVersion)
                .build();
        Library truffleCompiler = Library.builder()
                .groupId("org{}graalvm{}truffle")
                .artifactId("truffle-compiler")
                .version(graalVersion)
                .build();
        Library truffleEnterprise = Library.builder()
                .groupId("org{}graalvm{}truffle")
                .artifactId("truffle-enterprise")
                .version(graalVersion)
                .build();
        Library truffleRuntime = Library.builder()
                .groupId("org{}graalvm{}truffle")
                .artifactId("truffle-runtime")
                .version(graalVersion)
                .build();
        Library polyglot = Library.builder()
                .groupId("org.graalvm.polyglot")
                .artifactId("polyglot")
                .version(graalVersion)
                .build();
        Library graalSdkCollections = Library.builder()
                .groupId("org{}graalvm{}sdk")
                .artifactId("collections")
                .version(graalVersion)
                .build();
        Library graalSdkNativeImage = Library.builder()
                .groupId("org{}graalvm{}sdk")
                .artifactId("nativeimage")
                .version(graalVersion)
                .build();
        Library graalSdkWord = Library.builder()
                .groupId("org{}graalvm{}sdk")
                .artifactId("word")
                .version(graalVersion)
                .build();
        Library graalSdkNativeBridge = Library.builder()
                .groupId("org{}graalvm{}sdk")
                .artifactId("nativebridge")
                .version(graalVersion)
                .build();
        Library graalJniUtils = Library.builder()
                .groupId("org{}graalvm{}sdk")
                .artifactId("jniutils")
                .version(graalVersion)
                .build();
        Library graalRegex = Library.builder()
                .groupId("org{}graalvm{}regex")
                .artifactId("regex")
                .version(graalVersion)
                .build();

        libraryManager.loadLibrary(byteBuddy);
        libraryManager.loadLibrary(graalJS);
        libraryManager.loadLibrary(graalJSEngine);
        libraryManager.loadLibrary(truffleAPI);
        libraryManager.loadLibrary(polyglot);
        libraryManager.loadLibrary(graalSdkCollections);
        libraryManager.loadLibrary(graalSdkNativeImage);
        libraryManager.loadLibrary(graalSdkWord);
        libraryManager.loadLibrary(shadowedIcu4j);
        libraryManager.loadLibrary(graalSdkNativeBridge);
        libraryManager.loadLibrary(graalJniUtils);
        libraryManager.loadLibrary(graalRegex);
        libraryManager.loadLibrary(truffleCompiler);
        libraryManager.loadLibrary(truffleEnterprise);
        libraryManager.loadLibrary(truffleRuntime);
    }

    public static boolean allowUpdate(String prjId) {
        if (runtime) return false;

        return INSTANCE.getConfig().getBoolean("update.auto")
                && !INSTANCE.getConfig().getStringList("update.blocks").contains(prjId);
    }

    public static void saveExample() {
        String head = "addons/example/info.yml";

        String filePath = new File(INSTANCE.getDataFolder(), head).getAbsolutePath();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            INSTANCE.saveResource(head, true);
        }
    }
}
