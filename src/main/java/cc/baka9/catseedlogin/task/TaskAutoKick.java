package cc.baka9.catseedlogin.task;

import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TaskAutoKick extends Task {
    public Map<String, Long> playerJoinTime = new HashMap<>();

    @Override
    public void run(){
        if (!Cache.isLoaded || Config.Settings.AutoKick < 1) return;
        long autoKickMs = Config.Settings.AutoKick * 1000;
        long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!LoginPlayerHelper.isLogin(player.getName())) {
                if (playerJoinTime.containsKey(player.getName())) {
                    if (now - playerJoinTime.get(player.getName()) > autoKickMs) {
                        player.kickPlayer(Config.Language.AUTO_KICK.replace("{time}", Config.Settings.AutoKick + ""));
                    }
                } else {
                    playerJoinTime.put(player.getName(), now);
                }
            }
        }
    }
}
