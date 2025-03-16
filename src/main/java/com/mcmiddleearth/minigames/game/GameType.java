package com.mcmiddleearth.minigames.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Eriol_Eandur, Jubo
 */
public enum GameType {
    HIDE_AND_SEEK   ("Hide"),
    RACE            ("Race"),
    LORE_QUIZ       ("Quiz"),
    GEO_GUESSR      ("Geo"),
    MANHUNT         ("Manhunt"),
    WEREWOLF        ("Werewolf");

    private final String name;

    GameType(String name) {
        this.name = name;
    }

    public static GameType getGameType(String name) {
        for(GameType type: GameType.values()) {
            if(type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public static List<String> toList(){
        List<String> names = new ArrayList<>();
        for(GameType type: GameType.values())
            names.add(type.name);
        return names;
    }

    @Override
    public String toString() {
        return switch (this) {
            case HIDE_AND_SEEK -> "Hide and Seek";
            case RACE -> "Race";
            case LORE_QUIZ -> "Lore Quiz";
            case GEO_GUESSR -> "GeoGuessr";
            case MANHUNT -> "Manhunt";
            case WEREWOLF -> "Werewolf";
        };
    }

    public Class associatedClass() {
        try {
            return switch (this) {
                case HIDE_AND_SEEK -> Class.forName("com.mcmiddleearth.minigames.game.HideAndSeekGame");
                case RACE -> Class.forName("com.mcmiddleearth.minigames.game.RaceGame");
                case LORE_QUIZ -> Class.forName("com.mcmiddleearth.minigames.game.QuizGame");
                case GEO_GUESSR -> Class.forName("com.mcmiddleearth.minigames.game.GeoGuessrGame");
                case MANHUNT -> Class.forName("com.mcmiddleearth.minigames.game.ManhuntGame");
                case WEREWOLF -> Class.forName("com.mcmiddleearth.minigames.game.WerewolfGame");
            };
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GameType.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}