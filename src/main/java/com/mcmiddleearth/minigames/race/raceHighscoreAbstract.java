package com.mcmiddleearth.minigames.race;

import com.mcmiddleearth.minigames.data.PluginData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class raceHighscoreAbstract {

    private File file;
    private FileConfiguration config;
    private String filename;

    public raceHighscoreAbstract(String filename, Player manager) {
        this.file = new File(PluginData.getHighscoreDir(),"Highscore.yml");
        config = YamlConfiguration.loadConfiguration(file);
        this.filename = filename;
        if(!config.contains(filename)) {
            config.createSection(filename);
            config.getConfigurationSection(filename).createSection("PB");
            config.getConfigurationSection(filename).createSection("Highscore");
            config.getConfigurationSection(filename).getConfigurationSection("Highscore");
            config.getConfigurationSection(filename).getConfigurationSection("Highscore").set("UUID",String.valueOf(manager.getUniqueId()));
            config.getConfigurationSection(filename).getConfigurationSection("Highscore").set("Time",Integer.MAX_VALUE-1);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void setPB(UUID uuid,int PB) {
        config.getConfigurationSection(filename).getConfigurationSection("PB").set(String.valueOf(uuid), PB);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public int getPB(UUID uuid) {
        if (config.getConfigurationSection(filename).getConfigurationSection("PB").contains(String.valueOf(uuid))) {
            int pb = config.getConfigurationSection(filename).getConfigurationSection("PB").getInt(String.valueOf(uuid));
            return pb;
        }else{
            config.getConfigurationSection(filename).getConfigurationSection("PB").set(String.valueOf(uuid),Integer.MAX_VALUE);
            return Integer.MAX_VALUE;
        }
    }

    public Map.Entry getHighscore(){
        Map<String,Object> highscore = new HashMap<>();//config.getConfigurationSection(filename).getConfigurationSection("Highscore").getValues(false);
        //Map.Entry<String,Object> highscore = null;
        Object uuid = config.getConfigurationSection(filename).getConfigurationSection("Highscore").get("UUID");
        Object time = config.getConfigurationSection(filename).getConfigurationSection("Highscore").get("Time");
        highscore.put((String)uuid,time);
        return highscore.entrySet().iterator().next();
    }

    public void setHighscore(UUID uuid, int Time){
        config.getConfigurationSection(filename).getConfigurationSection("Highscore").set("UUID",String.valueOf(uuid));
        config.getConfigurationSection(filename).getConfigurationSection("Highscore").set("Time",(Object) Time);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
