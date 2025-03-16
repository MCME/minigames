package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.Style;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;

public class PlayerListener{

    @Subscribe
    public void playerJoin(PostLoginEvent event){
        if(PluginData.gameRunning()){
            PluginData.getMessageUtil().sendInfoMessage(event.getPlayer(),"A game is currently running. Do "+ Style.HIGHLIGHT+"/game check "+ Style.INFO+" for more information.");
            for(String gameName : PluginData.getGames()){
                AbstractGame game = PluginData.getGame(gameName);
                if(game != null && event.getPlayer().getUsername().equalsIgnoreCase(game.getManager().getUsername()))
                    game.returnedManager(game.getManager());
            }
        }
    }

    @Subscribe
    public void playerSwitchServer(ServerConnectedEvent event){
        if(PluginData.isInGame(event.getPlayer())){
            AbstractGame game = PluginData.getGame(event.getPlayer());
            //game.getBoard().switchServer(event.getPlayer());
        }
    }

//    @Subscribe
//    public void playerLeave(PlayerDisconnectEvent event){
//        if(PluginData.isInGame(event.getPlayer())){
//            AbstractGame game = PluginData.getGame(event.getPlayer());
//            if(event.getPlayer().equals(game.getManager())){
//                game.selfDestruction();
//            }else{
//                game.removePlayer(event.getPlayer());
//            }
//        }
//        MinigameCommandSender.removeMcmePlayer(event.getPlayer());
//    }
//
//    @Override
//    public void execute(PluginMessageEvent pluginMessageEvent) {
//
//    }
}
