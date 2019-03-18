package cc.baka9.catseedlogin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Config {
    private static CatSeedLogin plugin = CatSeedLogin.getInstance();
    private static Map<String, Location> offlineLocations = new HashMap<>();

    public static void load(){
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        if (config.contains("offlineLocations"))
            config.getConfigurationSection("offlineLocations").getKeys(false).forEach(key ->
                    offlineLocations.put(key, str2Location(config.getString("offlineLocations." + key)))
            );

    }

    public static Location getOfflineLocation(Player player){
        Location loc = offlineLocations.get(player.getName());
        return loc == null ? Bukkit.getWorld("world").getSpawnLocation() : loc;
    }

    public static void setOfflineLocation(Player player){
        String name = player.getName();
        Location loc = player.getLocation();
        offlineLocations.put(name, loc);
        plugin.getConfig().set("offlineLocations." + name, loc2String(loc));
        plugin.saveConfig();
    }

    private static Location str2Location(String str){
        Location loc;
        String[] locStrs = str.split(":");
        try {

            World world = Bukkit.getWorld(locStrs[0]);
            double x = Double.valueOf(locStrs[1]);
            double y = Double.valueOf(locStrs[2]);
            double z = Double.valueOf(locStrs[3]);
            float yaw = Float.valueOf(locStrs[4]);
            float pitch = Float.valueOf(locStrs[5]);
            loc = new Location(world, x, y, z, yaw, pitch);
        } catch (Exception ignored) {
            loc = Bukkit.getWorld("world").getSpawnLocation();
        }
        return loc;

    }

    private static String loc2String(Location loc){
        return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();

    }


}
