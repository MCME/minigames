package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.scoreboard.WerewolfGameScoreboard;
import com.mcmiddleearth.minigames.werewolf.WerewolfRoles;
import com.mcmiddleearth.pluginutil.DynmapUtil;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.units.qual.A;

import javax.management.relation.Role;
import java.util.*;

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

    private boolean throwable = true;

    private  List<OfflinePlayer> eliminated = new ArrayList<>();
    private  List<OfflinePlayer> alive = new ArrayList<>();

    private List<Player> voted = new ArrayList<>();

    private WerewolfRoles roles;

    private Integer heads;

    private ItemStack headCount;

    private final List<String> categoryList = Arrays.asList("Wolf","Village","Other");

    private List<String> rolesByCat = new ArrayList();

    //Map<ItemStack,Integer> Slot = new HashMap<>();
    Map<String,Integer> RoleCount = new HashMap<>();
    Map<Player,String> assignedRole = new HashMap<>();

    private final String invName = "Configuration";

    private final Integer invSize = 54;

    private final Material confirmButton = Material.SLIME_BALL;
    private final Material backButton = Material.MAGMA_CREAM;
    private final Material lastPageButton = Material.NAME_TAG;
    private final Material nextPageButton = Material.NAME_TAG;
    private final Material RoleCountZeroButton = Material.BOOK;
    private final Material RoleCountButton = Material.WRITTEN_BOOK;
    private final Material categoryButton = Material.PAPER;
    private final Material playerCountButton = Material.PLAYER_HEAD;
    private final Material playerCountZeroButton = Material.SKELETON_SKULL;

    public WerewolfGame(Player manager, String name){
        super(manager,name,GameType.WEREWOLF,new WerewolfGameScoreboard());
        this.manager = manager;
        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());

        setTeleportAllowed(false);
        setFlightAllowed(true);
        setGm2Forced(false);
        setCollision(true);
        announceGame();

        /*
        alive.add((OfflinePlayer) Bukkit.getPlayer(UUID.fromString("b8d1ce5c-2b38-428c-9bb8-c8ee6ad58c4b")));
        alive.add((OfflinePlayer) Bukkit.getPlayer(UUID.fromString("975c816e-ebb3-4cd5-bec1-394e0976b6f7")));
        alive.add((OfflinePlayer) Bukkit.getPlayer(UUID.fromString("08ded767-ace9-421c-9776-b37c28bf2020")));
        alive.add((OfflinePlayer) Bukkit.getPlayer(UUID.fromString("5614724c-33f2-464c-9e9f-1c593b416bd8")));

         */

        this.roles = new WerewolfRoles();
        configSetup();

        BossBar bar = Bukkit.createBossBar(ChatColor.YELLOW+"Werewolf", BarColor.WHITE, BarStyle.SOLID);
        bar.setProgress(1.0);
        bar.setVisible(true);
        this.bar = bar;
        //Bukkit.getPlayer("Jubo").sendMessage(String.valueOf(alive));
    }

    public void start(){
        ((WerewolfGameScoreboard)this.getBoard()).start();
        //this.started = true;
        //assignPlayerToRole();
        givePlayerBook();
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

    public void Revive(Player player){
        eliminated.remove((OfflinePlayer) player);
        addPlayer(player);
        sendPlayerRevived(manager,player);
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

    private void assignPlayerToRole() {
        for (OfflinePlayer player : alive) {
            assignedRole.put((Player) player, null);
        }
        int i = 0;
        for (String roleName : RoleCount.keySet()) {
            if (RoleCount.get(roleName) != 0) {
                for (int j = 0; j < RoleCount.get(roleName); j++) {
                    do {
                        Random generator = new Random();
                        i = generator.nextInt(alive.size());
                        if (assignedRole.get((Player) alive.get(i)) == null) {
                            assignedRole.replace((Player) alive.get(i), roleName);
                            break;
                        }
                    } while (true);
                }
            }
        }
    }


    private void givePlayerBook(){
        Map<String,Object> role = roles.getRoles();
        for(Player player: assignedRole.keySet()){
            ItemStack roleBook = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta roleBookMeta = (BookMeta) roleBook.getItemMeta();
            roleBookMeta.setTitle(assignedRole.get(player));
            roleBookMeta.addPage(String.valueOf(role.get(assignedRole.get(player))).replaceAll("<br>", "\n"));
            roleBookMeta.setAuthor("Minigame");
            roleBook.setItemMeta(roleBookMeta);
            player.getInventory().addItem(roleBook);

            ItemStack willBook = new ItemStack(Material.WRITABLE_BOOK);
            ItemMeta willBookMeta = willBook.getItemMeta();
            willBookMeta.setDisplayName("Will");
            willBook.setItemMeta(willBookMeta);
            player.getInventory().addItem(willBook);
        }
    }


    private void configSetup(){
        Map<String,Object> role = new HashMap<>();
        role = roles.getRoles();
        for(String roleName: role.keySet()){
            RoleCount.put(roleName,0);
        }
        //heads = getOnlinePlayers().size();
        //
        //Bukkit.getPlayer("Jubo").sendMessage(String.valueOf(alive.size()));
        heads = alive.size();
        ItemStack headCount = new ItemStack(playerCountButton);
        ItemMeta headMeta = headCount.getItemMeta();
        headMeta.setDisplayName("Players without roles");
        headCount.setItemMeta(headMeta);
        this.headCount = headCount;
        //
    }


     public void Configuration(Player player){
        this.started = true;
        openGUI(player);
     }


    private void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, invSize, invName);

        ItemStack confirm = new ItemStack(confirmButton);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("confirm");
        confirm.setItemMeta(confirmMeta);
        inv.setItem(50,confirm);

        headCount.setAmount(heads);
        inv.setItem(49,headCount);

        if(heads <= 0){
            ItemStack Skeleton = new ItemStack(playerCountZeroButton);
            ItemMeta headMeta = Skeleton.getItemMeta();
            headMeta.setDisplayName("Players without roles");
            Skeleton.setItemMeta(headMeta);
            inv.setItem(49,Skeleton);
        }

        int i = 20;
        for(String category: categoryList){
            ItemStack book = new ItemStack(categoryButton);
            ItemMeta meta = book.getItemMeta();
            meta.setDisplayName(category);
            book.setItemMeta(meta);
            inv.setItem(i, book);
            i = i + 2;
        }
        //Bukkit.getPlayer("Jubo").sendMessage(Slot.toString());
        player.openInventory(inv);
    }

    private void openGUI_Category(Player player,String category){
        List<String> rolesByCat = roles.getbyCategorySorted(category);
        //player.sendMessage(String.valueOf(rolesByCat.size()));
        this.rolesByCat = rolesByCat;
        openGUI_Category_FirstPage(player);
        //Bukkit.getPlayer("Jubo").sendMessage(String.valueOf(rolesByCat.size()));
    }

    private void openGUI_Category_FirstPage(Player player){
        Inventory inv = Bukkit.createInventory(null,invSize,invName);

        //player.sendMessage("Test");
        //Map<String,Object> role = new HashMap<>();
        //role = roles.getRoles();
        //Bukkit.getPlayer("Jubo").sendMessage(String.valueOf(rolesByCat));



        int i = 0;
        for(String roleName: rolesByCat) {
            if (RoleCount.get(roleName) == 0) {
                ItemStack book = new ItemStack(RoleCountZeroButton);
                ItemMeta meta = book.getItemMeta();
                meta.setDisplayName(roleName);
                book.setItemMeta(meta);
                inv.setItem(i, book);
            } else if (RoleCount.get(roleName) == 1) {
                ItemStack book = new ItemStack(RoleCountButton);
                ItemMeta meta = book.getItemMeta();
                meta.setDisplayName(roleName);
                book.setItemMeta(meta);
                inv.setItem(i, book);
            } else if (RoleCount.get(roleName) > 1) {
                ItemStack book = new ItemStack(RoleCountButton, RoleCount.get(roleName));
                ItemMeta meta = book.getItemMeta();
                meta.setDisplayName(roleName);
                book.setItemMeta(meta);
                inv.setItem(i, book);
            }
            //i = i + 2;
            i++;
            if(i == 45) break;
            //Bukkit.getPlayer("Jubo").sendMessage(roleName);
        }


        if(rolesByCat.size() > 45) {
            ItemStack nextPage = new ItemStack(nextPageButton);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.setDisplayName("next Page");
            nextPage.setItemMeta(nextPageMeta);
            inv.setItem(53, nextPage);
        }

        ItemStack confirm = new ItemStack(confirmButton);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("confirm");
        confirm.setItemMeta(confirmMeta);
        inv.setItem(50,confirm);

        ItemStack back = new ItemStack(backButton);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("back");
        back.setItemMeta(backMeta);
        inv.setItem(48,back);

        if(heads <= 0){
            ItemStack Skeleton = new ItemStack(playerCountZeroButton);
            ItemMeta headMeta = Skeleton.getItemMeta();
            headMeta.setDisplayName("Players without roles");
            Skeleton.setItemMeta(headMeta);
            inv.setItem(49,Skeleton);
        }else{
            headCount.setAmount(heads);
            inv.setItem(49,headCount);
        }



        player.openInventory(inv);
    }

    private void openGUI_category_SecondPage(Player player){
        Inventory inv = Bukkit.createInventory(null,invSize,invName);

        //player.sendMessage(String.valueOf(rolesByCat.size()));
        List<String> rolesByCat_2 = new ArrayList<>();
        for(int j = 45; j < rolesByCat.size(); j++){
            rolesByCat_2.add(rolesByCat.get(j));
        }
        //player.sendMessage(String.valueOf(rolesByCat));
        //if(true) return;

        int i = 0;
        for(String roleName: rolesByCat_2) {
            if (RoleCount.get(roleName) == 0) {
                ItemStack book = new ItemStack(RoleCountZeroButton);
                ItemMeta meta = book.getItemMeta();
                meta.setDisplayName(roleName);
                book.setItemMeta(meta);
                inv.setItem(i, book);
            } else if (RoleCount.get(roleName) == 1) {
                ItemStack book = new ItemStack(RoleCountButton);
                ItemMeta meta = book.getItemMeta();
                meta.setDisplayName(roleName);
                book.setItemMeta(meta);
                inv.setItem(i, book);
            } else if (RoleCount.get(roleName) > 1) {
                ItemStack book = new ItemStack(RoleCountButton, RoleCount.get(roleName));
                ItemMeta meta = book.getItemMeta();
                meta.setDisplayName(roleName);
                book.setItemMeta(meta);
                inv.setItem(i, book);
            }
            //i = i + 2;
            i++;
            if(i >= 45) break;
            //Bukkit.getPlayer("Jubo").sendMessage(roleName);
        }

        ItemStack lastPage = new ItemStack(lastPageButton);
        ItemMeta lastPageMeta = lastPage.getItemMeta();
        lastPageMeta.setDisplayName("last Page");
        lastPage.setItemMeta(lastPageMeta);
        inv.setItem(45, lastPage);

        ItemStack confirm = new ItemStack(confirmButton);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("confirm");
        confirm.setItemMeta(confirmMeta);
        inv.setItem(50,confirm);

        ItemStack back = new ItemStack(backButton);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("back");
        back.setItemMeta(backMeta);
        inv.setItem(48,back);

        if(heads <= 0){
            ItemStack Skeleton = new ItemStack(playerCountZeroButton);
            ItemMeta headMeta = Skeleton.getItemMeta();
            headMeta.setDisplayName("Players without roles");
            Skeleton.setItemMeta(headMeta);
            inv.setItem(49,Skeleton);
        }else{
            headCount.setAmount(heads);
            inv.setItem(49,headCount);
        }

        player.openInventory(inv);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if(event.getView().getTitle() != invName) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        Inventory currentInv = event.getInventory();
        ClickType click = event.getClick();
        ItemStack currentHead = currentInv.getItem(49);
        if(currentHead == null) return;

        if (current == null) return;
        String itemName = current.getItemMeta().getDisplayName();
        event.setCancelled(true);




        if(categoryList.contains(current.getItemMeta().getDisplayName()) && current.getType() == categoryButton && click == ClickType.LEFT){
            //Bukkit.getPlayer("Jubo").sendMessage("Test");
            openGUI_Category(player,current.getItemMeta().getDisplayName());
        }

        if (current.getType() == RoleCountZeroButton && click == ClickType.LEFT) {
            current.setType(RoleCountButton);
            RoleCount.replace(itemName, RoleCount.get(itemName)+1);
            if(currentHead.getType() == playerCountZeroButton){
                heads--;
            }else if(currentHead.getType() == playerCountButton) currentHead.setAmount(--heads);
        } else if (current.getType() == RoleCountButton && click == ClickType.LEFT) {
            current.setAmount(current.getAmount() + 1);
            RoleCount.replace(itemName, RoleCount.get(itemName) + 1);
            if(currentHead.getType() == playerCountZeroButton){
                heads--;
            }else if(currentHead.getType() == playerCountButton) currentHead.setAmount(--heads);
        } else if (current.getType() == RoleCountButton && click == ClickType.RIGHT) {
            if(current.getAmount() > 1){
                current.setAmount(current.getAmount()-1);
                RoleCount.replace(itemName, RoleCount.get(itemName)-1);
            }else{
                current.setType(RoleCountZeroButton);
                RoleCount.replace(itemName, RoleCount.get(itemName)-1);
            }
            if(currentHead.getType() == playerCountZeroButton){
                heads++;
            }else if(currentHead.getType() == playerCountButton) currentHead.setAmount(++heads);
        } else if(current.getType() == confirmButton){
            player.closeInventory();
            /*
            for(String roleName: RoleCount.keySet()){
                if(RoleCount.get(roleName) != 0){
                    Bukkit.getPlayer("Jubo").sendMessage(roleName+"="+String.valueOf(RoleCount.get(roleName)));
                }
            }
             */
            assignPlayerToRole();
            sendAssignmentConfirmed(player);
            /*
            for(String name: role.keySet()){
                ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta bookMeta = (BookMeta) book.getItemMeta();
                bookMeta.setTitle(name);
                bookMeta.addPage(String.valueOf(role.get(name)).replaceAll("<br>", "\n"));
                bookMeta.setAuthor("Stoog_Gaming");
                book.setItemMeta(bookMeta);
                Bukkit.getPlayer("Jubo").getInventory().addItem(book);
                Bukkit.getPlayer("Jubo").sendMessage(name);
            }
             */
        }else if(current.getType() == backButton){
            //Bukkit.getPlayer("Jubo").sendMessage("Test");
            openGUI(player);
        }else if(current.getType() == nextPageButton && "next Page".equals(current.getItemMeta().getDisplayName())){
            //Bukkit.getPlayer("Jubo").sendMessage("Test1");
            openGUI_category_SecondPage(player);
        }else if(current.getType() == lastPageButton && "last Page".equals(current.getItemMeta().getDisplayName())){
            //Bukkit.getPlayer("Jubo").sendMessage("Test2");
            openGUI_Category_FirstPage(player);
        }
        /*
        else if(current.getType() == Material.NAME_TAG){ //&& current.getItemMeta().getDisplayName().equalsIgnoreCase("next Page")){
            player.sendMessage("Test");
            //openGUI_Category(player);
        }

         */
        if(heads == 0){
            ItemStack Skeleton = new ItemStack(Material.SKELETON_SKULL);
            ItemMeta headMeta = Skeleton.getItemMeta();
            headMeta.setDisplayName("Players without roles");
            Skeleton.setItemMeta(headMeta);
            currentInv.setItem(49,Skeleton);
        }else if(heads > 0 && currentHead.getType() == Material.SKELETON_SKULL){
            currentHead.setType(Material.PLAYER_HEAD);
        }
    }



    @Override
    public void checkThrow(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Material material = event.getMaterial();
        if((material == Material.EGG || material == Material.SNOWBALL || material == Material.FIREWORK_ROCKET) && !throwable){
            player.getInventory().remove(material);
            sendYouCantDoThisMessage(player);
            event.setCancelled(true);
        }else event.setCancelled(false);
    }





    public void switchThrowable(boolean bool){
        this.throwable = bool;
    }


    @Override
    public void addPlayer(Player player){
        super.addPlayer(player);
        bar.addPlayer(player);
        forceTeleport(player,getWarp());
        player.setSilent(true);
        ((WerewolfGameScoreboard)getBoard()).addPlayer(player.getName());
        if(!((Player)player == getManager().getPlayer())){
            player.setGameMode(GameMode.ADVENTURE);
            alive.add((Player)player);
            heads++;
        }
        DynmapUtil.hide(player);
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
        alive.remove(player);
        eliminated.remove(player);
        heads--;
        if(player.isOnline()){
            Player player_on = player.getPlayer();
            bar.removePlayer(player_on);
            player_on.setSilent(false);
            player_on.removePotionEffect(PotionEffectType.INVISIBILITY);
            DynmapUtil.show(player_on);
        }
    }

    public void sendAliveList(CommandSender cs){
        List<String> aliveString = new ArrayList<>();
        for(OfflinePlayer p_alive : this.alive){
            aliveString.add(p_alive.getName());
        }
        PluginData.getMessageUtil().sendInfoMessage(cs,ChatColor.GREEN+"Alive players:");
        PluginData.getMessageUtil().sendInfoMessage(cs,ChatColor.WHITE+"["+alive.size()+"]: "+aliveString.toString());
    }

    public void sendEliminatedList(CommandSender cs){
        List<String> eliminatedString = new ArrayList<>();
        for(OfflinePlayer p_eliminated : this.eliminated){
            eliminatedString.add(p_eliminated.getName());
        }
        PluginData.getMessageUtil().sendInfoMessage(cs,ChatColor.RED+"Eliminated players:");
        PluginData.getMessageUtil().sendInfoMessage(cs,ChatColor.WHITE+"["+eliminated.size()+"]: "+eliminatedString.toString());
    }

    public void sendRoleList(CommandSender cs){
        if((Player)cs == manager){
            PluginData.getMessageUtil().sendInfoMessage(cs,ChatColor.WHITE+"Assigned Roles:");
            PluginData.getMessageUtil().sendInfoMessage(cs,ChatColor.WHITE+String.valueOf(assignedRole));

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

    private void sendPlayerRevived(CommandSender cs,Player player){
        PluginData.getMessageUtil().sendInfoMessage(cs,"You revived "+player.getName()+".");
    }

    private void sendAssignmentConfirmed(CommandSender cs){
        PluginData.getMessageUtil().sendInfoMessage(cs,"The players were assigned a role. Type /game start to start the game and give them their role-book.");
    }
}
