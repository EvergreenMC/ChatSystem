package eu.adventuria.chatsystem;

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

        prefix = getInstance().getConfig().getString("prefix.global");
        cs.sendMessage(prefix + "§aDas Plugin wurde aktiviert.");
        enableEvents();

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

    public static ChatSystem getInstance() {
        return instance;
    }
}
