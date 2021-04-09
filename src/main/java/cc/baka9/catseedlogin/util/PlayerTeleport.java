package cc.baka9.catseedlogin.util;

import cc.baka9.catseedlogin.CatSeedLogin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerTeleport {

    private static boolean async = true;

    public static void teleport(Player player, Location location){
        if (async) {
            CatSeedLogin.instance.runTaskAsync(() -> {
                try {
                    player.teleport(location);
                } catch (IllegalStateException e) {
                    async = false;
                    CatSeedLogin.instance.getLogger().warning("无法异步传送玩家，插件切换成主线程传送玩家");
                    Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> teleport(player, location));
                }
            });
        } else {
            player.teleport(location);
        }

    }

}
