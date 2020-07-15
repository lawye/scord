package io.github.lawye.scord;

import java.io.*;
import java.util.*;
import java.util.logging.Level;


import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
    //public final String ConfigURL = getDataFolder() + File.separator + "config.yml";

    public HashMap<String,Integer> Data = new HashMap<String, Integer>();
    public HashMap<String,Integer> PlayerSetting = new HashMap<String, Integer>();

    //public int NUM = 5;
    //public FileConfiguration CONFIG;

    private FileConfiguration customConfig = null;
    private File customConfigFile = null;

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
                getLogger().log(Level.SEVERE,"Could not save DataBase to " + DataURL,e);
                //e.printStackTrace();
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
                getLogger().log(Level.SEVERE,"Could not save User_Setting_Data to " + PlayerSettingURL,e);
                //e.printStackTrace();
            }
        }
        //getLogger().info("read all, adding him");
        //Data.put("Herobine",-1);
        //PlayerSetting.put("Herobine",0);
        //getLogger().info("him is here");
        saveDefaultConfig();
        saveCustomConfig();
        //getCommand("scord").setExecutor(new ScordCommandExecutor(this));
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
        String wrargs = "you type in wrong arguments";
        String pmsion = "you don't have permission to access this command";
        String tfargs = "there is too few arguments";
        String tmargs = "there is too many arguments";
        //List<String> yaml=new ArrayList<String>(Arrays.asList("title","numberprefix","maxmiumleaders","nameprefix"));
        if(cmd.getName().equalsIgnoreCase("scord")){
            if(args.length==0){
                sender.sendMessage(tfargs);
                //TODO: 切换成介绍文字
                return false;
            }
            if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("1")){
                if(sender instanceof Player){
                    if(sender.hasPermission("scord.basic")){
                        if(args.length==1){
                            PlayerSetting.put(sender.getName(), 1);
                            update_board(first());
                            sender.sendMessage("your display setting for scord is successfully changed");
                            return true;
                        }else{
                            sender.sendMessage("too many arguments");
                            return false;
                        }    
                    }else{
                        sender.sendMessage(pmsion);
                        return false;
                    }
                }else{
                    sender.sendMessage("you must be a player to run this command");
                    return false;
                }
            }else if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("0")){
                if(sender instanceof Player){
                    if(sender.hasPermission("scord.basic")){
                        if(args.length==1){
                            PlayerSetting.put(sender.getName(), 0);
                            update_board(first());
                            sender.sendMessage("your display setting for scord is successfully changed");
                            return true;
                        }else{
                            sender.sendMessage("too many arguments");
                            return false;
                        }    
                    }else{
                        sender.sendMessage(pmsion);
                        return false;
                    }
                }else{
                    sender.sendMessage("you must be a player to run this command");
                    return false;
                }
            }else if(args[0].equalsIgnoreCase("player")){
                if(sender.hasPermission("scord.set")){
                    if(args.length==4){
                        if(args[2].equalsIgnoreCase("score")){
                            Data.put(args[1], Integer.parseInt(args[3]));
                            return true;
                        }else if(args[2].equalsIgnoreCase("config")){
                            if(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("on") || args[3].equals("1")){
                                PlayerSetting.put(args[1], 1);
                                return true;
                            }else if(args[3].equalsIgnoreCase("false") || args[3].equalsIgnoreCase("off") || args[3].equals("1")){
                                PlayerSetting.put(args[1], 0);
                                return true;
                            }else{
                                sender.sendMessage(wrargs);
                                return false;
                            }
                        }else{
                            sender.sendMessage(wrargs);
                            return false;
                        }
                    }else if(args.length<4){
                        sender.sendMessage(tfargs);
                        return false;
                    }else{
                        sender.sendMessage(tmargs);
                        return false;
                    }
                }else{
                    sender.sendMessage(pmsion);
                    return false;
                }
            }else if(args[0].equalsIgnoreCase("reload")){
                if(sender.hasPermission("scord.set")){
                    if(args.length==1){
                        /*
                        if(customConfigFile == null){
                            customConfigFile=new File(getDataFolder(),"config.yml");
                        }
                        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
                        */
                        try{
                        reloadCustomConfig();
                        }catch(UnsupportedEncodingException err){
                            sender.sendMessage("config file encoding error");
                            err.printStackTrace();
                        }
                        //reloadConfig();
                        //saveCustomConfig();
                        sender.sendMessage("config reloaded");
                        update_board(first());
                        return true;
                    }else{
                        sender.sendMessage(tmargs);
                        return false;
                    }
                }else{
                    sender.sendMessage(pmsion);
                    return false;
                }
                /*
            }else if(args[0].equalsIgnoreCase("board")){
                if(sender.hasPermission("scord.set")){
                    if(args.length==4){
                        if(args[2].equalsIgnoreCase("set")){
                            if(yaml.contains(args[1])){
                                customConfig.set(args[1], args[3]);
                                saveCustomConfig();
                                return true;
                            }else{
                                sender.sendMessage(wrargs);
                                return false;
                            }
                        }else{
                            sender.sendMessage(wrargs);
                            return false;
                        }
                    }else if(args.length<4){
                        sender.sendMessage(tfargs);
                        return false;
                    }else{
                        sender.sendMessage(tmargs);
                        return false;
                    }
                }else{
                    sender.sendMessage(pmsion);
                    return false;
                }
            }else if(args[0].equalsIgnoreCase("blacklist")){
                if(sender.hasPermission("scord.set")){
                    if(args.length==3){
                        List<?> list = getCustomConfig().getList("blacklist");
                        if(args[1].equalsIgnoreCase("add")){
                            try {
                                list.add(args[2]);
                            } catch (UnsupportedOperationException e) {
                                sender.sendMessage("not support this name");
                                e.printStackTrace();
                            }
                            getCustomConfig().set("blacklist", list);
                            saveCustomConfig();
                        }else if(args[1].equalsIgnoreCase("remove")){
                            list.remove(args[2]);
                            getCustomConfig().set("blacklist",list);
                            saveCustomConfig();
                        }else if(args[1].equalsIgnoreCase("find") || args[1].equalsIgnoreCase("isin")){
                            sender.sendMessage("player" + args[2] + "is in blacklist: " + list.contains(args[2]));
                        }else{
                            sender.sendMessage(wrargs);
                            return false;
                        }
                    }else if(args.length<3){
                        sender.sendMessage(tfargs);
                        return false;
                    }else{
                        sender.sendMessage(tmargs);
                        return false;
                    }
                }else{
                    sender.sendMessage(pmsion);
                    return false;
                }*/
            }else{
                sender.sendMessage(wrargs);
                return false;
            }
        }else{
            sender.sendMessage(wrargs);
            return false;
        }
        /*
        if(cmd.getName().equalsIgnoreCase("scord")){
            if(args.length==1){
                if(sender instanceof Player){
                    if(sender.hasPermission("scord.basic")){
                        if(args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("on") || args[0].equals("1")){
                            PlayerSetting.put(sender.getName(),1);
                            update_board(first());
                            return true;
                        }else if(args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false") || args[0].equals("0")){
                            PlayerSetting.put(sender.getName(),0);
                            update_board(first());
                            return true;
                        }else{
                            sender.sendMessage(wrargs);
                            return false;
                        }
                    }else{
                        sender.sendMessage(pmsion);
                        return false;
                    }
                }else{
                    sender.sendMessage("you must be a player to run this command");
                    return false;
                }
            }else{
                if(args[0].equalsIgnoreCase("player")){
                    if (sender.hasPermission("scord.set")){
                        if(args.length==4){
                            if(args[0].equalsIgnoreCase("player")){
                                if(args[2].equalsIgnoreCase("score") || args[2].equalsIgnoreCase("scores")){
                                    Data.put(args[1],Integer.parseInt(args[3]));
                                    update_board(first());
                                    return true;
                                }else if(args[2].equalsIgnoreCase("config")){
                                    if(args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("on") || args[3].equals("1")){
                                        PlayerSetting.put(args[1],1);
                                        update_board(first());
                                        return true;
                                    }else if(args[3].equalsIgnoreCase("off") || args[3].equalsIgnoreCase("false") || args[3].equals("0")){
                                        PlayerSetting.put(args[1],0);
                                        update_board(first());
                                        return true;
                                    }else{
                                        sender.sendMessage(wrargs);
                                        return false;
                                    }
                                }else{
                                    sender.sendMessage(wrargs);
                                    return false;
                                }
                            }else if (args[0].equalsIgnoreCase("board")){
                                return false;
                            }else{
                                sender.sendMessage(wrargs);
                                return false;
                            }
                        }else if (args.length==3){
                            if (args[0].equalsIgnoreCase("blacklist")){

                            }else{
                                sender.sendMessage(wrargs);
                                return false;
                            }
                        }else{
                            sender.sendMessage(wrargs);
                            return false;
                        }
                    }else{
                        sender.sendMessage(pmsion);
                        return false;
                    }
                }
            }
        }
        */
        /*
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

         */
        //return false;
    }

    public HashMap<String,Integer> loadDB(String url){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(url));
            Object result = ois.readObject();
            ois.close();
            if(result instanceof HashMap){
                return (HashMap<String,Integer>)result;
            }else{
                return new HashMap<String,Integer>();
            }
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

    public void saveDefaultConfig(){
        if(customConfigFile ==null){
            customConfigFile = new File(getDataFolder(),"config.yml");
        }
        if(!customConfigFile.exists()){
            saveResource("config.yml",false);
        }
    }

    public void saveCustomConfig(){
        if(customConfig ==null || customConfigFile == null){
            return;
        }else{
            try{
                getCustomConfig().save(customConfigFile);
            }catch (IOException err){
                getLogger().log(Level.SEVERE,"Could not save config to " + customConfigFile, err);
            }
        }
    }

    public FileConfiguration getCustomConfig(){
        if(customConfig ==null){
            try {
                reloadCustomConfig();
            }catch (UnsupportedEncodingException err){
                getLogger().log(Level.SEVERE,"unable to read config file because of it is not encoding as UTF8",err);
            }
        }
        return customConfig;
    }

    public void reloadCustomConfig() throws UnsupportedEncodingException {
        if(customConfigFile == null){
            customConfigFile=new File(getDataFolder(),"config.yml");
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
        Reader defCustomStream = new InputStreamReader(this.getResource("config.yml"),"UTF8");
        if(defCustomStream != null){
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defCustomStream);
            customConfig.setDefaults(defConfig);
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
                Objective obj = scoreboard.registerNewObjective(player.getName()+"scord", "dummy", getCustomConfig().getString("title").replace("&","§"), RenderType.HEARTS);
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                if(leader.size()<1){
                    //getLogger().info("没人可以排");
                    return;
                }else {
                    int n = leader.size();
                    int i =1;
                    for(String name:leader){
                        obj.getScore(getCustomConfig().getString("numberprefix").replace("&","§") + i + getCustomConfig().getString("nameprefix").replace("&","§") + name + getCustomConfig().getString("scoreprefix").replace("&","§") + Data.get(name)).setScore(n - i+1);
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
        int NUM = 5;
        if(getCustomConfig().getInt("maxmiumleaders")>0){
            NUM = getCustomConfig().getInt("maxmiumleaders");
        }
        List<?> blacklist = getCustomConfig().getList("blacklist");
        List<String> list= Lists.newArrayList();
        Map<String,Integer> map = Data;
        for(Map.Entry<String, Integer> entry : map.entrySet()){
            if(blacklist.contains(entry.getKey()))continue;
            if(list.isEmpty()){
                list.add(entry.getKey());
            }else if(list.size()<NUM){
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
                for(int i=0;i<NUM;i++){
                    if(Data.get(list.get(i))<entry.getValue()){
                        list.add(i,entry.getKey());
                        list.remove(NUM);
                        break;
                    }
                }
            }
        }
        //list.add("M16A1");
        return list;
    }

}

