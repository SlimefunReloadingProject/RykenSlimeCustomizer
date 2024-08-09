package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global;

import lombok.Getter;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced.ScriptableListener;

import java.util.List;

public class ScriptableListeners {
    @Getter
    private static List<ScriptableListener> scriptableListeners;

    public static void addScriptableListener(ScriptableListener scriptableListener) {
        scriptableListeners.add(scriptableListener);
    }

    public static void removeScriptableListener(ScriptableListener scriptableListener) {
        scriptableListeners.remove(scriptableListener);
    }
}
