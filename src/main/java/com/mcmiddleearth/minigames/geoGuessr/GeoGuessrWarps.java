package com.mcmiddleearth.minigames.geoGuessr;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.mariadb.jdbc.MySQLDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Jubo
 */

public class GeoGuessrWarps {


    private final String dbUser;
    private final String dbPassword;
    private final String dbName;
    private final String dbIp;
    private final int port;

    private final MySQLDataSource dataBase;

    private Connection dbConnection;

    private PreparedStatement getWarp;

    private PreparedStatement getRows;


    private Map<String, String> worldUUID = new HashMap<>();

    private boolean connected = false;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private BukkitTask keepAliveTask;

    public GeoGuessrWarps() {
        Plugin plugin = MiniGamesPlugin.getPluginInstance();
        ConfigurationSection dbConfig = plugin.getConfig().getConfigurationSection("sqlConnection");
        dbUser = dbConfig.getString("user");
        dbPassword = dbConfig.getString("password");
        dbName = dbConfig.getString("name");
        dbIp = dbConfig.getString("ip");
        port = dbConfig.getInt("port");
        dataBase = new MySQLDataSource(dbIp, port, dbName);
        connect();

        keepAliveTask = new BukkitRunnable() {
            @Override
            public void run() {
                checkConnection();
            }
        }.runTaskTimerAsynchronously(MiniGamesPlugin.getPluginInstance(), 0, 1200);

    }

    public synchronized void disconnect() {
        connected = false;
        if(keepAliveTask !=null){
            keepAliveTask.cancel();
        }
        try {
            dbConnection.close();
        } catch (SQLException ex) {
            Logger.getLogger(GeoGuessrWarps.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void checkConnection() {
        try {
            if (connected && dbConnection.isValid(5)) {
                connected = true;
            } else {
                //throw new SQLException();
                if (dbConnection != null) {
                    dbConnection.close();
                }
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        connect();
                        MiniGamesPlugin.getPluginInstance().getLogger().log(Level.INFO,"Reconnection to warp database.");
                    }
                }.runTaskAsynchronously(MiniGamesPlugin.getPluginInstance());
            }
        } catch (SQLException ex) {
            Logger.getLogger(GeoGuessrWarps.class.getName()).log(Level.SEVERE, "No DB connection!!", ex);
            connected = false;
        }
    }

    private synchronized void connect() {
        try {
            dbConnection = dataBase.getConnection(dbUser, dbPassword);
            getWarp = dbConnection.prepareStatement("SELECT warp.name, warp.x, warp.y, warp.z FROM warp WHERE warp.type = 1 AND warp.world_id = ?");
            getWarp.setQueryTimeout(1);
            getRows = dbConnection.prepareStatement("SELECT COUNT(warp.name) FROM warp WHERE warp.type = 1 AND warp.world_id = ?");
            getWarp.setQueryTimeout(1);
            connected = true;
        } catch (SQLException ex) {
            Logger.getLogger(GeoGuessrWarps.class.getName()).log(Level.SEVERE, null, ex);
            connected = false;
        }
    }

    public synchronized String[][] getWarps(World world) {
        int i = 0;
        if (connected) {
            try {
                String str_uuid = "2";
                if (world.getName().equalsIgnoreCase("world")) {
                    str_uuid = "2";
                } else if (world.getName().equalsIgnoreCase("moria")) {
                    str_uuid = "5";
                }

                getRows.setString(1, str_uuid);
                getWarp.setString(1, str_uuid);
                ResultSet count = getRows.executeQuery();
                if (count.next()) {
                    int rows = count.getInt("COUNT(warp.name)");
                    String[][] warps = new String[rows][4];

                    ResultSet result = getWarp.executeQuery();

                    if (result.next()) {
                        do {
                            warps[i][0] = result.getString("warp.name");
                            warps[i][1] = result.getString("warp.x");
                            warps[i][2] = result.getString("warp.y");
                            warps[i][3] = result.getString("warp.z");
                            i++;
                        } while (result.next());
                        return warps;
                    }
                }
            } catch (SQLException throwables) {
                Logger.getLogger(GeoGuessrWarps.class.getName()).log(Level.SEVERE, null, throwables);
                connected = false;
            }
        }
        return null;
    }
}