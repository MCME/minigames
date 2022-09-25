package com.mcmiddleearth.minigames.werewolf;

import com.mcmiddleearth.minigames.data.PluginData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

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

    public List<String> getbyCategorySorted(String category){
        List<String> rolesByCategory = new ArrayList<>();

        for(int i = 0; i <= Integer.MAX_VALUE; i++){
            if(config.contains(String.valueOf(i))){
                if(String.valueOf(config.getConfigurationSection(String.valueOf(i)).get("category")).equalsIgnoreCase(category)){
                    rolesByCategory.add(String.valueOf(config.getConfigurationSection(String.valueOf(i)).get("name")));
                }
            }else{
                break;
            }
        }
        java.util.Collections.sort(rolesByCategory);
        return rolesByCategory;
    }

}
