package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ban;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class Delegations {
    public static <T extends Player> T delegatePlayer(String fileName, T p) {
        return (T) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class[]{Player.class}, new OperatorDelegation(fileName, p));
    }

    public static Server delegateServer(String fileName) {
        return (Server) Proxy.newProxyInstance(Server.class.getClassLoader(), new Class[]{Server.class}, new ServerDelegation(fileName));
    }

    public static <T extends Event> T replacePlayerInEvent(String fileName, T e) {
        try {
            Field field = Event.class.getDeclaredField("player");
            field.setAccessible(true);
            Player player = delegatePlayer(fileName, (Player) field.get(e));
            field.set(e, player);
            return e;
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            return e;
        }
    }
}
