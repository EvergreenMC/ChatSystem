package net.evergreenmc.chatsystem;

import net.evergreenmc.chatsystem.utils.ArangoUtils;
import net.evergreenmc.chatsystem.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatSystem extends JavaPlugin {

    private String prefix;
    private ConsoleCommandSender cs = Bukkit.getConsoleSender();

    public static ChatSystem instance;

    @Override
    public void onEnable() {
        instance = this;

        this.loadConfig();
        this.saveDefaultConfig();

        prefix = Messages.prefix;
        enableEvents();
        enableCommands();

        if(ArangoUtils.isConnected()){
            ArangoUtils.createCollection(ArangoUtils.database, "ChatSystem");

            cs.sendMessage(prefix + "§aDas Plugin wurde aktiviert.");
        }
    }

    @Override
    public void onDisable() {
        cs.sendMessage(prefix + "§4Das Plugin wurde deaktiviert.");
    }

    private void loadConfig(){
        FileConfiguration cfg = this.getConfig();
        cfg.options().copyDefaults(true);
        this.saveConfig();
    }

    private void enableEvents(){
        new ChatEvent(instance);
    }

    private void enableCommands(){}

    public static ChatSystem getInstance() {
        return instance;
    }
}