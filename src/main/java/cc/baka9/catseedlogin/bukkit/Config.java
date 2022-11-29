package cc.baka9.catseedlogin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 加载/保存/重载 yml配置文件
 * config.yml 玩家退出服务器的位置
 * emailVerify.yml 邮箱找回密码
 * language.yml 语言，提示
 * settings.yml 设置
 * sql.yml 数据库
 */
public class Config {
    private static CatSeedLogin plugin = CatSeedLogin.instance;
    private static Map<String, String> offlineLocations = new HashMap<>();

    /**
     * 数据库
     */
    public static class MySQL {
        public static boolean Enable;
        public static String Host;
        public static String Port;
        public static String Database;
        public static String User;
        public static String Password;

        public static void load(){
            FileConfiguration config = getConfig("sql.yml");
            MySQL.Enable = config.getBoolean("MySQL.Enable");
            MySQL.Host = config.getString("MySQL.Host");
            MySQL.Port = config.getString("MySQL.Port");
            MySQL.Database = config.getString("MySQL.Database");
            MySQL.User = config.getString("MySQL.User");
            MySQL.Password = config.getString("MySQL.Password");
        }
    }

    public static class BungeeCord {
        public static boolean Enable;
        public static String Host;
        public static String Port;
        public static String AuthKey;

        public static void load(){

            FileConfiguration config = getConfig("bungeecord.yml");
            BungeeCord.Enable = config.getBoolean("Enable");
            BungeeCord.Host = config.getString("Host");
            BungeeCord.Port = config.getString("Port");
            BungeeCord.AuthKey = config.getString("AuthKey");
        }
    }

    /**
     * 设置
     */
    public static class Settings {
        public static int IpRegisterCountLimit;
        public static int IpCountLimit;
        public static Location SpawnLocation;
        public static boolean LimitChineseID;
        public static int MaxLengthID;
        public static int MinLengthID;
        public static boolean BeforeLoginNoDamage;
        public static long ReenterInterval;
        public static boolean AfterLoginBack;
        public static boolean CanTpSpawnLocation;
        public static List<Pattern> CommandWhiteList = new ArrayList<>();
        public static int AutoKick;
        // 死亡状态退出游戏是否记录退出位置 (玩家可以通过死亡时退出服务器然后重新进入，再复活，登录返回死亡地点)
        public static boolean DeathStateQuitRecordLocation;

        public static void load(){
            FileConfiguration config = getConfig("settings.yml");
            FileConfiguration resourceConfig = getResourceConfig("settings.yml");

            IpRegisterCountLimit = config.getInt("IpRegisterCountLimit", resourceConfig.getInt("IpRegisterCountLimit"));
            IpCountLimit = config.getInt("IpCountLimit", resourceConfig.getInt("IpCountLimit"));
            LimitChineseID = config.getBoolean("LimitChineseID", resourceConfig.getBoolean("LimitChineseID"));
            MinLengthID = config.getInt("MinLengthID", resourceConfig.getInt("MinLengthID"));
            MaxLengthID = config.getInt("MaxLengthID", resourceConfig.getInt("MaxLengthID"));
            BeforeLoginNoDamage = config.getBoolean("BeforeLoginNoDamage", resourceConfig.getBoolean("BeforeLoginNoDamage"));
            ReenterInterval = config.getLong("ReenterInterval", resourceConfig.getLong("ReenterInterval"));
            AfterLoginBack = config.getBoolean("AfterLoginBack", resourceConfig.getBoolean("AfterLoginBack"));
            CanTpSpawnLocation = config.getBoolean("CanTpSpawnLocation", resourceConfig.getBoolean("CanTpSpawnLocation"));
            List<String> commandWhiteList = config.getStringList("CommandWhiteList");
            if (commandWhiteList.size() == 0) {
                commandWhiteList = resourceConfig.getStringList("CommandWhiteList");
            }
            Settings.CommandWhiteList.clear();
            Settings.CommandWhiteList.addAll(commandWhiteList.stream().map(Pattern::compile).collect(Collectors.toList()));
            AutoKick = config.getInt("AutoKick", 120);
            SpawnLocation = str2Location(config.getString("SpawnLocation"));
            DeathStateQuitRecordLocation = config.getBoolean("DeathStateQuitRecordLocation", resourceConfig.getBoolean("DeathStateQuitRecordLocation"));


        }

