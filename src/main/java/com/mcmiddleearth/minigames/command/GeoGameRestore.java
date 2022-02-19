package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.geoGuessr.GeoGuessrSigns;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeoGameRestore extends AbstractGameCommand{

    public GeoGameRestore(String... permissionNodes){
        super(0,true,permissionNodes);
        cmdGroup = CmdGroup.GEO_GUESSR;
        setShortDescription("Restores signs");
        setUsageDescription("Restores signs if the server crashes and the game cant do it itself. Stores in a yml file.");
    }

    @Override
    protected void execute(CommandSender cs,String... args){
        GeoGuessrSigns signs = new GeoGuessrSigns();
        signs.restoreSigns((Player)cs);
        sendRestored(cs);
    }

    private void sendRestored(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"The signs were restored.");
    }
}
