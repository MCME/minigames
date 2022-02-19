package com.mcmiddleearth.minigames.geoGuessr;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.pluginutil.plotStoring.IStoragePlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeoGuessrSigns implements IStoragePlot {

    private static final File restoreDir = new File(PluginData.getGeoGuessrDir(),"restoreData");

    private Location location;
    private Location lowCorner;
    private Location highCorner;

    private static final String restoreExt = "res";

    private Map<Location,List<String>> blocks = new HashMap<>();

    private final List<Material> signs = Arrays.asList(
            Material.OAK_SIGN,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.ACACIA_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.BIRCH_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.CRIMSON_SIGN,
            Material.CRIMSON_WALL_SIGN,
            Material.JUNGLE_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.WARPED_SIGN,
            Material.WARPED_WALL_SIGN,
            Material.DARK_OAK_SIGN,
            Material.DARK_OAK_WALL_SIGN
    );

    public GeoGuessrSigns(){}

    // other things to do. hide radius during the game, new minigame catch, add a round during game geo

    public void removeSigns(Location location,double radius) {
        if(!restoreDir.exists()) {
            restoreDir.mkdir();
        }
        File restoreFile = new File(restoreDir+"."+restoreExt);
        if(restoreFile.exists()) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "RestoreFile already exists.");
            return;
        }
        lowCorner = null;
        highCorner = null;
        Location loc = location;
        double xloc = loc.getX();
        double yloc = loc.getY();
        double zloc = loc.getZ();

        for(int x =  (int)(xloc-radius);x <= (xloc+radius); x++){
            for(int y =(int)(yloc-radius);y<=(yloc+radius);y++){
                for(int z = (int)(zloc-radius);z<=(zloc+radius);z++){
                    Location loc2 = new Location(location.getWorld(),x,y,z);
                    Block b = loc2.getBlock();
                    if(signs.contains(b.getType())){
                        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) b.getState();
                        List<String> signText = new ArrayList<>();
                        for(int i =1; i <= 4;i++){
                            signText.add(sign.getLine(i-1));
                            sign.setLine(i-1,"");
                        }
                        blocks.put(b.getLocation(),signText);
                        Bukkit.getPlayer("Jubo").sendMessage(String.valueOf(b.getLocation()));
                        sign.update();
                    }
                }
            }
        }
        restoreFile();
    }

    public void replaceSigns(){
        for (Map.Entry<Location,List<String>> entry : blocks.entrySet()) {
            Block block = entry.getKey().getBlock();
            org.bukkit.block.Sign temp = (org.bukkit.block.Sign) block.getState();
            List<String> signText = entry.getValue();
            for(int i = 1;i <= 4;i++){
                temp.setLine(i-1,signText.get(i-1));
            }
            temp.update();
        }
    }

    private void restoreFile(){
        File file = new File(PluginData.getGeoGuessrRestoreDir(),"restoreGeo.yml");
        file.delete();
        FileConfiguration config = new YamlConfiguration();
        int i = 0;
        for(Map.Entry<Location,List<String>> entry : blocks.entrySet()){
            config.createSection(String.valueOf(i));
            config.getConfigurationSection(String.valueOf(i)).set("x",entry.getKey().getX());
            config.getConfigurationSection(String.valueOf(i)).set("y",entry.getKey().getY());
            config.getConfigurationSection(String.valueOf(i)).set("z",entry.getKey().getZ());
            config.getConfigurationSection(String.valueOf(i)).set("0",entry.getValue().get(0));
            config.getConfigurationSection(String.valueOf(i)).set("1",entry.getValue().get(1));
            config.getConfigurationSection(String.valueOf(i)).set("2",entry.getValue().get(2));
            config.getConfigurationSection(String.valueOf(i)).set("3",entry.getValue().get(3));
            i++;
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreSigns(Player player){
        File file = new File(PluginData.getGeoGuessrRestoreDir(),"restoreGeo.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for(int i = 0; i <= Integer.MAX_VALUE; i++){
            if(config.contains(String.valueOf(i))){
                double x = config.getConfigurationSection(String.valueOf(i)).getDouble("x");
                double y = config.getConfigurationSection(String.valueOf(i)).getDouble("y");
                double z = config.getConfigurationSection(String.valueOf(i)).getDouble("z");
                List<String> signText = new ArrayList<>();
                for(int l = 0;l < 4;l++){
                    signText.add(config.getConfigurationSection(String.valueOf(i)).getString(String.valueOf(l)));
                }
                Location loc = new Location(player.getWorld(),x,y,z);
                org.bukkit.block.Sign sign = (org.bukkit.block.Sign) loc.getBlock().getState();
                for(int j = 0; j < 4;j++ ){
                    sign.setLine(j,signText.get(j));
                }
                sign.update();
            }else{
                return;
            }
        }
    }


    @Override
    public World getWorld() {
        return location.getWorld();
    }

    @Override
    public Location getLowCorner() {
        return lowCorner;
    }

    @Override
    public Location getHighCorner() {
        return highCorner;
    }

    @Override
    public boolean isInside(Location location) {
        return false;
    }
}
