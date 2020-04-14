package com.mcmiddleearth.minigames.golf;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author Planetology
 */
public class GolfPlayer {

    private Player golfer;
    private int shots, points;
    private Location arrowLocation;
    private Material arrowBlockMaterial;
    private boolean shot;

    public GolfPlayer(Player player) {
        golfer = player;
        shots = 0;
        points = 0;
    }

    public Player getGolfer() {
        return golfer;
    }

    public void setGolfer(Player golfer) {
        this.golfer = golfer;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Location getArrowLocation() {
        return arrowLocation;
    }

    public void setArrowLocation(Location arrowLocation) {
        this.arrowLocation = arrowLocation;
    }

    public Material getArrowBlockMaterial() {
        return arrowBlockMaterial;
    }

    public void setArrowBlockMaterial(Material arrowBlockMaterial) {
        this.arrowBlockMaterial = arrowBlockMaterial;
    }

    public boolean isShot() {
        return shot;
    }

    public void setShot(boolean shot) {
        this.shot = shot;
    }
}
