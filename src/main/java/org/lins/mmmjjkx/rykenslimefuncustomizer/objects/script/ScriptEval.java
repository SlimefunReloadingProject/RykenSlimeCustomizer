package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.RykenSlimefunCustomizer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.libraries.colors.CMIChatColor;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced.NBTAPIIntegration;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda.CiConsumer;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda.CiFunction;

@Getter(AccessLevel.PROTECTED)
public abstract class ScriptEval {
    private final File file;
    private String fileContext;

    public ScriptEval(File file) {
        this.file = file;

        contextInit();
    }

    public abstract String key();

    protected void contextInit() {
        try {
            fileContext = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            fileContext = "";
            e.printStackTrace();
        } catch (IOException e) {
            fileContext = "";
            e.printStackTrace();
        }
    }

    @Deprecated
    protected final void setup() {
    }

    public abstract void addThing(String name, Object value);

    public final void doInit() {
        if (fileContext == null || fileContext.isBlank()) {
            contextInit();
        }

        evalFunction("init");
    }

    @CanIgnoreReturnValue
    @Nullable public abstract Object evalFunction(String functionName, Object... args);

    public abstract void close();
}
