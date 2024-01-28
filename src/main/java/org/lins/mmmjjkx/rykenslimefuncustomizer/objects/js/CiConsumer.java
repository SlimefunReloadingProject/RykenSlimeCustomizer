package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js;

@FunctionalInterface
public interface CiConsumer<A,B,C> {
    void apply(A a, B b, C c);
}
