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
        setShortDescription("Deactivates Warps in GeoGuessr");
        setUsageDescription("/game blacklist show; /game blacklist add <warpname>; /game blacklist delete <number in list> (seeable through show)");
    }

    @Override
    protected void execute(CommandSender cs, String... args){
        GeoGuessrBlacklist Blacklist = new GeoGuessrBlacklist();
        if(args[0].equals("show")){
            Map<String,Object> blacklist = Blacklist.show();
            sendWarpList(cs,blacklist);
        }
        if(args.length > 1) {
            String argsAdded = "";
            for(int i = 1; i < args.length; i++){
                argsAdded = argsAdded + " " + args[i];
            }
            argsAdded = argsAdded.substring(1);
            if (args[0].equals("add")) {
                Blacklist.add(argsAdded);
                sendAddedMessage(cs);
            } else if (args[0].equals("delete")) {
                if (Blacklist.delete(argsAdded)) {
                    sendDeletedMessage(cs);
                } else {
                    sendDeletedErrorMessage(cs);
                }
            } else {
                sendWrongCommandMessage(cs);
            }
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
