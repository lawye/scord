package io.github.lawye.scord;

import java.io.*;
import java.util.*;


import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

public final class Scord extends JavaPlugin implements Listener{
    public final String DataURL = getDataFolder() + File.separator + "data.db";
    public final String PlayerSettingURL = getDataFolder()+File.separator+"player_setting.db";
    public HashMap<String,Integer> Data = new HashMap<String, Integer>();
    public HashMap<String,Integer> PlayerSetting = new HashMap<String, Integer>();
    /*
    public List<String> list=new List<String>() {
        public int size() {
            return 0;
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean contains(Object o) {
            return false;
        }

        public Iterator<String> iterator() {
            return null;
        }

        public Object[] toArray() {
            return new Object[0];
        }

        public <T> T[] toArray(T[] ts) {
            return null;
        }

        public boolean add(String s) {
            return false;
        }

        public boolean remove(Object o) {
            return false;
        }

        public boolean containsAll(Collection<?> collection) {
            return false;
        }

        public boolean addAll(Collection<? extends String> collection) {
            return false;
        }

        public boolean addAll(int i, Collection<? extends String> collection) {
            return false;
        }

        public boolean removeAll(Collection<?> collection) {
            return false;
        }

        public boolean retainAll(Collection<?> collection) {
            return false;
        }

        public void clear() {

        }

        public String get(int i) {
            return null;
        }

        public String set(int i, String s) {
            return null;
        }

        public void add(int i, String s) {

        }

        public String remove(int i) {
            return null;
        }

        public int indexOf(Object o) {
            return 0;
        }

        public int lastIndexOf(Object o) {
            return 0;
        }

        public ListIterator<String> listIterator() {
            return null;
        }

        public ListIterator<String> listIterator(int i) {
            return null;
        }

        public List<String> subList(int i, int i1) {
            return null;
        }
    };
*/
    @Override
    public void onEnable(){
        getLogger().info("Scord start initialize");
        getServer().getPluginManager().registerEvents(this,this);
        File data_db = new File(DataURL);
        File folder=getDataFolder();
        if(!folder.exists() && !folder.isDirectory()){
            folder.mkdirs();
            //getLogger().info("dir made");
        }
        if (data_db.exists()){
            Data = loadDB(DataURL);
            //getLogger().info("read Data");
        }else{
            try {
                if(data_db.createNewFile()) getLogger().info("new DataBase");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File setting_db = new File(PlayerSettingURL);
        if (setting_db.exists()){
            PlayerSetting = loadDB(PlayerSettingURL);
            //getLogger().info("setting read");
        }else{
            try {
                setting_db.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //getLogger().info("read all, adding him");
        //Data.put("Herobine",-1);
        //PlayerSetting.put("Herobine",0);
        //getLogger().info("him is here");
        update_board(first());
        getLogger().info("plugin loaded");
    }
    @Override
    public void onDisable(){
        getLogger().info("Scord shutdown, backing up");
        saveDB(Data,DataURL);
        saveDB(PlayerSetting,PlayerSettingURL);
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard empty = manager.getNewScoreboard();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(empty);
        }
        getLogger().info("Scord down");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof Player){
            Player player = (Player)sender;
            if(cmd.getName().equalsIgnoreCase("scord")){
                if(args.length>1){
                    sender.sendMessage("too many arguments!");
                }else if(args.length==0) {
                    sender.sendMessage("/scord on/off");
                }else{
                    if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true") || args[0].equals("1")){
                        PlayerSetting.put(player.getName(),1);
                        update_board(first());
                        return true;
                    }else if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false") || args[0].equals("0")){
                        PlayerSetting.put(player.getName(),0);
                        update_board(first());
                        return true;
                    }else{
                        sender.sendMessage("Wrong Argument! try /help <command>");
                        return false;
                    }
                }
                return false;
            }
        }else{
            sender.sendMessage("You must be a Player");
            return false;
        }
        return false;
    }

    public HashMap<String,Integer> loadDB(String url){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(url));
            Object result = ois.readObject();
            return (HashMap<String,Integer>)result;
        }catch(Exception err){
            err.printStackTrace();
            return null;
        }
    }
    public void saveDB(HashMap<String,Integer> map, String URL){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(URL));
            oos.writeObject(map);
            oos.flush();
            oos.close();
        }catch (Exception err){
            err.printStackTrace();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Material tool=event.getPlayer().getInventory().getItemInMainHand().getType();
        if(tool==Material.WOODEN_PICKAXE || tool==Material.STONE_PICKAXE || tool==Material.IRON_PICKAXE || tool==Material.GOLDEN_PICKAXE || tool==Material.DIAMOND_PICKAXE || tool==Material.NETHERITE_PICKAXE){
            Data.put(event.getPlayer().getName(),Data.get(event.getPlayer().getName())+1);
            saveDB(Data,DataURL);
            update_board(first());
            //tab_board();
        }
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event){
        if(!Data.containsKey(event.getPlayer().getName())){
            Data.put(event.getPlayer().getName(),0);
            PlayerSetting.put(event.getPlayer().getName(),1);
        }

        update_board(first());
        //tab_board();
    }

