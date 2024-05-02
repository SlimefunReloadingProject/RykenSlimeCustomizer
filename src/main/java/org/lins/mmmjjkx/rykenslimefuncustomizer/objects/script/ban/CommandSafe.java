package org.lins.mmmjjkx.rykenslimefuncustomizer.objects.script.ban;

import java.util.List;

public class CommandSafe {
    private static final List<String> badCommands = List.of(
            "sudo",
            "stop",
            "restart",
            "op",
            "deop",
            "whitelist",
            "ban-ip",
            "banlist",
            "pardon",
            "kick",
            "ban",
            "pardon-ip",
            "save-all");

    public static boolean isBadCommand(String command) {
        return badCommands.contains(command.toLowerCase());
    }
}
