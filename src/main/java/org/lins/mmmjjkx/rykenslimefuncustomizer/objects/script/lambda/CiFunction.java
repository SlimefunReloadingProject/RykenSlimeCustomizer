package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda;

@FunctionalInterface
public interface CiFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}
