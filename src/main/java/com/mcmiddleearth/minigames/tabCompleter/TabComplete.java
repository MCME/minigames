package com.mcmiddleearth.minigames.tabCompleter;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TabComplete implements TabCompleter {

    final List<String> commandsManager = Arrays.asList("create");
    final List<String> commandsManagerGame = Arrays.asList("ban","allow", "deny", "end",
            "invite", "kick", "manager", "ready", "restart", "start", "unban","files");
    final List<String> commandsManagerGeo = Arrays.asList("winner", "round", "setrounds", "setarea", "blacklist");
    final List<String> commandsManagerHide = Arrays.asList("hide", "seeker", "tphere", "glow", "radius");
    final List<String> commandsManagerManhunt = Arrays.asList("manhunt_start", "manhunt_seeker");
    final List<String> commandsManagerQuiz = Arrays.asList("clear", "loadquiz", "loadquestions", "question", "random", "savequiz"
            , "send", "stat", "winner");
    final List<String> commandsManagerRace = Arrays.asList("loadrace", "marker", "remove", "savemarker", "saverace", "show"
            , "raceset", "stop", "tpstart");
    final List<String> commandsManagerWerewolf = Arrays.asList("eliminate", "revive", "pardon", "config");

    final List<String> commandsUser = Arrays.asList("stats", "check", "info", "join", "spectate", "warp", "leaderboard");
    final List<String> commandsUserGame = Arrays.asList("leave", "help");
    final List<String> commandsUserGeo = new ArrayList<>();
    final List<String> commandsUserHide = Arrays.asList("unstuck", "hiddenlist");
    final List<String> commandsUserManhunt = new ArrayList<>();
    final List<String> commandsUserQuiz = Arrays.asList("showCategories", "submitquestion");
    final List<String> commandsUserRace = Arrays.asList("tpcp", "racestats");
    final List<String> commandsUserWerewolf = Arrays.asList("vote", "list", "roleinfo");

    final List<String> commandsAllowDeny = Arrays.asList("flight","teleport","join","warp","spectate","collision","invisible"
            ,"signs","points","throwable","save");
    final List<String> commandsPlayer = Arrays.asList("vote","ban","invite","kick","manager","unban"
            ,"tphere","seeker","manhunt_seeker","eliminate","pardon");
    final List<String> commandsGametypes = Arrays.asList("geo","hide","race","quiz");



    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        List<String> commands = new ArrayList<>();
        String input = args[0].toLowerCase();
        List<String> completions = null;
        AbstractGame game = PluginData.getGame((Player)sender);
        Boolean arg_1 = true;

        if(input.startsWith("deny") || input.startsWith("allow")){
            commands.addAll(commandsAllowDeny);
        }else if(commandsPlayer.contains(input)){
            if(game != null){
                for(Player player :game.getOnlinePlayers()){
                    commands.add(player.getName());
                }
                if(input.startsWith("vote")){
                    commands.add("yay");
                    commands.add("nay");
                }
            }
        }else if(input.startsWith("roleinfo")){
            if(game != null) {
                WerewolfGame werewolf = (WerewolfGame) game;
                Set<String> roleNames = werewolf.getRoleNames();
                commands.addAll(roleNames);
            }
        } else if(input.startsWith("create")){
            if(game == null){
                commands.addAll(commandsGametypes);
            }
        } else {
            arg_1 = false;
            if (game == null) {
                commands.addAll(commandsUser);
                commands.addAll(commandsManager);
            } else {
                if (PluginData.isManager((Player) sender)) {
                    commands.addAll(commandsManagerGame);
                    if (game instanceof GeoGuessrGame) {
                        commands.addAll(commandsManagerGeo);
                    } else if (game instanceof HideAndSeekGame) {
                        commands.addAll(commandsManagerHide);
                    } else if (game instanceof ManhuntGame) {
                        commands.addAll(commandsManagerManhunt);
                    } else if (game instanceof QuizGame) {
                        commands.addAll(commandsManagerQuiz);
                    } else if (game instanceof RaceGame) {
                        commands.addAll(commandsManagerRace);
                    } else if (game instanceof WerewolfGame) {
                        commands.addAll(commandsManagerWerewolf);
                    }
                }
                commands.addAll(commandsUserGame);
                if (game instanceof GeoGuessrGame) {
                    commands.addAll(commandsUserGeo);
                } else if (game instanceof HideAndSeekGame) {
                    commands.addAll(commandsUserHide);
                } else if (game instanceof ManhuntGame) {
                    commands.addAll(commandsUserManhunt);
                } else if (game instanceof QuizGame) {
                    commands.addAll(commandsUserQuiz);
                } else if (game instanceof RaceGame) {
                    commands.addAll(commandsUserRace);
                } else if (game instanceof WerewolfGame) {
                    commands.addAll(commandsUserWerewolf);
                }
            }
        }
        if(args.length > 1 && arg_1){
            input = args[1];
        }
        for(String s : commands){
            if(s.startsWith(input)){
                if(completions == null){
                    completions = new ArrayList<>();
                }
                completions.add(s);
            }
        }
        if(completions != null ){
            java.util.Collections.sort(completions);
        }else {
            completions = commands;
        }
        return completions;
    }
}
