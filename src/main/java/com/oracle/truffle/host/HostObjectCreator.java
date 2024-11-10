package com.oracle.truffle.host;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

public class HostObjectCreator {
    public static Object create(Class<?> clazz, TruffleLanguage.Env env, Node any) {
        return HostObject.forStaticClass(clazz, new HostContext(HostLanguage.get(any), env));
    }
}