/*
    public void set_board(Player player){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective obj = scoreboard.registerNewObjective("Servername", "dummy", "Test Server");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
*/

    /*
    public void tab_board(){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        for(Player player : Bukkit.getOnlinePlayers()){
            Objective obj = scoreboard.registerNewObjective("Scordtab","dummy","tabtable");
            obj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            obj.getScore(player.getName()).setScore(Data.get(player.getName()));
            player.setScoreboard(scoreboard);
        }
    }

     */

    public void update_board(List<String> leader){
        ScoreboardManager manager=Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard scoreboard = manager.getNewScoreboard();
        Scoreboard empty = manager.getNewScoreboard();
        for (Player player : Bukkit.getOnlinePlayers()) {
            //Scoreboard scoreboard = player.getScoreboard();
            //if(true){
            if (PlayerSetting.get(player.getName()) == 1) {
                //getLogger().info("开始启动"+player.getName()+"的榜");
                Objective obj = scoreboard.registerNewObjective("scord", "dummy", ChatColor.AQUA + "挖掘榜", RenderType.HEARTS);
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                if(leader.size()<1){
                    //getLogger().info("没人可以排");
                    return;
                }else {
                    int n = leader.size();
                    int i =1;
                    for(String name:leader){
                        obj.getScore("§fNo." + i + ": §r" + name + " §e" + Data.get(name)).setScore(n - i+1);
                        //getLogger().info("初始化"+i);
                        i++;
                    }
                    obj.getScore("§f<" + player.getName() + ">: §6" + Data.get(player.getName())).setScore(0);
                    //getLogger().info(player.getName()+" score "+ Data.get(player.getName())+" settings "+ PlayerSetting.get(player.getName()));
                    //getLogger().info("现在应该ok");
                    player.setScoreboard(scoreboard);
                }
                /*
                if (leader.size() >= 5) {
                    for (int i = 0; i < 5; i++) {
                        obj.getScore("§fNo." + i + ": §b" + leader.get(i) + " §e" + Data.get(leader.get(i))).setScore(5 - i);
                    }
                } else if (leader.size() < 1) {
                    return;
                } else {
                    for (int i = 0; i < leader.size(); i++) {
                        obj.getScore("§fNo." + i + ": §b" + leader.get(i) + " §e" + Data.get(leader.get(i))).setScore(5 - i);
                    }
                }
                obj.getScore("§fme<" + nw.getName() + ">: §6" + Data.get(nw.getName())).setScore(0);
                nw.setScoreboard(scoreboard);

                 */
            } else {
                player.setScoreboard(empty);
            }
        }
    }
/*
    public void show_board(List<String> leader, Player player){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if(PlayerSetting.get(player.getName())==1){
            Scoreboard scoreboard = manager.getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("scord", "dummy","挖掘榜");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.getScore("§fme<" + player.getName() +">: §6"+ Data.get(player.getName())).setScore(0);
            for(int i=0;i<5;i++){
                //objective.getScore("§fNo." + i + ": §b" + leader.get(i) + " §e" + Data.get(leader.get(i))).setScore(5-i);
                objective.getScore("§fNo." + i + ":" + ChatColor.GOLD + leader.get(i)).setScore(Data.get(leader.get(i)));
            }
            player.setScoreboard(scoreboard);
        }
    }
*/
    public List<String> first(){
        List<String> list= Lists.newArrayList();
        Map<String,Integer> map = Data;
        for(Map.Entry<String, Integer> entry : map.entrySet()){
            if(list.isEmpty()){
                list.add(entry.getKey());
            }else if(list.size()<5){
                boolean flag=true;
                for(int i=0;i<list.size();i++){
                    if(Data.get(list.get(i))<entry.getValue()){
                        list.add(i,entry.getKey());
                        flag=false;
                        break;
                    }
                }
                if(flag){
                    list.add(entry.getKey());
                }
            }else {
                for(int i=0;i<5;i++){
                    if(Data.get(list.get(i))<entry.getValue()){
                        list.add(i,entry.getKey());
                        list.remove(5);
                        break;
                    }
                }
            }
        }
        //list.add("M16A1");
        return list;
    }

}

