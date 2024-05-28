package cc.baka9.catseedlogin.bukkit.database;

import cc.baka9.catseedlogin.bukkit.CatSeedLogin;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private static final ConcurrentHashMap<String, LoginPlayer> PLAYER_MAP = new ConcurrentHashMap<>();
    public static volatile boolean isLoaded = false;

    public static List<LoginPlayer> getAllLoginPlayer() {
        synchronized (PLAYER_MAP) {
            return new ArrayList<>(PLAYER_MAP.values());
        }
    }

    public static LoginPlayer getIgnoreCase(String name) {
        return PLAYER_MAP.get(name.toLowerCase());
    }

    public static void refreshAll() {
        isLoaded = false;
        CatSeedLogin.instance.runTaskAsync(() -> {
            try {
                List<LoginPlayer> newCache = CatSeedLogin.sql.getAll();
                synchronized (PLAYER_MAP) {
                    PLAYER_MAP.clear();
                    newCache.forEach(p -> PLAYER_MAP.put(p.getName().toLowerCase(), p));
                }
                CatSeedLogin.instance.getLogger().info("缓存加载 " + PLAYER_MAP.size() + " 个数据");
                isLoaded = true;
            } catch (Exception e) {
                CatSeedLogin.instance.getLogger().warning("数据库错误,无法更新缓存!");
                e.printStackTrace();
            }
        });
    }

    public static void refresh(String name) {
        if (name == null) {
            return;
        }
        CatSeedLogin.instance.runTaskAsync(() -> {
            try {
                LoginPlayer newLp = CatSeedLogin.sql.get(name);
                String key = name.toLowerCase();
                if (newLp != null) {
                    PLAYER_MAP.put(key, newLp);
                } else {
                    PLAYER_MAP.remove(key);
                }
                CatSeedLogin.instance.getLogger().info("缓存加载 " + PLAYER_MAP.size() + " 个数据");
            } catch (Exception e) {
                CatSeedLogin.instance.getLogger().warning("数据库错误,无法更新缓存!");
                e.printStackTrace();
            }
        });
    }
}