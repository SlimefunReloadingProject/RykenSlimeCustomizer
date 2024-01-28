package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js;

import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class JavaScriptEval {
    private final ScriptEngine jsEngine;
    private boolean failed = false;


    public JavaScriptEval(File js) {
        ScriptEngineManager sem = new ScriptEngineManager();
        jsEngine = sem.getEngineByName("javascript");
        String context;
        try {
            context = Files.readString(js.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            context = "";
            failed = true;
        }
        try {
            jsEngine.eval(context);
        } catch (ScriptException e) {
            failed = true;
        }
    }

    public void evalFunction(String funName, Object... args) {
        if (!failed) {
            if (jsEngine instanceof Invocable in) {
                try {
                    in.invokeFunction("tick", args);
                } catch (ScriptException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    ExceptionHandler.handleError("方法" + funName + "不存在！");
                    e.printStackTrace();
                }
            }
        }
    }
}
