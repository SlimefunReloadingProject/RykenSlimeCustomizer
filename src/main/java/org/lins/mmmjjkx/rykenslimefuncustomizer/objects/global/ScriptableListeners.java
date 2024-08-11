package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced.ScriptableListener;

public class ScriptableListeners {
    private static final Map<String, ScriptableListener> scriptableListeners = new HashMap<>();

    public static void addScriptableListener(String prjId, ScriptableListener scriptableListener) {
        scriptableListeners.put(prjId, scriptableListener);
    }

    public static void removeScriptableListener(String prjId) {
        scriptableListeners.remove(prjId);
    }

    public static void clearScriptableListeners() {
        scriptableListeners.clear();
    }

    public static List<ScriptableListener> getScriptableListeners() {
        return new ArrayList<>(scriptableListeners.values());
    }
}
