package com.mcmiddleearth.minigames.util;

/**
 *
 * @author Jubo
 */
public enum Permission {

    STAFF       ("minigames.staff"),
    MANAGER     ("minigames.manager"),
    USER        ("minigames.user");

    private final String permissionNode;

    Permission(String permissionNode){
        this.permissionNode = permissionNode;
    }

    public String getPermissionNode(){
        return permissionNode;
    }
}