package com.mcmiddleearth.minigames.geoGuessr;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.mariadb.jdbc.MySQLDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    private Map config = new HashMap();

    private final MySQLDataSource dataBase;

    private Connection dbConnection;

    private PreparedStatement getWarp;

    private PreparedStatement getRows;


    private Map<String, String> worldUUID = new HashMap<>();

    private boolean connected = false;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /*  TESTING
    private static String[][] warp_list = {
            {"test0", "0", "70", "0",},
            {"test 1", "1", "70", "1"},
            {"test 2", "2", "70", "2"},
            {"test3", "3", "70", "3"},
            {"test4", "4", "70", "4"},
            {"test5", "5", "70", "5"},
            {"test6", "6", "70", "6"},
    };

    public static String[][] getWarps_test() {
        return warp_list;
    }
    */



    public GeoGuessrWarps() {
        Plugin plugin = MiniGamesPlugin.getPluginInstance();
        ConfigurationSection dbConfig = plugin.getConfig().getConfigurationSection("sqlConnection");
        dbUser = dbConfig.getString("user");
        dbPassword = dbConfig.getString("password");
        dbName = dbConfig.getString("name");
        dbIp = dbConfig.getString("localhost");
        port = dbConfig.getInt("port");
        dataBase = new MySQLDataSource(dbIp, port, dbName);
        connect();
        boolean check = checkConnection();
    }

    public void disconnect() {
        connected = false;
        try {
            dbConnection.close();
        } catch (SQLException ex) {
            Logger.getLogger(GeoGuessrWarps.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean checkConnection() {
        try {
            if (connected && dbConnection.isValid(5)) {
                connected = true;
            } else {
                //throw new SQLException();
                if (dbConnection != null) {
                    dbConnection.close();
                }
                connect();
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(GeoGuessrWarps.class.getName()).log(Level.SEVERE, "No DB connection!!", ex);
            connected = false;
            return false;
        }
    }

    private void connect() {
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

    public String[][] getWarps(UUID uuid) {
        int i = 0;
        if (connected) {
            try {
                String str_uuid = "2";
                getRows.setString(1, String.valueOf(str_uuid));
                getWarp.setString(1, String.valueOf(str_uuid));
                ResultSet count = getRows.executeQuery();
                if (count.first()) {
                    int rows = count.getInt("COUNT(warp.name)");
                    String[][] warps = new String[rows][4];

                    ResultSet result = getWarp.executeQuery();

                    if (result.first()) {
                        while (result.next()) {
                            warps[i][0] = result.getString("warp.name");
                            warps[i][1] = result.getString("warp.x");
                            warps[i][2] = result.getString("warp.y");
                            warps[i][3] = result.getString("warp.z");
                            i++;
                        }
                        return warps;
                    }
                }
                return warps;
            } catch (SQLException throwables) {
                Logger.getLogger(GeoGuessrWarps.class.getName()).log(Level.SEVERE, null, throwables);
                connected = false;
            }

        }
        return null;
    }
}


