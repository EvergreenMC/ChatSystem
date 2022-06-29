package net.evergreenmc.chatsystem.commands;

import de.dytanic.cloudnet.ext.bridge.BaseComponentMessenger;
import net.evergreenmc.chatsystem.ChatSystem;
import net.evergreenmc.chatsystem.utils.ConfigManager;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.UserManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class msgCommand implements CommandExecutor {

    public static ConfigManager cf = ChatSystem.getInstance().cm;
    private static ChatSystem pl = ChatSystem.getInstance();
    private static final UserManager um = LuckPermsProvider.get().getUserManager();

    static CachedPermissionData Permission;

    static String nickname;
    static String color;
    static String rank;
    static String displayname;
    static String rankname;

    static String targetnickname;
    static String targetcolor;
    static String targetrank;
    static String targetdisplayname;
    static String targetrankname;

    String prefix_global;
    String prefix_msg;
    String prefix_warning;

    public final Pattern pattern;

    public msgCommand(ChatSystem instance){
        this.pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        pl = instance;

        prefix_global = cf.getString("prefix.global");
        prefix_warning = cf.getString("prefix.warning");
        prefix_msg = cf.getString("prefix.msg");

        pl.getCommand("msg").setExecutor(this);
    }

    private String format(String msg) {
        Matcher match = this.pattern.matcher(msg);
        while (match.find()) {
            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, ChatColor.of(color) + "");
            match = this.pattern.matcher(msg);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    //      /msg <player> <message>
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        if(sender instanceof Player){
            Player p = (Player)sender;

            rank = um.getUser(p.getUniqueId()).getPrimaryGroup();
            displayname = LuckPermsProvider.get().getGroupManager().getGroup(rank).getDisplayName();
            rankname = LuckPermsProvider.get().getGroupManager().getGroup(rank).getCachedData().getMetaData().getSuffix();
            color = um.getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix();
            nickname = color + p.getName();
            Permission = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getPermissionData();


            if(Bukkit.getPlayer((args[0])).getUniqueId() != null){
                UUID uuid = Bukkit.getPlayer((args[0])).getUniqueId();

                targetrank = um.getUser(uuid).getPrimaryGroup();
                targetdisplayname = LuckPermsProvider.get().getGroupManager().getGroup(targetrank).getDisplayName();
                targetrankname = LuckPermsProvider.get().getGroupManager().getGroup(targetrank).getCachedData().getMetaData().getSuffix();
                targetcolor = um.getUser(uuid).getCachedData().getMetaData().getPrefix();
                targetnickname = targetcolor + Bukkit.getPlayer(uuid).getName();

                if(args.length > 1){
                    String msg = "";
                    for (int x = 1; x < args.length; x++) {
                        msg = msg + args[x] + " ";
                    }

                    final BaseComponent[] base = new ComponentBuilder(prefix_msg).appendLegacy("§7[" + format(targetcolor) + "Ich§7] §7-> " + format(targetnickname) + "§7] " + msg).create();
                    BaseComponentMessenger.sendMessage(uuid, base);

                    final BaseComponent[] base1 = new ComponentBuilder(prefix_msg).appendLegacy("§7[" + format(targetnickname) + "§7] §7-> " + format(color) + "Ich§7] " + msg).create();
                    BaseComponentMessenger.sendMessage(p.getUniqueId(), base1);

                }
            }else{
                p.sendMessage(prefix_warning + "§cDieser Spieler ist nicht auf dem Server Online!");
            }

        }else{
            sender.sendMessage(ChatSystem.getPrefix() + "§cDu musst ein Spieler sein um diesen Befehl nutzen zu dürfen!");
        }
        return false;
    }
}
