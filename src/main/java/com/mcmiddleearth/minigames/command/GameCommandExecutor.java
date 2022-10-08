/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.Permissions;
import com.mcmiddleearth.minigames.data.PluginData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Eriol_Eandur
 */
public class GameCommandExecutor implements CommandExecutor {

    private final Map <String, AbstractCommand> commands = new LinkedHashMap <>();
    
    public GameCommandExecutor() {
        addCommandHandler("acceptquestions", new QuizGameQuestionsAccept(Permissions.STAFF));
        addCommandHandler("allow", new GameAllow(Permissions.MANAGER));
        addCommandHandler("ban", new GameBan(Permissions.MANAGER));
        addCommandHandler("blacklist",new GeoGameBlacklist(Permissions.MANAGER));
        addCommandHandler("catch",new CatchGameCatch(Permissions.MANAGER));
        addCommandHandler("catcher",new CatchGameCatcher(Permissions.MANAGER));
        addCommandHandler("check", new GameCheck(Permissions.USER));
        addCommandHandler("clear", new QuizGameClear(Permissions.MANAGER));
        addCommandHandler("config",new WerewolfGameConfiguration(Permissions.MANAGER));
        addCommandHandler("create", new GameCreate(Permissions.MANAGER));
        addCommandHandler("delete", new GameDelete(Permissions.STAFF));
        addCommandHandler("deny", new GameDeny(Permissions.MANAGER));
        addCommandHandler("eliminate",new WerewolfGameEliminate(Permissions.MANAGER));
        addCommandHandler("end", new GameEnd(Permissions.MANAGER));
        addCommandHandler("field", new PvPGameField(Permissions.MANAGER));
        addCommandHandler("files", new GameFiles(Permissions.MANAGER));
        addCommandHandler("golfset", new GolfGameSet(Permissions.MANAGER));
        addCommandHandler("help", new GameHelp(Permissions.USER));
        addCommandHandler("hiddenlist", new GameHiddenList(Permissions.USER));
        addCommandHandler("hide", new HaSGameHide(Permissions.MANAGER));
        addCommandHandler("hunt",new ManhuntHunt(Permissions.MANAGER));
        addCommandHandler("info", new GameInfo(Permissions.USER));
        addCommandHandler("invite", new GameInvite(Permissions.MANAGER));
        addCommandHandler("join", new GameJoin(Permissions.USER));
        addCommandHandler("kick", new GameKick(Permissions.MANAGER));
        addCommandHandler("leaderboard",new GameLeaderboard(Permissions.USER));
        addCommandHandler("leave", new GameLeave(Permissions.USER));
        addCommandHandler("list",new WerewolfGameList(Permissions.USER));
        addCommandHandler("loadgolf", new GolfGameLoad(Permissions.MANAGER));
        addCommandHandler("loadout", new PvPGameLoadout(Permissions.MANAGER));
        addCommandHandler("loadpvp", new PvPGameLoad(Permissions.MANAGER));
        addCommandHandler("loadquestions", new QuizGameQuestionsLoad(Permissions.MANAGER));
        addCommandHandler("loadquiz", new QuizGameLoad(Permissions.MANAGER));
        addCommandHandler("loadrace", new RaceGameLoad(Permissions.MANAGER));
        addCommandHandler("manager", new GameManager(Permissions.MANAGER));
        addCommandHandler("manhunt_seeker",new ManhuntSeeker(Permissions.MANAGER));
        addCommandHandler("marker", new RaceGameMarker(Permissions.MANAGER));
        addCommandHandler("pardon",new WerewolfGamePardon(Permissions.MANAGER));
        addCommandHandler("pvpset", new PvPGameSet(Permissions.MANAGER));
        addCommandHandler("question", new QuizGameQuestion(Permissions.USER));
        addCommandHandler("raceset", new RaceGameSet(Permissions.MANAGER));
        addCommandHandler("racestats",new RaceGameStats(Permissions.USER));
        addCommandHandler("radius",new HaSGameRadius(Permissions.MANAGER));
        addCommandHandler("random", new QuizGameRandom(Permissions.MANAGER));
        addCommandHandler("ready", new GameReady(Permissions.MANAGER));
        addCommandHandler("remove", new RaceGameRemove(Permissions.MANAGER));
        addCommandHandler("resetscores",new RaceGameDeleteHighscore(Permissions.STAFF));
        addCommandHandler("respawn", new PvPGameRespawn(Permissions.MANAGER));
        addCommandHandler("restart", new GameRestart(Permissions.MANAGER));
        addCommandHandler("restoresigns",new GeoGameRestore(Permissions.STAFF));
        addCommandHandler("reviewquestions", new QuizGameQuestionsReview(Permissions.STAFF));
        addCommandHandler("revive",new WerewolfGameRevive(Permissions.MANAGER));
        addCommandHandler("roleinfo",new WereWolfGameRoleInfo(Permissions.USER));
        addCommandHandler("round", new GeoGameRound(Permissions.MANAGER));
        addCommandHandler("savegolf", new GolfGameSave(Permissions.MANAGER));
        addCommandHandler("saveloadout", new PvPGameSaveLoadout(Permissions.MANAGER));
        addCommandHandler("savemarker", new RaceGameSaveMarker(Permissions.MANAGER));
        addCommandHandler("savepvp", new PvPGameSave(Permissions.MANAGER));
        addCommandHandler("savequiz", new QuizGameSave(Permissions.MANAGER));
        addCommandHandler("saverace", new RaceGameSave(Permissions.MANAGER));
        addCommandHandler("seeker", new GameSeeker(Permissions.MANAGER));
        addCommandHandler("send", new QuizGameSend(Permissions.MANAGER));
        addCommandHandler("setarea", new GeoGameSetArea(Permissions.MANAGER));
        addCommandHandler("setrounds", new GeoGameSetRounds(Permissions.MANAGER));
        addCommandHandler("show", new RaceGameShow(Permissions.MANAGER));
        addCommandHandler("showcategories", new QuizGameShowCategories(Permissions.USER));
        addCommandHandler("spectate", new GameSpectate(Permissions.USER));
        addCommandHandler("start", new GameStart(Permissions.MANAGER));
        addCommandHandler("stat", new QuizGameStatus(Permissions.MANAGER));
        addCommandHandler("stats",new GameStats(Permissions.USER));
        addCommandHandler("stop", new RaceGameStop(Permissions.MANAGER));
        addCommandHandler("submitquestion", new QuizGameQuestionsSubmit(Permissions.USER));
        addCommandHandler("switchables",new GameSwitchables(Permissions.MANAGER));
        addCommandHandler("teamblue", new PvPGameTeamBlue(Permissions.MANAGER));
        addCommandHandler("teamred", new PvPGameTeamRed(Permissions.MANAGER));
        addCommandHandler("tpcp",new RaceGameTPCheckpoint(Permissions.USER));
        addCommandHandler("tphere",new HaSGameTPHere(Permissions.MANAGER));
        addCommandHandler("tpstart", new RaceGameStartTP(Permissions.MANAGER));
        addCommandHandler("unban", new GameUnban(Permissions.MANAGER));
        addCommandHandler("unstuck", new HaSGameUnstuck(Permissions.USER));
        addCommandHandler("vote",new WerewolfGameVote(Permissions.USER));
        addCommandHandler("warp", new GameWarp(Permissions.USER));
        addCommandHandler("winner", new GameWinner(Permissions.MANAGER));
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!string.equalsIgnoreCase("game")) {
            return false;
        }
        if(strings == null || strings.length == 0) {
            sendNoSubcommandErrorMessage(cs);
            return true;
        }
        if(commands.containsKey(strings[0].toLowerCase())) {
            commands.get(strings[0].toLowerCase()).handle(cs, Arrays.copyOfRange(strings, 1, strings.length));
        } else {
            sendSubcommandNotFoundErrorMessage(cs);
        }
        return true;
    }
    
    private void sendNoSubcommandErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You're missing subcommand name for this command.");
    }
    
    private void sendSubcommandNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Subcommand not found.");
    }
    
    private void addCommandHandler(String name, AbstractCommand handler) {
        commands.put(name, handler);
    }

    public Map<String, AbstractCommand> getCommands() {
        return commands;
    }
}
