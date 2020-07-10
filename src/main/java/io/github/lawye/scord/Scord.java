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
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public final class Scord extends JavaPlugin implements Listener{
    public final String DataURL = getDataFolder() + File.separator + "data.db";
    public final String PlayerSettingURL = getDataFolder()+File.separator+"player_setting.db";
    public HashMap<String,Integer> Data = new HashMap<String, Integer>();
    public HashMap<String,Integer> PlayerSetting = new HashMap<String, Integer>();

    @Override
    public void onEnable(){
        getLogger().info("Scord start initialize");
        getServer().getPluginManager().registerEvents(this,this);
        File data_db = new File(DataURL);
        File folder=getDataFolder();
        if(!folder.exists() && !folder.isDirectory()){
            folder.mkdirs();
            getLogger().info("dir made");
        }
        if (data_db.exists()){
            Data = loadDB(DataURL);
            getLogger().info("read Data");
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
            getLogger().info("setting read");
        }else{
            try {
                setting_db.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        getLogger().info("read all, adding him");
        Data.put("Herobine",-1);
        PlayerSetting.put("Herobine",0);
        getLogger().info("him is here");
        update_board(first());
        getLogger().info("done");
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
        if(tool==Material.WOODEN_PICKAXE || tool==Material.STONE_PICKAXE || tool==Material.GOLDEN_PICKAXE || tool==Material.DIAMOND_PICKAXE || tool==Material.NETHERITE_PICKAXE){
            System.out.print("block break");
            Data.put(event.getPlayer().getName(),Data.get(event.getPlayer().getName())+1);
            saveDB(Data,DataURL);
            update_board(first());

        }
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event){
        if(!Data.containsKey(event.getPlayer().getName())){
            Data.put(event.getPlayer().getName(),0);
            PlayerSetting.put(event.getPlayer().getName(),1);
        }
        update_board(first());
    }

    public void update_board(List<String> leader){
        ScoreboardManager manager=Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard scoreboard = manager.getNewScoreboard();
        Scoreboard empty = manager.getNewScoreboard();
        for (Player nw : Bukkit.getOnlinePlayers()) {
            if(true){
            //if (PlayerSetting.get(nw.getName()) == 1) {
                Objective obj = scoreboard.registerNewObjective("scord", "dummy", ChatColor.AQUA + "挖掘榜");
                if(leader.size()<1){
                    getLogger().info("没人可以排");
                    return;
                }else {
                    int n = leader.size();
                    int i =1;
                    for(String name:leader){
                        obj.getScore("§fNo." + i + ": §b" + name + " §e" + Data.get(name)).setScore(n - i);
                        i++;
                    }
                    obj.getScore("§fme<" + nw.getName() + ">: §6" + Data.get(nw.getName())).setScore(0);
                    getLogger().info("现在应该ok");
                    nw.setScoreboard(scoreboard);
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
                nw.setScoreboard(empty);
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
        List<String> list= Lists.newArrayList(Data.keySet().iterator().next());
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

