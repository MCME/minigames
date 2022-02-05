package com.mcmiddleearth.minigames.highscores;

import com.mcmiddleearth.minigames.data.PluginData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Jubo
 */

public class gameWinHighscore {

    private File file;
    private FileConfiguration config;

    public gameWinHighscore(){
        this.file = new File(PluginData.getHighscoreDir(),"highscore.yml");
        config = YamlConfiguration.loadConfiguration(file);
        List<String> games = Arrays.asList("hide","geo","race","quiz");
        for(String game : games){
            if(!config.contains(game)){
                config.createSection(game);
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean gameExists(String game){
        if(config.contains(game) || game.equalsIgnoreCase("seek")){
            return true;
        }
        return false;
    }

    public Map<String,Integer> getWins(UUID uuid) {
        Map<String,Integer> wins = new HashMap<>();
        boolean save = false;
        if(!config.getConfigurationSection("hide").getConfigurationSection("seek").contains(String.valueOf(uuid))){
            config.getConfigurationSection("hide").getConfigurationSection("seek").set(String.valueOf(uuid),0);
            save = true;
        }
        if(!config.getConfigurationSection("hide").getConfigurationSection("hide").contains(String.valueOf(uuid))){
            config.getConfigurationSection("hide").getConfigurationSection("hide").set(String.valueOf(uuid),0);
            save = true;
        }
        if(!config.getConfigurationSection("geo").contains(String.valueOf(uuid))){
            config.getConfigurationSection("geo").set(String.valueOf(uuid),0);
            save = true;
        }
        if(!config.getConfigurationSection("race").contains(String.valueOf(uuid))){
            config.getConfigurationSection("race").set(String.valueOf(uuid),0);
            save = true;
        }
        if(!config.getConfigurationSection("quiz").contains(String.valueOf(uuid))){
            config.getConfigurationSection("quiz").set(String.valueOf(uuid),0);
            save = true;
        }

        if(save){
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int seek = (Integer) config.getConfigurationSection("hide").getConfigurationSection("seek").get(String.valueOf(uuid));
        int hide = (Integer) config.getConfigurationSection("hide").getConfigurationSection("hide").get(String.valueOf(uuid));
        int geo  = (Integer) config.getConfigurationSection("geo").get(String.valueOf(uuid));
        int race = (Integer) config.getConfigurationSection("race").get(String.valueOf(uuid));
        int quiz = (Integer) config.getConfigurationSection("quiz").get(String.valueOf(uuid));

        wins.put("Seeker wins",seek);
        wins.put("Hide wins",hide);
        wins.put("GeoGuessr wins",geo);
        wins.put("Race wins",race);
        wins.put("Quiz wins",quiz);

        return wins;
    }

    public void setHideWin(UUID uuid){
        if(!config.getConfigurationSection("hide").getConfigurationSection("hide").contains(String.valueOf(uuid))){
            config.getConfigurationSection("hide").getConfigurationSection("hide").set(String.valueOf(uuid),0);
        }
        int wins = config.getConfigurationSection("hide").getConfigurationSection("hide").getInt(String.valueOf(uuid));
        config.getConfigurationSection("hide").getConfigurationSection("hide").set(String.valueOf(uuid),wins+1);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSeekWin(UUID uuid){
        if(!config.getConfigurationSection("hide").getConfigurationSection("seek").contains(String.valueOf(uuid))){
            config.getConfigurationSection("hide").getConfigurationSection("seek").set(String.valueOf(uuid),0);
        }
        int wins = config.getConfigurationSection("hide").getConfigurationSection("seek").getInt(String.valueOf(uuid));
        config.getConfigurationSection("hide").getConfigurationSection("seek").set(String.valueOf(uuid),wins+1);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRaceWin(UUID uuid){
        if(!config.getConfigurationSection("race").contains(String.valueOf(uuid))){
            config.getConfigurationSection("race").set(String.valueOf(uuid),0);
        }
        int wins = config.getConfigurationSection("race").getInt(String.valueOf(uuid));
        config.getConfigurationSection("race").set(String.valueOf(uuid),wins+1);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGeoWin(UUID uuid){
        if(!config.getConfigurationSection("geo").contains(String.valueOf(uuid))){
            config.getConfigurationSection("geo").set(String.valueOf(uuid),0);
        }
        int wins = config.getConfigurationSection("geo").getInt(String.valueOf(uuid));
        config.getConfigurationSection("geo").set(String.valueOf(uuid),wins+1);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setQuizWin(UUID uuid){
        if(!config.getConfigurationSection("quiz").contains(String.valueOf(uuid))){
            config.getConfigurationSection("quiz").set(String.valueOf(uuid),0);
        }
        int wins = config.getConfigurationSection("quiz").getInt(String.valueOf(uuid));
        config.getConfigurationSection("quiz").set(String.valueOf(uuid),wins+1);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String,Object> getLeaderboard(String game, Integer count){
        Map<String,Object> all;
        Map<String,Object> leaderboard = new HashMap<>();
        Map.Entry<String,Object> top;

        if(game.equalsIgnoreCase("hide") || game.equalsIgnoreCase("seek")){
            all = config.getConfigurationSection("hide").getConfigurationSection(game).getValues(false);
        }else{
            all = config.getConfigurationSection(game).getValues(false);
        }

        if(all.size() < count){
            count = all.size();
        }

        for(int i = 1;i <= count; i++){
            top = null;
            for (Map.Entry<String, Object> entry : all.entrySet()) {
                if (top == null || (Integer) entry.getValue() > (Integer) top.getValue()) {
                    top = entry;
                }
            }
            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(top.getKey()));
            leaderboard.put(String.valueOf(op.getName()), top.getValue());
            all.remove(top.getKey());
        }
        return leaderboard;
    }
}