package eu.evergreenmc.chatsystem;

import de.dytanic.cloudnet.ext.bridge.BaseComponentMessenger;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;

import eu.evergreenmc.chatsystem.utils.ArangoMethods;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.UserManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatEvent implements Listener {

    static ChatSystem pl;
    static CachedPermissionData Permission;
    static String nickname;
    static String color;
    static String rank;
    static String displayname;
    static String rankname;

    String prefix_local;
    String prefix_global;
    String prefix_spy;
    String prefix_team;

    private static UserManager um = LuckPermsProvider.get().getUserManager();

    public ChatEvent(ChatSystem instance) {
        pl = instance;
        Bukkit.getPluginManager().registerEvents(this, instance);

        prefix_global = pl.getConfig().getString("chat.global");
        prefix_local = pl.getConfig().getString("chat.local");
        prefix_spy = pl.getConfig().getString("chat.spy");
        prefix_team = pl.getConfig().getString("chat.team");
    }

    public String format(String msg) {
        final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}]");
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        Matcher matcher = pattern.matcher(msg);
        while(matcher.find()) {
            String color = msg.substring(matcher.start(), matcher.end());
            msg = msg.replace(color, ChatColor.of(color) + "");
            matcher = pattern.matcher(msg);
        }
        return msg;
    }


   @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        ICloudPlayer cp = BridgePlayerManager.getInstance().getOnlinePlayer(p.getUniqueId());

        this.rank = um.getUser(p.getUniqueId()).getPrimaryGroup();
        this.displayname = LuckPermsProvider.get().getGroupManager().getGroup(rank).getDisplayName();
        this.rankname = LuckPermsProvider.get().getGroupManager().getGroup(rank).getCachedData().getMetaData().getSuffix();
        this.color = um.getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix();
        this.nickname = color + p.getName();
        this.Permission = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getPermissionData();

        String msg = format(e.getMessage());

       if (e.getMessage().startsWith("%") && p.hasPermission("adventuria.chat.team")) {
           final BaseComponent[] base = new ComponentBuilder(prefix_team).appendLegacy(format(nickname)).appendLegacy(" §8» §7" + format(msg.replace("%", ""))).create();
           BaseComponentMessenger.broadcastMessage(base, "adventuria.chat.team");
           e.setCancelled(true);
       } else if (e.getMessage().startsWith("@l")) {
           sendLocalMessage(e, msg.replace("@l", ""));
       } else if (e.getMessage().startsWith("@g") || e.getMessage().startsWith("!")) {
           final BaseComponent[] base = new ComponentBuilder(prefix_global).appendLegacy("§8[" + format(color) + displayname + "§8] " + format(nickname)).appendLegacy(" §8» §7" + format(msg.replaceAll("%", "%%").replace("@g", "").replace("!", ""))).create();
           BaseComponentMessenger.broadcastMessage(base);
           e.setCancelled(true);
       } else {
           e.setCancelled(true);
           final BaseComponent[] base = new ComponentBuilder("").appendLegacy("§8[" + format(color) + displayname + "§8] " + format(nickname) + " §8» §7" + format(msg)).create();
           for(Player all : Bukkit.getOnlinePlayers()){
               all.sendMessage(base);
           }
       }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        this.color = um.getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix();
        this.nickname = color + p.getName();

        e.setJoinMessage("§7Der Spieler " + format(nickname) + " §7hat das Spiel betreten.");
        ArangoMethods.createDefault(p.getUniqueId().toString());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        this.color = um.getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix().replace("&", "§");
        this.nickname = color + p.getName();

        e.setQuitMessage("§7Der Spieler " + format(nickname) + " §7hat das Spiel verlassen.");
    }

    public void sendLocalMessage(AsyncPlayerChatEvent e, String msg) {
        int radius = 60;
        Player p = e.getPlayer();
        Location pl = p.getLocation();
        List<String> plo = new ArrayList<>();
        for (Player near : Bukkit.getOnlinePlayers()) {
            plo.add(near.getName());
            if (near.getWorld() != p.getWorld()) {
                e.getRecipients().remove(near);
                plo.remove(near.getName());
            }
            if (near.getWorld() == p.getWorld() && near.getLocation().distance(pl) > radius) {
                e.getRecipients().remove(near);
                plo.remove(near.getName());
            }
        }

        e.setFormat(format(prefix_local + "§8[" + color + displayname + "§8] " + nickname + " §8» §7" + msg.replaceAll("%", "%%")));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("advisystem.spychat.see") &&
                    !plo.contains(player.getName()) && this.nickname != player
                    .getName())
                player.sendMessage(format(prefix_spy + "§8[" + color + displayname + "§8] " + nickname + " §8» §7" + msg.replaceAll("%", "%%")));
        }
    }
}