        public static void save(){
            FileConfiguration config = getConfig("settings.yml");
            config.set("IpRegisterCountLimit", IpRegisterCountLimit);
            config.set("IpCountLimit", IpCountLimit);
            config.set("SpawnWorld", null);
            config.set("LimitChineseID", LimitChineseID);
            config.set("MinLengthID", MinLengthID);
            config.set("MaxLengthID", MaxLengthID);
            config.set("BeforeLoginNoDamage", BeforeLoginNoDamage);
            config.set("ReenterInterval", ReenterInterval);
            config.set("AfterLoginBack", AfterLoginBack);
            config.set("CanTpSpawnLocation", CanTpSpawnLocation);
            config.set("AutoKick", AutoKick);
            config.set("SpawnLocation", loc2String(SpawnLocation));
            config.set("CommandWhiteList", CommandWhiteList.stream().map(Pattern::toString).collect(Collectors.toList()));
            config.set("DeathStateQuitRecordLocation", DeathStateQuitRecordLocation);
            try {
                config.save(new File(CatSeedLogin.instance.getDataFolder(), "settings.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 语言，提示
     */
    public static class Language {
        public static String LOGIN_REQUEST;
        public static String REGISTER_REQUEST;
        public static String LOGIN_NOREGISTER;
        public static String LOGIN_REPEAT;
        public static String LOGIN_SUCCESS;
        public static String LOGIN_FAIL;
        public static String LOGIN_FAIL_IF_FORGET;
        public static String REGISTER_SUCCESS;
        public static String REGISTER_BEFORE_LOGIN_ALREADY;
        public static String REGISTER_AFTER_LOGIN_ALREADY;
        public static String REGISTER_PASSWORD_CONFIRM_FAIL;
        public static String COMMON_PASSWORD_SO_SIMPLE;
        public static String RESETPASSWORD_NOREGISTER;
        public static String RESETPASSWORD_EMAIL_DISABLE;
        public static String RESETPASSWORD_EMAIL_NO_SET;
        public static String RESETPASSWORD_EMAIL_REPEAT_SEND_MESSAGE;
        public static String RESETPASSWORD_EMAIL_SENDING_MESSAGE;
        public static String RESETPASSWORD_EMAIL_SENT_MESSAGE;
        public static String RESETPASSWORD_EMAIL_WARN;
        public static String RESETPASSWORD_SUCCESS;
        public static String RESETPASSWORD_EMAILCODE_INCORRECT;
        public static String RESETPASSWORD_FAIL;
        public static String CHANGEPASSWORD_NOREGISTER;
        public static String CHANGEPASSWORD_NOLOGIN;
        public static String CHANGEPASSWORD_OLDPASSWORD_INCORRECT;
        public static String CHANGEPASSWORD_PASSWORD_CONFIRM_FAIL;
        public static String CHANGEPASSWORD_SUCCESS;
        public static String AUTO_KICK;
        public static String REGISTER_MORE;

        public static void load(){
            FileConfiguration resourceConfig = getResourceConfig("language.yml");
            FileConfiguration config = getConfig("language.yml");
            for (Field field : Language.class.getDeclaredFields()) {
                try {
                    String fieldName = field.getName();
                    String value = config.getString(fieldName, resourceConfig.getString(fieldName));
                    field.set(null, value.replace('&', ChatColor.COLOR_CHAR));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 邮箱找回密码
     */
    public static class EmailVerify {

        public static boolean Enable;
        public static String EmailAccount;
        public static String EmailPassword;
        public static String EmailSmtpHost;
        public static String EmailSmtpPort;
        public static boolean SSLAuthVerify;
        public static String FromPersonal;


        public static void load(){
            FileConfiguration config = getConfig("emailVerify.yml");
            Enable = config.getBoolean("Enable");
            EmailAccount = config.getString("EmailAccount");
            EmailPassword = config.getString("EmailPassword");
            EmailSmtpHost = config.getString("EmailSmtpHost");
            EmailSmtpPort = config.getString("EmailSmtpPort");
            SSLAuthVerify = config.getBoolean("SSLAuthVerify");
            FromPersonal = config.getString("FromPersonal");

        }

    }
    // 获取插件文件夹中的配置文件，如果不存在则从插件jar包中获取配置文件保存到插件文件夹中
    public static FileConfiguration getConfig(String yamlFileName){
        File file = new File(plugin.getDataFolder(), yamlFileName);
        if (!file.exists()) {
            plugin.saveResource(yamlFileName, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    // 获取插件jar包中的配置文件
    public static FileConfiguration getResourceConfig(String yamlFileName){
        return YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(yamlFileName), StandardCharsets.UTF_8));
    }

    public static void load(){
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        if (config.contains("offlineLocations")) {
            config.getConfigurationSection("offlineLocations").getKeys(false).forEach(key ->
                    offlineLocations.put(key, config.getString("offlineLocations." + key))
            );
        }
        MySQL.load();
        Settings.load();
        EmailVerify.load();
        Language.load();
        BungeeCord.load();
    }

    public static void save(){
        Settings.save();
    }

    public static void reload(){
        plugin.reloadConfig();
        load();

    }

    // 获取玩家退出服务器时的位置
    public static Optional<Location> getOfflineLocation(Player player){
        String data = offlineLocations.get(player.getName());
        return data == null ? Optional.empty() : Optional.of(str2Location(data));
    }

    // 保存玩家退出服务器的位置
    public static void setOfflineLocation(Player player){
        String name = player.getName();
        String data = loc2String(player.getLocation());
        offlineLocations.put(name, data);
        plugin.getConfig().set("offlineLocations." + name, data);
        plugin.saveConfig();
    }

    // 字符串转成位置
    private static Location str2Location(String str){
        Location loc;
        try {
            String[] locStrs = str.split(":");
            World world = Bukkit.getWorld(locStrs[0]);
            double x = Double.parseDouble(locStrs[1]);
            double y = Double.parseDouble(locStrs[2]);
            double z = Double.parseDouble(locStrs[3]);
            float yaw = Float.parseFloat(locStrs[4]);
            float pitch = Float.parseFloat(locStrs[5]);
            loc = new Location(world, x, y, z, yaw, pitch);
        } catch (Exception ignored) {
            loc = getDefaultWorld().getSpawnLocation();
        }
        fixLocation(loc);
        return loc;

    }
    // 位置转成字符串
    private static String loc2String(Location loc){
        fixLocation(loc);
        try {
            return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
        } catch (Exception ignored) {
            loc = getDefaultWorld().getSpawnLocation();
        }
        fixLocation(loc);
        return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();

    }

    // 修正位置坐标防止崩服卡服
    private static void fixLocation(Location location){
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        location.setYaw(yaw > 180 || yaw < -180 ? 0 : (float)(Math.round(yaw * 100)) / 100);
        location.setPitch(pitch > 90 || pitch < -90 ? 0 : (float)(Math.round(pitch * 100)) / 100);
        location.setX((double)(Math.round(location.getX() * 10000)) / 10000);
        location.setY((double)(Math.round(location.getY() * 10000)) / 10000);
        location.setZ((double)(Math.round(location.getZ() * 10000)) / 10000);
    }

    // 获取默认世界
    private static World getDefaultWorld(){
        try (InputStream is = new BufferedInputStream(Files.newInputStream(new File("server.properties").toPath()))) {
            Properties properties = new Properties();
            properties.load(is);
            String worldName = properties.getProperty("level-name");
            return Bukkit.getWorld(worldName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Bukkit.getWorlds().get(0);
    }


}
