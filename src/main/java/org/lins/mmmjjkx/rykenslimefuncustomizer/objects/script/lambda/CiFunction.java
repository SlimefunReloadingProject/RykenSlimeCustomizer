package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda;

import com.caoccao.javet.annotations.V8Convert;
import com.caoccao.javet.enums.V8ProxyMode;

@FunctionalInterface
@V8Convert(proxyMode = V8ProxyMode.Function)
public interface CiFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}
