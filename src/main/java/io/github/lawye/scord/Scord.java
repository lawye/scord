package io.github.lawye.scord;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

public final class Scord extends JavaPlugin {
    public final String URL = "~/Scord.db";
    @Override
    public void onEnable(){
        getLogger().info("Scord start initialize");
    }
    @Override
    public void onDisable(){
        getLogger().info("Scord shutdown, backing up");
    }
    public HashMap<String,Integer> loadDB() throws IOException, ClassNotFoundException {
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(URL));
            Object result = ois.readObject();
            return (HashMap<String,Integer>)result;
        }catch(Exception err){
            err.printStackTrace();
        }
    }
}
