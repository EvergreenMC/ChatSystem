package eu.evergreenmc.chatsystem;

import eu.evergreenmc.chatsystem.utils.ArangoMethods;
import eu.evergreenmc.chatsystem.utils.ArangoUtils;
import eu.evergreenmc.chatsystem.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

public class ChatSystem extends JavaPlugin {

    private String prefix;
    private ConsoleCommandSender cs = Bukkit.getConsoleSender();
    private static ScoreboardManager instance2;

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
            ArangoUtils.createDatabase(ArangoMethods.database);
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

    public String getPrefix() {
        return prefix;
    }

    private void enableEvents(){
        new ChatEvent(instance);
    }

    private void enableCommands(){
        getCommand("chat").setExecutor(new ChatCommand());
    }

    public static ChatSystem getInstance() {
        return instance;
    }
}