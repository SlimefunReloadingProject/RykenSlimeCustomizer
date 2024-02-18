package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.lambda;

@FunctionalInterface
public interface CiFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}
