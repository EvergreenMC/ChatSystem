package net.evergreenmc.chatsystem;

import net.evergreenmc.chatsystem.utils.ArangoUtils;
import net.evergreenmc.chatsystem.utils.ConfigManager;
import net.evergreenmc.chatsystem.utils.Messages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ChatSystem extends JavaPlugin {

    private String prefix;

    public static ChatSystem instance;
    public ConfigManager cm = new ConfigManager(getDataFolder().toPath().toString(), "config.yml");

    @Override
    public void onEnable() {
        instance = this;

        cm.create("prefix.global", "§8§l| §aEvergreenMC §8» §7");
        cm.create("prefix.warning", "§8§l| §eWarning §8» §c");
        cm.create("chat.team", "§7[§b§lTeamChat§7] ");
        cm.create("chat.local", "§7[Local] ");
        cm.create("chat.global", "§7[Global] ");
        cm.create("chat.spy", "§7[Spy] ");
        cm.create("database.host", "localhost");
        cm.create("database.user", "user");
        cm.create("database.password", "password");
        cm.create("database.database", "database");
        cm.create("database.port", 8529);

        prefix = Messages.prefix;
        enableEvents();
        enableCommands();

        if(ArangoUtils.isConnected()){
            ArangoUtils.createCollection(ArangoUtils.database, "ChatSystem");

            getLogger().log(Level.INFO, " Das Plugin wurde aktiviert");
        }
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, " Das Plugin wurde deaktiviert");
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
