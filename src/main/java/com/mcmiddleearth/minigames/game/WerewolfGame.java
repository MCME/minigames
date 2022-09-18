package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.scoreboard.WerewolfGameScoreboard;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jubo
 */
public class WerewolfGame extends AbstractGame implements Listener {

    private BossBar bar;

    private Player votee;
    private final Player manager;

    private boolean upForVote = false;

    private boolean started = false;

    private  List<Player> eliminated = new ArrayList<>();
    private  List<Player> alive = new ArrayList<>();

    private List<Player> voted = new ArrayList<>();

    public WerewolfGame(Player manager, String name){
        super(manager,name,GameType.WEREWOLF,new WerewolfGameScoreboard());
        this.manager = manager;
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());

        setTeleportAllowed(false);
        setFlightAllowed(true);
        setGm2Forced(false);
        setCollision(true);
        announceGame();

        BossBar bar = Bukkit.createBossBar(ChatColor.YELLOW+"Werewolf", BarColor.WHITE, BarStyle.SOLID);
        bar.setProgress(1.0);
        bar.setVisible(true);
        this.bar = bar;
    }

    public void start(){
        ((WerewolfGameScoreboard)this.getBoard()).start();
        this.started = true;
        sendGameStartMessage();
    }

    public void eliminate(Player player){
        if(player == votee){
            ((WerewolfGameScoreboard)getBoard()).reset();
            voted.clear();
            upForVote = false;
        }
        player.getInventory().setHelmet(new ItemStack(Material.SKELETON_SKULL));
        player.setSilent(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        eliminated.add(player);
        alive.remove(player);
        sendPlayerEliminated(player);
    }

    public void pardon(Player player){
        ((WerewolfGameScoreboard)getBoard()).reset();
        voted.clear();
        upForVote = false;
        votee = null;
        sendPlayerPardoned(player);
    }


    public void putUpVote(Player player){
        votee=player;
        upForVote = true;
        ((WerewolfGameScoreboard)getBoard()).putUpForVote(player.getName());
        voted.clear();
        sendPutUpForVoteMessage(player);

    }

    public void suggest(CommandSender cs, Player player){
        if(!(eliminated.contains((Player)cs))) {
            if ((Player) cs == manager) {
                putUpVote(player);
            } else {
                if (manager != player) {
                    if (eliminated.contains(player)) {
                        sendAlreadyEliminatedMessage(cs);
                    } else {
                        if (!voted.contains(((Player) cs))) {
                            ((WerewolfGameScoreboard) getBoard()).suggest(player.getName());
                            voted.add((Player) cs);
                            sendSuggestionMessage(player, cs);
                        } else {
                            sendAlreadyVotedMessage(cs);
                        }
                    }
                } else {
                    sendVoteManager(cs);
                }
            }
        }else{
            sendYouAreDead(cs);
        }
    }

    public void vote(CommandSender cs,boolean bool){
        if(!(eliminated.contains((Player)cs))) {
            if (votee != (Player) cs) {
                if (upForVote) {
                    if (!voted.contains(((Player) cs))) {
                        ((WerewolfGameScoreboard) getBoard()).vote(bool);
                        voted.add((Player) cs);
                        sendVoteMessage(bool, (Player) cs);
                    } else {
                        sendAlreadyVotedMessage(cs);
                    }
                } else {
                    sendYouCantDoThisMessage(cs);
                }
            } else {
                sendVoteYourselfMessage(cs);
            }
        }else{
            sendYouAreDead(cs);
        }
    }


    public void sendAliveList(CommandSender cs){
        List<String> aliveString = new ArrayList<>();
        for(Player p_alive : this.alive){
            aliveString.add(p_alive.getName());
        }
        PluginData.getMessageUtil().sendInfoMessage(cs,ChatColor.GREEN+"Alive players:");
        PluginData.getMessageUtil().sendInfoMessage(cs,ChatColor.WHITE+aliveString.toString());
    }

    public void sendEliminatedList(CommandSender cs){
        List<String> eliminatedString = new ArrayList<>();
        for(Player p_eliminated : this.eliminated){
            eliminatedString.add(p_eliminated.getName());
        }
        PluginData.getMessageUtil().sendInfoMessage(cs,ChatColor.RED+"Eliminated players:");
        PluginData.getMessageUtil().sendInfoMessage(cs,ChatColor.WHITE+eliminatedString.toString());
     }


    @Override
    public void addPlayer(Player player){
        super.addPlayer(player);
        bar.addPlayer(player);
        forceTeleport(player,getWarp());
        player.setSilent(true);
        ((WerewolfGameScoreboard)getBoard()).addPlayer(player.getName());
        if(!(player == getManager().getPlayer())){
            player.setGameMode(GameMode.ADVENTURE);
            alive.add(player);
        }
    }

    @Override
    public void playerMove(PlayerMoveEvent event){
        super.playerMove(event);
        event.getPlayer().stopSound(Sound.BLOCK_STONE_STEP);
    }

    @Override
    public boolean joinAllowed() {
        return super.joinAllowed() && !started;
    }

    @Override
    public int allowedRadius(Player player){
        return 75;
    }

    @Override
    public void end(Player sender){
        super.end(sender);
        for(Player player: getOnlinePlayers()){
            bar.removePlayer(player);
            player.setSilent(false);
        }
    }

    @Override
    public void removePlayer(OfflinePlayer player){
        super.removePlayer(player);
        if(player.isOnline()){
            Player player_on = player.getPlayer();
            bar.removePlayer(player_on);
            player_on.setSilent(false);
            player_on.removePotionEffect(PotionEffectType.INVISIBILITY);
            alive.remove(player_on);
            eliminated.add(player_on);

        }
    }

    private void sendGameStartMessage(){
        for(Player p: getOnlinePlayers()){
            PluginData.getMessageUtil().sendInfoMessage(p,"The game was started.");
        }
    }

    private void sendAlreadyVotedMessage(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"You already voted on this one.");
    }

    private void sendVoteManager(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"You can´t suggest the manager.");
    }

    private void sendAlreadyEliminatedMessage(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"This player was already eliminated.");
    }

    private void sendYouCantDoThisMessage(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"You can´t do this right now.");
    }

    private void sendVoteMessage(boolean bool,Player player){
        if(bool){
            for(Player p: getOnlinePlayers()){
                PluginData.getMessageUtil().sendInfoMessage(p,player.getName()+" voted yay.");
            }
        } else {
            for(Player p: getOnlinePlayers()){
                PluginData.getMessageUtil().sendInfoMessage(p,player.getName()+" voted nay.");
            }
        }
    }

    private void sendPutUpForVoteMessage(Player player){
        for(Player p: getOnlinePlayers()){
            PluginData.getMessageUtil().sendInfoMessage(p,player.getName()+" was put up for voting. You can vote yay to see "+player.getName()+" dead, or nay to pardon them.");
        }
    }

    private void sendSuggestionMessage(Player player,CommandSender cs){
        for(Player p: getOnlinePlayers()){
            PluginData.getMessageUtil().sendInfoMessage(p,player.getName()+ " was suggested by "+cs.getName());
        }
    }

    private void sendVoteYourselfMessage(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"You can´t for for yourself.");
    }

    private void sendYouAreDead(CommandSender cs){
        PluginData.getMessageUtil().sendErrorMessage(cs,"You are dead.");
    }

    private void sendPlayerPardoned(Player player){
        for(Player p: getOnlinePlayers()){
            PluginData.getMessageUtil().sendInfoMessage(p,player.getName()+" was pardoned.");
        }
    }

    private void sendPlayerEliminated(Player player){
        for(Player p:getOnlinePlayers()){
            PluginData.getMessageUtil().sendInfoMessage(p,player.getName()+" was eliminated from the game.");
        }
    }
}
