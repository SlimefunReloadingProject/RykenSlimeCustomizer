package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent;

import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.io.File;

public interface ScriptCreator {
    String scriptKey();
    ScriptEval createScript(File file, ProjectAddon addon);
}
