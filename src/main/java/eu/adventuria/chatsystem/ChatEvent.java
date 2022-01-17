package eu.adventuria.chatsystem;

import de.dytanic.cloudnet.ext.bridge.BaseComponentMessenger;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;

import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.UserManager;

import net.md_5.bungee.api.chat.*;

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

public class ChatEvent implements Listener {

    ChatSystem pl;
    CachedPermissionData Permission;
    String nickname;
    String color;
    String rank;
    String rankname;

    String prefix_local;
    String prefix_global;
    String prefix_spy;
    String prefix_team;

    private static UserManager um = LuckPermsProvider.get().getUserManager();

    public ChatEvent(ChatSystem instance){
        pl = instance;
        Bukkit.getPluginManager().registerEvents(this, instance);

        prefix_global = pl.getConfig().getString("chat.global");
        prefix_local = pl.getConfig().getString("chat.local");
        prefix_spy = pl.getConfig().getString("chat.spy");
        prefix_team = pl.getConfig().getString("chat.team");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        ICloudPlayer cp = BridgePlayerManager.getInstance().getOnlinePlayer(p.getUniqueId());

        this.rank = um.getUser(p.getUniqueId()).getPrimaryGroup();
        this.rankname = LuckPermsProvider.get().getGroupManager().getGroup(rank).getDisplayName();
        this.color = um.getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix().replace("&", "§");
        this.nickname = color + p.getName();
        this.Permission = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getPermissionData();

        String msg = e.getMessage();
        if(Permission.checkPermission("adventuria.chat.color").asBoolean()){
            msg = msg.replaceAll("&", "§");
        }
        String[] msglist = msg.split(" ");
        if(msglist.toString().contains("arsch")){
            e.setCancelled(true);
            p.sendMessage(Messages.prefix + "§cDu hast ein Wort geschrieben welches im Chat nicht erwünscht ist!");
        }
        if(msg.length() > 0){
            if(e.getMessage().startsWith("%") && p.hasPermission("adventuria.chat.team")){
                final BaseComponent[] base = new ComponentBuilder(prefix_team).append(nickname).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Rang: " + color + rank).create())).append(" §8» §7" + msg.replace("%", "")).reset().create();
                BaseComponentMessenger.broadcastMessage(base, "adventuria.chat.team");
                e.setCancelled(true);
            }else if(e.getMessage().startsWith("@l")){ // Lokalchat im Bereich von 60 Blöcken
                sendLocalMessage(e, msg.replace("@l", ""));
            }else if(e.getMessage().startsWith("@g")){ // Globalchat an alle Server
                final BaseComponent[] base = new ComponentBuilder(prefix_global).append(color + rankname +  " §8● " + nickname).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Server: §e" + cp.getConnectedService().getServerName()).create())).append(" §8» §7" + msg.replaceAll("%", "%%").replace("@g", "")).reset().create();
                BaseComponentMessenger.broadcastMessage(base);
                e.setCancelled(true);
            }else{
                e.setFormat(color + rankname +  " §8● " + nickname + " §8» §7" + msg.replaceAll("%", "%%"));
            }
        }else{
            p.sendMessage(Messages.prefix + "§cDu musst mindestens einen Buchstaben schreiben!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        this.color = um.getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix().replace("&", "§");
        this.nickname = color + p.getName();

        final net.kyori.adventure.text.TextComponent component = Component.text("§7Der Spieler ").append(Component.text(color + nickname)).append(Component.text(" §7hat das Spiel betreten."));
        e.joinMessage(component);

        ArangoMethods.createDefault(p.getUniqueId().toString());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();

        this.color = um.getUser(p.getUniqueId()).getCachedData().getMetaData().getPrefix().replace("&", "§");
        this.nickname = color + p.getName();

        final net.kyori.adventure.text.TextComponent component = Component.text("§7Der Spieler ").append(Component.text(color + nickname)).append(Component.text(" §7hat das Spiel verlassen."));
        e.quitMessage(component);
    }

    public void sendLocalMessage(AsyncPlayerChatEvent e, String msg){
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

        e.setFormat(prefix_local+ color + rankname +  " §8● " + nickname + " §8» §7" + msg.replaceAll("%", "%%"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("advisystem.spychat.see") &&
                    !plo.contains(player.getName()) && this.nickname != player
                    .getName())
                player.sendMessage(prefix_spy + color + rankname +  " §8● " + nickname + " §8» §7" + msg.replaceAll("%", "%%"));
        }
    }
}