package com.mcmiddleearth.minigames.geoGuessr;


import com.mcmiddleearth.minigames.data.PluginData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jubo
 */

public class GeoGuessrBlacklist {

    private File file;
    private FileConfiguration config;



    public GeoGuessrBlacklist()
    {
        this.file= new File(PluginData.getGeoGuessrDir(),"blacklist.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public Map <String,Object> show(){
        Map <String,Object> blacklist = new HashMap<>();
        blacklist = config.getValues(false);
        return blacklist;
    }

    public void add(String warp){
        for(int i = 0; i < Integer.MAX_VALUE;i++){
            if(!config.contains(String.valueOf(i))){
                config.set(String.valueOf(i),warp);
                break;
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String warp){
        if(!config.contains(warp)){
            return false;
        }
        config.set(warp,null);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
