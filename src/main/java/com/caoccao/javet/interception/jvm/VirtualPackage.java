package com.caoccao.javet.interception.jvm;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

public class VirtualPackage {
    private VirtualPackage() {}

    public static V8Value getPackage(V8Runtime runtime, String name) {
        try {
            return new JavetJVMInterceptor.JavetVirtualPackage(runtime, name).toV8Value();
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }
}
