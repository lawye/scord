package io.github.lawye.scord;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ScordCommandExecutor implements CommandExecutor {
    private final Scord plugin;
    public ScordCommandExecutor(Scord plugin){
        this.plugin=plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        return false;
    }
}
