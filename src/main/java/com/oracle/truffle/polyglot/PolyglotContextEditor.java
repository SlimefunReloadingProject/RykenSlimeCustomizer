package com.oracle.truffle.polyglot;

import org.graalvm.polyglot.Value;

public class PolyglotContextEditor {
    public static void edit(Object context, String key, Value polyglot) {
        PolyglotLanguageContext ctxt = (PolyglotLanguageContext) context;
        ctxt.context.polyglotBindings.put(key, polyglot);
    }
}
