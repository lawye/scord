package io.github.lawye.scord;

import org.bukkit.plugin.java.JavaPlugin;

public class Scord extends JavaPlugin {
    @Override
    public void onEnable(){
        getLogger().info("Scord start initialize");
    }

    @Override
    public void onDisable(){
        getLogger().info("Scord shutdown, backing up");
    }

}
