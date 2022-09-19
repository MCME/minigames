package com.mcmiddleearth.minigames.werewolf;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.geoGuessr.GeoGuessrBlacklist;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jubo
 */
public class WerewolfRoles {

    private File file;
    private FileConfiguration config;

    public WerewolfRoles() {
        this.file = new File(PluginData.getWerewolfDir(),"werewolfConfig.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public Map<String,Object> getRoles(){
        Map <String,Object> roles = new HashMap<>();
        Object name;
        Object description;

        for(int i = 0; i <= Integer.MAX_VALUE; i++){
            if(config.contains(String.valueOf(i))){
                name = config.getConfigurationSection(String.valueOf(i)).get("name");
                description = config.getConfigurationSection(String.valueOf(i)).get("description");
                roles.put(String.valueOf(name),description);
            }else{
                break;
            }
        }
        return roles;
    }

}
