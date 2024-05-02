package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda;

@FunctionalInterface
public interface CiConsumer<A, B, C> {
    void apply(A a, B b, C c);
}
