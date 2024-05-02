package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.parent;

import java.io.File;
import org.lins.mmmjjkx.rykenslimefuncustomizer.objects.ProjectAddon;

public interface ScriptCreator {
    String scriptKey();

    String getFileName(String name);

    ScriptEval createScript(File file, ProjectAddon addon);
}
