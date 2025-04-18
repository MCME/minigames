package com.mcmiddleearth.minigames.velocity.util;

import com.mcmiddleearth.minigames.game.GameType;
import com.velocitypowered.api.proxy.Player;

public class BackendGame {

    public final Player manager;
    public final GameType type;

    public BackendGame(Player manager, GameType type){
        this.manager = manager;
        this.type = type;
    }
}
