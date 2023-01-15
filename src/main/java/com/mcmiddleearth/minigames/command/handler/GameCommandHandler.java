package com.mcmiddleearth.minigames.command.handler;

import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.minigames.command.MinigameCommandSender;
import com.mcmiddleearth.minigames.command.argument.*;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.util.Permission;
import com.mcmiddleearth.minigames.util.PluginData;
import com.mcmiddleearth.minigames.util.Style;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

/**
 * @author Jubo
 */
public class GameCommandHandler extends AbstractCommandHandler {

    public GameCommandHandler(String name){
        super(name);
    }

    /*
    commands not overtaken:
     /game ban
     /game create private
     /game unban
    TODO:
        rework stats, maybe with /game stats <gametype>
     */

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder helpfulLiteralBuilder) {
        helpfulLiteralBuilder
                .requires(sender -> (sender instanceof MinigameCommandSender))

                .then(HelpfulLiteralBuilder.literal("allow")
                        .withHelpText("Allows various actions for a game.")
                        .withTooltip("flight|teleport|join|warp|spectate|collision|invisible|signs: 'flight'/'teleport' allows for players in the game to fly or teleport. 'join' allows players to " +
                                "join without invitation. 'warp' allows player to warp to game location. 'spectate' allows players to see scoreboard of the game without participating.'collision' allows " +
                                "players to collide in games.'save' allows /game tpcp in races.'invisible' allows Invisibility in races.'signs' allow signs fr GeoGuessr")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isManager(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("allow",new CommandAllowDenyArgument())
                        .executes(context -> doCommand(context.getSource(), "allow/deny","allow",context.getArgument("allow",String.class)))))
                .then(HelpfulLiteralBuilder.literal("create")
                        .withTooltip("Creates a new mini game.")
                        .withHelpText("Only quiz implemented for now."+Style.NOTYET+"quiz|race|hide|golf|pvp|geo <gamename>: Creates a lore quiz or a race or a hide and seek or golf or " +
                                "a geoguessr game with name <gamename>. The location of the player issuing the command becomes the warp of the game.")
                        .requires(sender -> PluginData.hasPermission((MinigameCommandSender) sender, Permission.MANAGER) & !PluginData.isInGame((MinigameCommandSender) sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("gametype",new CommandGameTypeArgument())
                                .then(HelpfulRequiredArgumentBuilder.argument("name",word())
                                        .executes(context -> doCommand(context.getSource(), "create",context.getArgument("gametype",String.class),context.getArgument("name",String.class))))))
                .then(HelpfulLiteralBuilder.literal("check")
                        .withHelpText("Lists all current minigames.")
                        .withTooltip("Lists all current minigames.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.USER) && !PluginData.isInGame(sender))
                        .executes(context -> doCommand(context.getSource(), "check",null)))
                .then(HelpfulLiteralBuilder.literal("delete")
                        .withHelpText("Deletes saved minigame files.")
                        .withTooltip("quiz|race|marker|werewolf <filename>: Deletes a quiz, race, marker, golf or pvp data file with name <filename>.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.STAFF))
                        .then(HelpfulRequiredArgumentBuilder.argument("gametype",new CommandGameTypeArgument())
                                .then(HelpfulRequiredArgumentBuilder.argument("filename",word())
                                        .executes(context -> doCommand(context.getSource(), "delete",context.getArgument("gametype", String.class),context.getArgument("filename",String.class))))))
                .then(HelpfulLiteralBuilder.literal("deny")
                        .withHelpText("Denies various actions for a game.")
                        .withTooltip("flight|teleport|join|warp|save|collision|invisible|signs: 'flight' or 'teleport' denies for players in the game to fly or teleport. 'join' denies players to join without invitation. " +
                                "'warp' denies players to warp to game location. 'spectate' denies players to see the game scoreboad without participating. 'collision' denies players to collide in games.'save' denies /game tpcp " +
                                "in races.'invisible' denies Invisibility in races.'signs' removes Signs in GeoGuessr")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isManager(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("deny",new CommandAllowDenyArgument())
                                .executes(context -> doCommand(context.getSource(),"allow/deny","deny",context.getArgument("deny",String.class)))))
                .then(HelpfulLiteralBuilder.literal("end")
                        .withHelpText("Ends a game.")
                        .withTooltip("Ends the curent game of the player issuing this command.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isManager(sender))
                        .executes(context -> doCommand(context.getSource(), "end",null)))
                .then(HelpfulLiteralBuilder.literal("files")
                        .withHelpText("Lists all saved game data files.")
                        .withTooltip("quiz|race|marker|werewolf: Lists all quiz, race, marker, golf course, pvp match or pvp loadout data files. " +
                                "For quiz, race, golf course, pvp match and pvp loadout data files a description of the saved data will be shown.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER))
                        .then(HelpfulRequiredArgumentBuilder.argument("gametype",new CommandGameTypeArgument())
                                .executes(context -> doCommand(context.getSource(), "files",context.getArgument("gametype",String.class)))))
                .then(HelpfulLiteralBuilder.literal("info")
                        .withHelpText("Displays information about a game.")
                        .withTooltip("Displays the manager and the number of players participating oin a game.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.USER) && PluginData.gameRunning())
                        .then(HelpfulRequiredArgumentBuilder.argument("gamename",new CommandGamenameArgument())
                                .executes(context -> doCommand(context.getSource(), "info",context.getArgument("gamename",String.class)))))
                .then(HelpfulLiteralBuilder.literal("join")
                        .withHelpText("Joins a minigame.")
                        .withTooltip("The player issuing this command joins the game with name <gamename>.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.USER) && PluginData.gameRunning() && !PluginData.isInGame(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("gamename",new CommandGamenameArgument())
                                .executes(context -> doCommand(context.getSource(), "join",context.getArgument("gamename",String.class)))))
                .then(HelpfulLiteralBuilder.literal("kick")
                        .withHelpText("Kicks a player from a game.")
                        .withTooltip("Removes <player> from a game, he may join again.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isManager(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("player",new CommandPlayerArgument())
                                .executes(context -> doCommand(context.getSource(), "kick",context.getArgument("player",String.class)))))
                .then(HelpfulLiteralBuilder.literal("leaderboard")
                        .withHelpText("Leaderboard of game.")
                        .withTooltip("/game leaderboard <gamename> <count> ;Default count is 10")
                        .requires(sender -> PluginData.hasPermission(sender, Permission.USER))
                        .then(HelpfulRequiredArgumentBuilder.argument("gametype",new CommandGameTypeArgument())
                                .executes(context -> doCommand(context.getSource(), "leaderboard",context.getArgument("gametype",String.class)))
                                .then(HelpfulRequiredArgumentBuilder.argument("count",integer())
                                        .executes(context -> doCommand(context.getSource(), "leaderboard",context.getArgument("gametype",String.class), String.valueOf(context.getArgument("count",Integer.class)))))))
                .then(HelpfulLiteralBuilder.literal("leave")
                        .withHelpText("Leaves a minigame.")
                        .withTooltip("The player issuing the command leaves the game he is participating.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.USER) && PluginData.isInGame(sender))
                        .executes(context -> doCommand(context.getSource(), "leave",null)))
                .then(HelpfulLiteralBuilder.literal("manager")
                        .withHelpText("Makes another player manager of a game.")
                        .withTooltip("Makes <player> new manager of the game. <player> must have game manager permission.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isManager(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("manager",new CommandPermmisionArgument())
                                .executes(context -> doCommand(context.getSource(),"manager",context.getArgument("manager",String.class)))))
                .then(HelpfulLiteralBuilder.literal("request")
                        .withHelpText("Submit a request for a game.")
                        .withTooltip("Sends a request to all guides and people with minigame-badge on the server. The gametype is optional and the request can be send without.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.USER) && !PluginData.isInGame(sender))
                        .executes(context -> doCommand(context.getSource(), "request",null))
                        .then(HelpfulRequiredArgumentBuilder.argument("gametype",new CommandGameTypeArgument())
                                .executes(context -> doCommand(context.getSource(), "request",context.getArgument("gametype", String.class)))))
                .then(HelpfulLiteralBuilder.literal("ready")
                        .withHelpText("Announces a game.")
                        .withTooltip("Announces a game which is sending a message to all online players.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isManager(sender) && !PluginData.isAlreadyAnnounced(sender))
                        .executes(context -> doCommand(context.getSource(), "ready",null)))
                .then(HelpfulLiteralBuilder.literal("restart")
                        .withHelpText("Restarts a game.")
                        .withTooltip("Restarts a game which is resetting all scores and marking all questions as not answered.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isManager(sender)
                                && (PluginData.isCorrectGameType(sender,GameType.LORE_QUIZ) || PluginData.isCorrectGameType(sender,GameType.GEO_GUESSR)))
                        .executes(context -> doCommand(context.getSource(), "restart",null)))
                .then(HelpfulLiteralBuilder.literal("spectate")
                        .withHelpText("Spectate at a game.")
                        .withTooltip("Displays the game's scoreboard to a player spectating the game. Use optional argument '!off' to stop spectating.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.USER) && PluginData.gameRunning() && !PluginData.isInGame(sender))
                        .then(HelpfulRequiredArgumentBuilder.argument("gamename",new CommandGamenameArgument())
                                .executes(context -> doCommand(context.getSource(), "spectate",context.getArgument("gamename",String.class)))))
                .then(HelpfulLiteralBuilder.literal("start")
                        .withHelpText("Starts a race or werewolf game.")
                        .withTooltip("Teleports players to start in races and locks the game in werewolf.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isManager(sender)
                                && (PluginData.isCorrectGameType(sender,GameType.RACE) || PluginData.isCorrectGameType(sender,GameType.WEREWOLF)))
                        .executes(context -> doCommand(context.getSource(), "start",null)))
                .then(HelpfulLiteralBuilder.literal("stats")
                        .withHelpText("Gives you your stats")
                        .withTooltip("Gives you your stats, splitted by games and Hide and Seeker")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.USER))
                        .then(HelpfulRequiredArgumentBuilder.argument("gametype",new CommandGameTypeArgument())
                            .executes(context -> doCommand(context.getSource(),"stats",context.getArgument("gametype",String.class)))))
                .then(HelpfulLiteralBuilder.literal("warp")
                        .withHelpText("Teleports to a minigame.")
                        .withTooltip("Teleports you to the location of minigame 'name'.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.USER) && PluginData.gameRunning())
                        .then(HelpfulRequiredArgumentBuilder.argument("gamename",new CommandGamenameArgument())
                                .executes(context -> doCommand(context.getSource(), "warp",context.getArgument("gamename",String.class)))))
                .then(HelpfulLiteralBuilder.literal("winner")
                        .withHelpText("Announces the winner.")
                        .withTooltip("Announces the winner in GeoGuessr Games and Quiz Games. Has to be used if there are two or more winner.")
                        .requires(sender -> PluginData.hasPermission(sender,Permission.MANAGER) && PluginData.isManager(sender)
                                && (PluginData.isCorrectGameType(sender,GameType.LORE_QUIZ) || PluginData.isCorrectGameType(sender,GameType.GEO_GUESSR)))
                        .executes(context -> doCommand(context.getSource(), "winner",null)))

                .then(HelpfulLiteralBuilder.literal("test")
                        .executes(context -> doCommand(context.getSource(), "test",null)));

        GeoGameCommandHandler geoHandler = new GeoGameCommandHandler();
        helpfulLiteralBuilder = geoHandler.createCommandTree(helpfulLiteralBuilder);

        HideGameCommandHandler hideHandler = new HideGameCommandHandler();
        helpfulLiteralBuilder = hideHandler.createCommandTree(helpfulLiteralBuilder);

        ManhuntGameCommandHandler manhuntHandler = new ManhuntGameCommandHandler();
        helpfulLiteralBuilder = manhuntHandler.createCommandTree(helpfulLiteralBuilder);

        QuizGameCommandHandler quizHandler = new QuizGameCommandHandler();
        helpfulLiteralBuilder = quizHandler.createCommandTree(helpfulLiteralBuilder);

        RaceGameCommandHandler raceHandler = new RaceGameCommandHandler();
        helpfulLiteralBuilder = raceHandler.createCommandTree(helpfulLiteralBuilder);

        WerewolfGameCommandHandler werewolfHandler = new WerewolfGameCommandHandler();
        helpfulLiteralBuilder = werewolfHandler.createCommandTree(helpfulLiteralBuilder);

        return helpfulLiteralBuilder;
    }

    private int doCommand(McmeCommandSender sender, String command, String... args){
        AbstractGame game;
        switch (command){
            case "test":
                ProxiedPlayer player = (ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender();
                PluginData.getMessageUtil().sendInfoMessage(player,player.getUUID());
                break;

            case "allow/deny":
                sendNotImplementedYetMessage(sender);
                break;
            case "check":
                sendNotImplementedYetMessage(sender);
                break;
            case "create":
                GameType type = GameType.getGameType(args[0]);
                switch (type){
                    case GEO_GUESSR:
                        sendNotImplementedYetMessage(sender);
                        break;
                    case HIDE_AND_SEEK:
                        sendNotImplementedYetMessage(sender);
                        break;
                    case LORE_QUIZ:
                        game = new QuizGame((ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender(),args[1]);
                        PluginData.addGame(game);
                        PluginData.getMessageUtil().sendInfoMessage(sender,"The quiz game was created.");
                        break;
                    case MANHUNT:
                        sendNotImplementedYetMessage(sender);
                        break;
                    case RACE:
                        sendNotImplementedYetMessage(sender);
                        break;
                    case WEREWOLF:
                        sendNotImplementedYetMessage(sender);
                        break;
                }
                break;
            case "delete":
                sendNotImplementedYetMessage(sender);
                break;
            case "end":
                game = PluginData.getGame(sender);
                game.endGame();
                break;
            case "files":
                sendNotImplementedYetMessage(sender);
                break;
            case "info":
                sendNotImplementedYetMessage(sender);
                break;
            case "join":
                game = PluginData.getGame(args[0]);
                game.addPlayer((ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender());
                break;
            case "kick":
                sendNotImplementedYetMessage(sender);
                break;
            case "leaderboard":
                sendNotImplementedYetMessage(sender);
                break;
            case "leave":
                game = PluginData.getGame(sender);
                game.removePlayer((ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender());
                break;
            case "manager":
                sendNotImplementedYetMessage(sender);
                break;
            case "ready":
                game = PluginData.getGame(sender);
                if(game instanceof QuizGame){
                    QuizGame quizGame = (QuizGame) game;
                    quizGame.announceGame();
                    quizGame.addPlayer((ProxiedPlayer) ((MinigameCommandSender) sender).getCommandSender());
                }
                break;
            case "restart":
                sendNotImplementedYetMessage(sender);
                break;
            case "spectate":
                sendNotImplementedYetMessage(sender);
                break;
            case "start":
                sendNotImplementedYetMessage(sender);
                break;
            case "stats":
                sendNotImplementedYetMessage(sender);
                break;
            case "warp":
                sendNotImplementedYetMessage(sender);
                break;
            case "winner":
                sendNotImplementedYetMessage(sender);
                break;
            default:
                sendNothereMessage(sender);
                break;
        }
        return 0;
    }

    private void sendNothereMessage(McmeCommandSender sender){
        PluginData.getMessageUtil().sendErrorMessage(sender,"You shouldn't be here. Try Again!");
    }

    private void sendNotImplementedYetMessage(McmeCommandSender sender){
        PluginData.getMessageUtil().sendErrorMessage(sender,"This is not yet implemented.");
    }
}
