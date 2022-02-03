package eu.adventuria.chatsystem;

import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.user.UserManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreboardManager implements Listener {

    /*public String rang = "Testrang";
    public int coins = 0;

    public final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    private String format(String msg) {
        Matcher match = pattern.matcher(msg);
        while (match.find()) {
            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, ChatColor.of(color) + "");
            match = pattern.matcher(msg);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        setScoreboard(p);

        setTab(p);
        Bukkit.getOnlinePlayers().forEach(all -> updateTab(all));

        *//*
        Bukkit.getScheduler().runTaskTimer(ChatSystem.getInstance(),() -> {
            this.coins++;
            updateScoreboard(p);
        }, 0,20);
        *//*
    }

    public void setScoreboard(Player p){
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("aaa", "bbb");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§1Test");

        Team test1 = board.registerNewTeam("test1");
        Team test2 = board.registerNewTeam("test2");

        obj.getScore("§1Coins:").setScore(3);
        obj.getScore("§d").setScore(2);
        obj.getScore("§5Rang:").setScore(1);
        obj.getScore("§e").setScore(0);

        test1.addEntry("§d");
        test1.setPrefix("§d" + coins);

        test2.addEntry("§e");
        test2.setPrefix("§e" + rang);

        p.setScoreboard(board);
    }

    public void updateScoreboard(Player p){
        Scoreboard board = p.getScoreboard();
        Team test1 = board.getTeam("test1");
        Team test2 = board.getTeam("test2");

        test1.setPrefix("§d" + coins);
        test2.setPrefix("§e" + rang);
    }


    public void setTab(Player p){
        Scoreboard board = p.getScoreboard();

        Team verwaltung = board.registerNewTeam("000Verwaltung");
        Team entwickler_leitung = board.registerNewTeam("001Entwickler_Leitung");
        Team entwickler = board.registerNewTeam("002Entwickler");
        Team architekt_leitung = board.registerNewTeam("003Architekt_Leitung");
        Team architekt = board.registerNewTeam("004Architekt");
        Team support_leitung = board.registerNewTeam("005Support_Leitung");
        Team supporter = board.registerNewTeam("006Supporter");
        Team social_leitung = board.registerNewTeam("007Social_Leitung");
        Team social = board.registerNewTeam("008Social");
        Team spieler = board.registerNewTeam("009Spieler");

        verwaltung.setPrefix(format("#ff0000Verwaltung &8● #ff0000"));
        spieler.setPrefix("§aSpieler §8● §a");

        Bukkit.getOnlinePlayers().forEach(all ->{
            all.sendMessage(LuckPermsProvider.get().getUserManager().getUser(all.getUniqueId()).getPrimaryGroup());
            if(LuckPermsProvider.get().getUserManager().getUser(all.getUniqueId()).getPrimaryGroup() == "verwaltung"){
                verwaltung.addEntry(all.getName());
            }else{
                spieler.addEntry(all.getName());
            }
        });
    }

    public void updateTab(Player p){
        Scoreboard board = p.getScoreboard();

        Team verwaltung = board.getTeam("000Verwaltung");
        Team spieler = board.getTeam("009Spieler");

        if(verwaltung == null || spieler == null){
            setTab(p);
            return;
        }

        Bukkit.getOnlinePlayers().forEach(all ->{
            all.sendMessage(LuckPermsProvider.get().getUserManager().getUser(all.getUniqueId()).getPrimaryGroup());
            if(LuckPermsProvider.get().getUserManager().getUser(all.getUniqueId()).getPrimaryGroup() == "verwaltung"){
                verwaltung.addEntry(all.getName());
            }else{
                spieler.addEntry(all.getName());
            }
        });
    }*/
}
