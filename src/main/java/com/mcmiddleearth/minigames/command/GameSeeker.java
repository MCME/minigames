/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.*;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameSeeker extends AbstractGameCommand{
    
    public GameSeeker(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Appoints the seeker for the next round.");
        setUsageDescription(" <player>: Appoints <player> to be next seeker. Without using this command seeker will be randomly chosen from all players.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            OfflinePlayer seeker = game.getPlayer(args[0]);
            if(seeker==null) {
                sendPlayerNotFoundErrorMessage(cs);
            }
            else {
                if(game instanceof HideAndSeekGame){
                    ((HideAndSeekGame)game).setSeeker(seeker);
                    sendSeekerSetMessage(cs, seeker,"seeker.");
                } else if(game instanceof ManhuntGame){
                    ((ManhuntGame)game).setSeeker(seeker);
                    sendSeekerSetMessage(cs, seeker,"hunter.");
                }

            }
        }
    }
    
    private void sendPlayerNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found.");
    }

    private void sendSeekerSetMessage(CommandSender cs, OfflinePlayer seeker,String gameType) {
        PluginData.getMessageUtil().sendInfoMessage(cs, seeker.getName() +" will be the next "+gameType);
        if(PlayerUtil.getOnlinePlayer(seeker)!=null)
            PluginData.getMessageUtil().sendInfoMessage(PlayerUtil.getOnlinePlayer(seeker), 
                                        "You are assigned to be the next "+gameType);
    }

 }
