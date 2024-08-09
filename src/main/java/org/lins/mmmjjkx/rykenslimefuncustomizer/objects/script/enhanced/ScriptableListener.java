package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.enhanced;

import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent.ScriptEval;

@AllArgsConstructor
public class ScriptableListener {
    private final @NotNull ScriptEval script;

    public void doEventEval(Event event) {
        String className = event.getClass().getSimpleName();
        String methodName = "on" + className.replace("Event", "");
        script.evalFunction(className, event);
    }
}
