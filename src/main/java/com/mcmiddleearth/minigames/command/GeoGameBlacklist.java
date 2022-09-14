package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.geoGuessr.GeoGuessrBlacklist;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 *
 * @author Jubo
 */

public class GeoGameBlacklist extends AbstractGameCommand{

    public GeoGameBlacklist(String... permissionNodes){
        super(1,true,permissionNodes);
        cmdGroup = CmdGroup.GEO_GUESSR;
        setShortDescription("");
        setUsageDescription("");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        GeoGuessrBlacklist Blacklist = new GeoGuessrBlacklist();
        if(args[0].equals("show")){
            Map<String,Object> blacklist = Blacklist.show();
            sendWarpList(cs,blacklist);
        }else if(args[0].equals("add")){
            Blacklist.add(args[1]);
            sendAddedMessage(cs);
        }else if(args[0].equals("delete")){
            if(Blacklist.delete(args[1])){
                sendDeletedMessage(cs);
            }else{
                sendDeletedErrorMessage(cs);
            }
        }else{
            sendWrongCommandMessage(cs);
        }
    }

    private void sendWarpList(CommandSender cs,Map<String,Object> blacklist){
        PluginData.getMessageUtil().sendInfoMessage(cs,String.valueOf(blacklist));
    }

    private void sendAddedMessage(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"Warp has been added successfully.");
    }

    private void sendDeletedMessage(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"Warp was deleted successfully.");
    }

    private void sendDeletedErrorMessage(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"There went something wrong. You need to use the number.");
    }

    private void sendWrongCommandMessage(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"This is not an accepted command.");
    }
}
