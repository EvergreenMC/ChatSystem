package net.evergreenmc.chatsystem.commands;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.BaseComponentMessenger;
import de.dytanic.cloudnet.ext.bridge.node.CloudNetBridgeModule;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class msgCommand implements CommandExecutor, TabCompleter {

    public static ConfigManager cf = ChatSystem.getInstance().cm;
    private static ChatSystem pl = ChatSystem.getInstance();
    private static final UserManager um = LuckPermsProvider.get().getUserManager();
    private static final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);

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
            UUID uuid = null;

            rank = um.getUser(p.getUniqueId()).getPrimaryGroup();
            displayname = LuckPermsProvider.get().getGroupManager().getGroup(rank).getDisplayName();
            rankname = LuckPermsProvider.get().getGroupManager().getGroup(rank).getCachedData().getMetaData().getSuffix();
            color = um.getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix();
            nickname = color + p.getName();
            Permission = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getPermissionData();

            if(args.length < 2){
                p.sendMessage(prefix_msg + "§7Nutze: /msg <spieler> <nachricht>");
            }else{

                try{
                    uuid = Bukkit.getOfflinePlayer((args[0])).getUniqueId();
                }catch (Exception e){
                    p.sendMessage(prefix_warning + "§7Es liegt ein Fehler vor: (msgCommand.java:92-96)");
                }

                if(playerManager.getOnlinePlayer(uuid) == null) {
                    p.sendMessage(prefix_warning + "§cDer Spieler ist nicht Online!");
                }else{
                    targetrank = um.getUser(uuid).getPrimaryGroup();
                    targetdisplayname = LuckPermsProvider.get().getGroupManager().getGroup(targetrank).getDisplayName();
                    targetrankname = LuckPermsProvider.get().getGroupManager().getGroup(targetrank).getCachedData().getMetaData().getSuffix();
                    targetcolor = um.getUser(uuid).getCachedData().getMetaData().getPrefix();
                    targetnickname = targetcolor + Bukkit.getOfflinePlayer(uuid).getName();

                    if(args.length > 1){
                        String msg = "";
                        for (int x = 1; x < args.length; x++) {
                            msg = msg + args[x] + " ";
                        }

                        final BaseComponent[] base = new ComponentBuilder(prefix_msg).appendLegacy("§7[" + format(color) + "Ich§7] §7-> " + format(targetnickname) + "§7] " + msg).create();
                        BaseComponentMessenger.sendMessage(p.getUniqueId(), base);

                        final BaseComponent[] base1 = new ComponentBuilder(prefix_msg).appendLegacy("§7[" + format(nickname) + "§7] §7-> " + format(targetcolor) + "Ich§7] " + msg).create();
                        BaseComponentMessenger.sendMessage(uuid, base1);

                    }
                }
            }
        }else{
            sender.sendMessage(ChatSystem.getPrefix() + "§cDu musst ein Spieler sein um diesen Befehl nutzen zu dürfen!");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String arg, String[] args) {
        final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
        if(args.length == 1){
            ArrayList<String> list = new ArrayList<>();
            for(ICloudPlayer all : playerManager.getOnlinePlayers()){
                if(all.getName() != sender.getName()){
                    list.add(all.getName());
                }
            }
            return removeAutoComplete(list, args[0]);
        }
        return null;
    }

    public List<String> removeAutoComplete(List<String> list, String s) {
        if (!s.equalsIgnoreCase("")) {
            List<String> newList = new ArrayList<>(list);
            newList.stream().filter(a -> !a.startsWith(s)).forEach(list::remove);
        }
        return list;
    }
}
