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
        switch(this) {
            case HIDE_AND_SEEK: return "Hide and Seek";
            case RACE: return "Race";
            case LORE_QUIZ: return "Lore Quiz";
            case GEO_GUESSR: return "GeoGuessr";
            case MANHUNT: return "Manhunt";
            case WEREWOLF: return "Werewolf";
        }
        return "Illegal type";
    }

    public Class associatedClass() {
        try {
            switch(this) {
                case HIDE_AND_SEEK: return Class.forName("com.mcmiddleearth.minigames.game.HideAndSeekGame");
                case RACE: return Class.forName("com.mcmiddleearth.minigames.game.RaceGame");
                case LORE_QUIZ: return Class.forName("com.mcmiddleearth.minigames.game.QuizGame");
                case GEO_GUESSR: return Class.forName("com.mcmiddleearth.minigames.game.GeoGuessrGame");
                case MANHUNT: return Class.forName("com.mcmiddleearth.minigames.game.ManhuntGame");
                case WEREWOLF: return Class.forName("com.mcmiddleearth.minigames.game.WerewolfGame");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GameType.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
