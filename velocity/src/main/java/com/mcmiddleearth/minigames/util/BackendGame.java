package com.mcmiddleearth.minigames.util;

import com.mcmiddleearth.minigames.GameType;
import com.velocitypowered.api.proxy.Player;

public record BackendGame(Player manager, GameType type) {

}
