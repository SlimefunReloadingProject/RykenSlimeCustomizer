package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global;

import org.jetbrains.annotations.Nullable;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent.ScriptCreator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScriptCreators {
    private static final Map<String, ScriptCreator> scriptCreators;

    static {
        scriptCreators = new HashMap<>();
    }

    public static void pushScriptCreator(ScriptCreator scriptCreator) {
        scriptCreators.put(scriptCreator.scriptKey().toUpperCase(), scriptCreator);
    }

    public static void clearScriptCreators() {
        scriptCreators.clear();
    }

    public static Set<String> scriptKeys() {return scriptCreators.keySet();}

    @Nullable
    public static ScriptCreator getScriptCreator(String s) {
        return scriptCreators.get(s);
    }
}
