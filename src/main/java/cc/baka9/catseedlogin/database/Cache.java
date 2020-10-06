package cc.baka9.catseedlogin.database;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.object.LoginPlayer;
import org.bukkit.Bukkit;

import java.util.*;

public class Cache {
    private static final Hashtable<String, LoginPlayer> PLAYER_HASHTABLE = new Hashtable<>();
    public static volatile boolean isLoaded = false;

    public static List<LoginPlayer> getAllLoginPlayer(){
        synchronized (PLAYER_HASHTABLE) {
            return new ArrayList<>(PLAYER_HASHTABLE.values());
        }

    }

    public static LoginPlayer getIgnoreCase(String name){

        return PLAYER_HASHTABLE.get(name.toLowerCase());
    }


    public static void refreshAll(){
        isLoaded = false;
        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
            try {
                List<LoginPlayer> newCache = CatSeedLogin.sql.getAll();
                synchronized (PLAYER_HASHTABLE) {
                    PLAYER_HASHTABLE.clear();
                    newCache.forEach(p -> PLAYER_HASHTABLE.put(p.getName().toLowerCase(), p));
                }
                CatSeedLogin.getInstance().getLogger().info("缓存加载 " + PLAYER_HASHTABLE.size() + " 个数据");
                isLoaded = true;
            } catch (Exception e) {
                CatSeedLogin.getInstance().getLogger().warning("数据库错误,无法更新缓存!");
                e.printStackTrace();
            }
        });
    }

    public static void refresh(String name){
        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
            try {
                LoginPlayer newLp = CatSeedLogin.sql.get(name);
                String key = name.toLowerCase();
                if (newLp != null) {
                    PLAYER_HASHTABLE.put(key, newLp);
                } else {
                    PLAYER_HASHTABLE.remove(key);
                }
                CatSeedLogin.getInstance().getLogger().info("缓存加载 " + PLAYER_HASHTABLE.size() + " 个数据");
            } catch (Exception e) {
                CatSeedLogin.getInstance().getLogger().warning("数据库错误,无法更新缓存!");
                e.printStackTrace();
            }
        });
    }
}