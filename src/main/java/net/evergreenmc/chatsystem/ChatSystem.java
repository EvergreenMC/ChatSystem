package net.evergreenmc.chatsystem;

import net.evergreenmc.chatsystem.commands.msgCommand;
import net.evergreenmc.chatsystem.utils.ArangoUtils;
import net.evergreenmc.chatsystem.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatSystem extends JavaPlugin {

    public static ChatSystem instance;
    public ConfigManager cm = new ConfigManager(getDataFolder().toPath().toString(), "config.yml");

    public static String prefix;

    @Override
    public void onEnable() {
        instance = this;
        prefix = cm.getString("prefix.global");

        cm.create("prefix.global", "§8§l| §aEvergreenMC §8» §7");
        cm.create("prefix.msg", "§8§l| §aChat §8» §7");
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

        enableCommands();
        enableEvents();

        new msgCommand(this);

        ArangoUtils.createCollection(ArangoUtils.database, "ChatSystem");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void enableEvents(){
        new ChatEvent(instance);
    }

    private void enableCommands(){}

    public static ChatSystem getInstance() {
        return instance;
    }

    public static String getPrefix() {
        return prefix;
    }
}
