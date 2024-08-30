package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.lambda;

@FunctionalInterface
public interface CiConsumer<A, B, C> {
    void accept(A a, B b, C c);
}
