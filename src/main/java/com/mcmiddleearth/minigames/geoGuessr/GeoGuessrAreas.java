package com.mcmiddleearth.minigames.geoGuessr;

import com.mcmiddleearth.minigames.data.PluginData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 *
 * @author Jubo
 */

public class GeoGuessrAreas {

    private File file;
    private FileConfiguration config;
    String area = "a";

    public GeoGuessrAreas(String area){
        this.file = new File(PluginData.getGeoGuessrDir(),"areas.yml");
        config = YamlConfiguration.loadConfiguration(file);
        this.area = area;
    }

    public int x1(){
        return config.getConfigurationSection(area).getInt("x1");
    }

    public int x2(){
        return config.getConfigurationSection(area).getInt("x2");
    }

    public int z1(){
        return config.getConfigurationSection(area).getInt("z1");
    }

    public int z2(){
        return config.getConfigurationSection(area).getInt("z2");
    }

    public String getName(){
        return config.getConfigurationSection(area).getString("name");
    }

    public void setArea(String area){
        this.area = area;
    }

    public boolean containsArea(String area){
        if(config.contains(area)){
            return true;
        }
        this.area = "a";
        return false;
    }
}
