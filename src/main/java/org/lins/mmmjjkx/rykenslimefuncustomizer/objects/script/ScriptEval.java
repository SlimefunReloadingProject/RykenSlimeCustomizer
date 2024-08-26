package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

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
    protected final void setup() {}

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
