package com.oracle.truffle.api;

import com.oracle.truffle.polyglot.PolyglotContextEditor;
import org.graalvm.polyglot.Value;

public class LanguageEditor {
    private LanguageEditor() {
    }

    public static void edit(TruffleLanguage.Env env, String key, Value value) {
        PolyglotContextEditor.edit(env.polyglotLanguageContext, key, value);
    }
}
