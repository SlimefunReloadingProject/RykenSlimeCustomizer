package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.ban;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.permissions.ServerOperator;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class Delegations {
    public static <T extends ServerOperator> T delegatePlayer(String fileName, T p) {
        return (T) Proxy.newProxyInstance(ServerOperator.class.getClassLoader(), new Class[]{p.getClass()}, new OperatorDelegation(fileName, p));
    }

    public static Server delegateServer(String fileName) {
        return (Server) Proxy.newProxyInstance(Server.class.getClassLoader(), new Class[]{Server.class}, new ServerDelegation(fileName));
    }

    public static <T extends PlayerEvent> T replacePlayerInEvent(String fileName, T e) {
        Player player = delegatePlayer(fileName, e.getPlayer());
        try {
            Field field = PlayerEvent.class.getDeclaredField("player");
            field.setAccessible(true);
            field.set(e, player);
            return e;
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
