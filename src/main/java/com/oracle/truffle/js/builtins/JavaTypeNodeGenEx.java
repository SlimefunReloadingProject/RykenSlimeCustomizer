package com.oracle.truffle.js.builtins;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.Introspection;
import com.oracle.truffle.api.dsl.NeverDefault;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeCost;
import com.oracle.truffle.api.strings.TruffleString;
import com.oracle.truffle.host.HostObjectCreator;
import com.oracle.truffle.js.nodes.JSGuards;
import com.oracle.truffle.js.nodes.JavaScriptNode;
import com.oracle.truffle.js.nodes.function.JSBuiltin;
import com.oracle.truffle.js.runtime.Errors;
import com.oracle.truffle.js.runtime.JSContext;
import com.oracle.truffle.js.runtime.JSRealm;
import com.oracle.truffle.js.runtime.Strings;

class JavaTypeNodeGenEx extends JavaBuiltins.JavaTypeNode {
    @Child
    private JavaScriptNode arguments0_;
    @CompilerDirectives.CompilationFinal
    private int state_0_;

    private JavaTypeNodeGenEx(JSContext context, JSBuiltin builtin, JavaScriptNode[] arguments) {
        super(context, builtin);
        this.arguments0_ = arguments != null && 0 < arguments.length ? arguments[0] : null;
    }

    public JavaScriptNode[] getArguments() {
        return new JavaScriptNode[]{this.arguments0_};
    }

    public Object execute(VirtualFrame frameValue) {
        int state_0 = this.state_0_;
        Object arguments0Value_ = this.arguments0_.execute(frameValue);
        if (state_0 != 0) {
            if ((state_0 & 1) != 0 && arguments0Value_ instanceof TruffleString arguments0Value__) {
                return this.type(arguments0Value__);
            }

            if ((state_0 & 2) != 0 && !JSGuards.isString(arguments0Value_)) {
                return this.typeNoString(arguments0Value_);
            }
        }

        CompilerDirectives.transferToInterpreterAndInvalidate();
        return this.executeAndSpecialize(arguments0Value_);
    }

    public void executeVoid(VirtualFrame frameValue) {
        this.execute(frameValue);
    }

    private Object executeAndSpecialize(Object arguments0Value) {
        int state_0 = this.state_0_;
        if (arguments0Value instanceof TruffleString arguments0Value_) {
            state_0 |= 1;
            this.state_0_ = state_0;
            return this.type(arguments0Value_);
        } else if (!JSGuards.isString(arguments0Value)) {
            state_0 |= 2;
            this.state_0_ = state_0;
            return this.typeNoString(arguments0Value);
        } else {
            throw new UnsupportedSpecializationException(this, new Node[]{this.arguments0_}, arguments0Value);
        }
    }

    public NodeCost getCost() {
        int state_0 = this.state_0_;
        if (state_0 == 0) {
            return NodeCost.UNINITIALIZED;
        } else {
            return (state_0 & state_0 - 1) == 0 ? NodeCost.MONOMORPHIC : NodeCost.POLYMORPHIC;
        }
    }

    public Introspection getIntrospectionData() {
        Object[] data = new Object[]{0, null, null};
        int state_0 = this.state_0_;
        Object[] s = new Object[]{"type", null, null};
        if ((state_0 & 1) != 0) {
            s[1] = 1;
        }

        if (s[1] == null) {
            s[1] = 0;
        }

        data[1] = s;
        s = new Object[]{"typeNoString", null, null};
        if ((state_0 & 2) != 0) {
            s[1] = 1;
        }

        if (s[1] == null) {
            s[1] = 0;
        }

        data[2] = s;
        return Introspection.Provider.create(data);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected Object type(TruffleString name) {
        TruffleLanguage.Env env = this.getRealm().getEnv();
        Object javaType = this.lookup(name, env);
        if (javaType == null) {
            throw Errors.createTypeErrorClassNotFound(name);
        } else {
            return javaType;
        }
    }

    private Object lookup(TruffleString name, TruffleLanguage.Env env) {
        if (env != null && env.isHostLookupAllowed()) {
            try {
                Class<?> clazz = Class.forName(Strings.toJavaString(name));
                return HostObjectCreator.create(clazz, env, JSRealm.get(null).getCallNode());
            } catch (Exception ignored) {
            }

            return lookupJavaType(name, env);
        } else {
            throw Errors.createTypeError("Java Interop is not available");
        }
    }

    @NeverDefault
    public static JavaBuiltins.JavaTypeNode create(JSContext context, JSBuiltin builtin, JavaScriptNode[] arguments) {
        return new JavaTypeNodeGenEx(context, builtin, arguments);
    }
}
