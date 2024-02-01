package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.js.ban;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.permissions.ServerOperator;
import org.lins.mmmjjkx.rykenslimefuncustomizer.utils.ExceptionHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class OperatorDelegation implements InvocationHandler {
    private String fileName;
    private ServerOperator original;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("setOp")) {
            ExceptionHandler.handleDanger("在"+fileName+"脚本文件中发现后门（ServerOperator#setOp）,请联系附属对应作者进行处理！！！！！");
            return null;
        } else {
            return method.invoke(original, args);
        }
    }
}
