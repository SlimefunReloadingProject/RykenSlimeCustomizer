package com.oracle.truffle.js.builtins;

import com.oracle.truffle.js.nodes.function.JSBuiltin;
import com.oracle.truffle.js.runtime.JSConfig;
import com.oracle.truffle.js.runtime.JSContext;
import com.oracle.truffle.js.runtime.JSRealm;

public class JavaBuiltinsOverride extends JSBuiltinsContainer.SwitchEnum<JavaBuiltins.Java> {
    public JavaBuiltinsOverride() {
        super(JSRealm.JAVA_CLASS_NAME, JavaBuiltins.Java.class);
    }

    @Override
    protected Object createNode(JSContext context, JSBuiltin builtin, boolean construct, boolean newTarget, JavaBuiltins.Java builtinEnum) {
        switch (builtinEnum) {
            case type:
                return JavaTypeNodeGenEx.create(context, builtin, args().fixedArgs(1).createArgumentNodes(context));
            case typeName:
                return JavaBuiltinsFactory.JavaTypeNameNodeGen.create(context, builtin, args().fixedArgs(1).createArgumentNodes(context));
            case from:
                return JavaBuiltinsFactory.JavaFromNodeGen.create(context, builtin, args().fixedArgs(1).createArgumentNodes(context));
            case to:
                return JavaBuiltinsFactory.JavaToNodeGen.create(context, builtin, args().fixedArgs(2).createArgumentNodes(context));
            case isType:
                return JavaBuiltinsFactory.JavaIsTypeNodeGen.create(context, builtin, args().fixedArgs(1).createArgumentNodes(context));
            case isJavaObject:
                return JavaBuiltinsFactory.JavaIsJavaObjectNodeGen.create(context, builtin, args().fixedArgs(1).createArgumentNodes(context));
            case addToClasspath:
                return JavaBuiltinsFactory.JavaAddToClasspathNodeGen.create(context, builtin, args().fixedArgs(1).createArgumentNodes(context));
            case extend:
                if (!JSConfig.SubstrateVM) {
                    return JavaBuiltinsFactory.JavaExtendNodeGen.create(context, builtin, args().varArgs().createArgumentNodes(context));
                }
                break;
            case super_:
                if (!JSConfig.SubstrateVM) {
                    return JavaBuiltinsFactory.JavaSuperNodeGen.create(context, builtin, args().fixedArgs(1).createArgumentNodes(context));
                }
        }

        return null;
    }
}
