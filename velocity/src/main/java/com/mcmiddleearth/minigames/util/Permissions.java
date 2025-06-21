package com.mcmiddleearth.minigames.util;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import java.util.Set;

public enum Permissions {
    USER("minigames.user"),
    MANAGER("minigames.manager", USER),
    STAFF("minigames.staff", MANAGER),
    ALL("minigames.*", STAFF);

    private final String permission;
    private final Set<Permissions> children;

    Permissions(String permission, Permissions... children){
        this.permission = permission;
        this.children = Set.of(children);
    }

    public boolean hasPermission(CommandSource source){
        return source instanceof Player player && hasPermission(player);
    }

    public boolean hasPermission(Player player){
        return player.hasPermission(this.permission) && children.stream().allMatch(child -> child.hasPermission(player));
    }
}
