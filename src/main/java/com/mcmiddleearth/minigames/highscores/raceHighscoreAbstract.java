package com.mcmiddleearth.minigames.highscores;

import com.mcmiddleearth.minigames.data.PluginData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Jubo
 */

public class raceHighscoreAbstract {

    private File file;
    private FileConfiguration config;
    private String filename;

    public raceHighscoreAbstract(String filename, Player manager) {
        this.file = new File(PluginData.getHighscoreDir(),"racetime.yml");
        config = YamlConfiguration.loadConfiguration(file);
        this.filename = filename;
        if(!config.contains(filename)) {
            config.createSection(filename);
            config.getConfigurationSection(filename).createSection("PB");
            config.getConfigurationSection(filename).createSection("Best");
            config.getConfigurationSection(filename).getConfigurationSection("Best");
            config.getConfigurationSection(filename).getConfigurationSection("Best").set("UUID",String.valueOf(manager.getUniqueId()));
            config.getConfigurationSection(filename).getConfigurationSection("Best").set("Time",Integer.MAX_VALUE-1);
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
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Integer.MAX_VALUE;
        }
    }

    public Map.Entry getHighscore(){
        Map<String,Object> highscore = new HashMap<>();//config.getConfigurationSection(filename).getConfigurationSection("Highscore").getValues(false);
        //Map.Entry<String,Object> highscore = null;
        Object uuid = config.getConfigurationSection(filename).getConfigurationSection("Best").get("UUID");
        Object time = config.getConfigurationSection(filename).getConfigurationSection("Best").get("Time");
        highscore.put((String)uuid,time);
        return highscore.entrySet().iterator().next();
    }

    public void setHighscore(UUID uuid, int Time){
        config.getConfigurationSection(filename).getConfigurationSection("Best").set("UUID",String.valueOf(uuid));
        config.getConfigurationSection(filename).getConfigurationSection("Best").set("Time",(Object) Time);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetHighscore(UUID uuid){
        config.getConfigurationSection(filename).getConfigurationSection("Best").set("UUID",String.valueOf(uuid));
        config.getConfigurationSection(filename).getConfigurationSection("Best").set("Time",(Object) Integer.MAX_VALUE);
        Map<String,Object> pb = config.getConfigurationSection(filename).getConfigurationSection("PB").getValues(false);
        for(String key : pb.keySet()){
            config.getConfigurationSection(filename).getConfigurationSection("PB").set(key,Integer.MAX_VALUE);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String,Object> getTop5(){
        Map<String,Object> all = config.getConfigurationSection(filename).getConfigurationSection("PB").getValues(false);
        Map<String,Object> top5 = new HashMap<>();
        Map.Entry<String,Object> top;
        int j = 5;

        if(all.size() < j){
            j = all.size();
        }
        for(int i = 1;i <= j; i++) {
            top = null;
            for (Map.Entry<String, Object> entry : all.entrySet()) {
                if (top == null || (Integer) entry.getValue() < (Integer) top.getValue()) {
                    top = entry;
                }
            }
            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(top.getKey()));
            top5.put(String.valueOf(op.getName()), top.getValue());
            all.remove(top.getKey());
        }
        return top5;
    }
}