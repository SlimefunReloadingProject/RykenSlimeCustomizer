package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent;

import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

import java.io.File;

public interface ScriptCreator {
    String scriptKey();

    String getFileName(String name);

    ScriptEval createScript(File file, ProjectAddon addon);
}
