package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.Style;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerJoin(PostLoginEvent event){
        if(PluginData.gameRunning()){
            PluginData.getMessageUtil().sendInfoMessage(event.getPlayer(),"A game is currently running. Do "+ Style.HIGHLIGHT+"/newgame check "+ Style.INFO+" for more information.");
            for(String gameName : PluginData.getGames()){
                AbstractGame game = PluginData.getGame(gameName);
                if(event.getPlayer().getName().equalsIgnoreCase(game.getManager().getName()))
                    game.returnedManager(game.getManager());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerLeave(PlayerDisconnectEvent event){
        if(PluginData.isInGame(event.getPlayer())){
            AbstractGame game = PluginData.getGame(event.getPlayer());
            if(event.getPlayer().equals(game.getManager())){
                game.selfDestruction();
            }else{
                game.removePlayer(event.getPlayer());
            }
            MinigameCommandSender.removeMcmePlayer(event.getPlayer());
        }
    }
}
