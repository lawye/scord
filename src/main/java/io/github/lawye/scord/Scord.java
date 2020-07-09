package io.github.lawye.scord;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class Scord extends JavaPlugin implements Listener{
    public final String DataURL = getDataFolder() + File.separator + "data.db";
    public final String PlayerSettingURL = getDataFolder()+File.separator+"player_setting.db";
    public HashMap<String,Integer> Data;
    public HashMap<String,Integer> PlayerSetting;

    @Override
    public void onEnable(){
        getLogger().info("Scord start initialize");
        getServer().getPluginManager().registerEvents(this,this);
        File data_db = new File(DataURL);
        if (data_db.exists()){
            Data = loadDB(DataURL);
        }else{
            try {
                data_db.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File setting_db = new File(PlayerSettingURL);
        if (setting_db.exists()){
            PlayerSetting = loadDB(PlayerSettingURL);
        }else{
            try {
                setting_db.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDisable(){
        getLogger().info("Scord shutdown, backing up");
        saveDB(Data,DataURL);
        saveDB(PlayerSetting,PlayerSettingURL);

    }

    public HashMap<String,Integer> loadDB(String URL){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(URL));
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
        }
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event){
        showboard(first(),event.getPlayer());
    }

    public void showboard(ArrayList<String> leader, Player player){
        if(PlayerSetting.get(player.getName())==1){
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("scord", "dummy","挖掘榜");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.getScore("§fme<" + player.getName() +">: §6"+ Data.get(player.getName())).setScore(0);
            for(int i=0;i<5;i++){
                objective.getScore("§fNo." + i + ": §b" + leader.get(i) + " §e" + Data.get(leader.get(i))).setScore(5-i);
            }
            player.setScoreboard(scoreboard);
        }
    }

    public ArrayList<String> first(){
        ArrayList<String> list=null;
        Map<String,Integer> map = Data;
        for(Map.Entry<String, Integer> entry : map.entrySet()){
            try {
                list.size();
            }catch (NullPointerException e){
                list.add(entry.getKey());
                break;
            }
            if(list.size()<5){
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
        return list;
    }
}
