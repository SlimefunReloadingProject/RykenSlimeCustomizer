package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.ban;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ServerDelegation implements InvocationHandler {
    private final Server original = Bukkit.getServer();

    private final String fileName;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "banIp" -> {
                ExceptionHandler.handleDanger("发现"+fileName+"脚本文件执行ban人操作（Server#banIP）,请联系附属对应作者进行处理！！！！！");
                return null;
            }
            case "unbanIp" -> {
                ExceptionHandler.handleDanger("发现"+fileName+"脚本文件执行取消ban人操作（Server#banIP）,请联系附属对应作者进行处理！！！！！");
                return null;
            }
            case "getBanList" -> {
                ExceptionHandler.handleDanger("发现"+fileName+"脚本文件执行获取ban列表操作（Server#getBanList）,请联系附属对应作者进行处理！！！！！");
                return null;
            }
            case "getPlayer" -> {
                Object arg = args[0];
                Player p;
                if (arg instanceof UUID uid) {
                    p = original.getPlayer(uid);
                } else if (arg instanceof String id) {
                    p = original.getPlayer(id);
                } else {
                    return null;
                }
                if (p != null) {
                    return Delegations.delegatePlayer(fileName, p);
                }
            }
            case "getPlayerExact" -> {
                Object arg = args[0];
                Player p = original.getPlayer(arg.toString());
                if (p == null) return null;
                return Delegations.delegatePlayer(fileName, p);
            }
            case "getPluginManager" -> {
                ExceptionHandler.handleDanger("发现"+fileName+"脚本文件执行获取插件管理器操作（Server#getPluginManager）,请联系附属对应作者进行处理！！！！！");
                return null;
            }
            case "getServicesManager" -> {
                ExceptionHandler.handleDanger("发现"+fileName+"脚本文件执行获取服务管理器操作（Server#getServicesManager）,请联系附属对应作者进行处理！！！！！");
                return null;
            }
            case "setWhitelistEnforced" -> {
                ExceptionHandler.handleDanger("发现"+fileName+"脚本文件执行设置强制白名单操作（Server#setWhitelistEnforced）,请联系附属对应作者进行处理！！！！！");
                return null;
            }
            case "setWhitelist" -> {
                ExceptionHandler.handleDanger("发现"+fileName+"脚本文件执行开启强制白名单操作（Server#setWhitelistEnforced）,请联系附属对应作者进行处理！！！！！");
                return null;
            }
            case "getIp" -> {
                return "";
            }
            case "getPort" -> {
                return -1;
            }
            case "getCommandMap", "setIdleTimeout", "getConsoleSender", "getIPBans" -> {
                return null;
            }
        }

        return method.invoke(original, args);
    }
}
