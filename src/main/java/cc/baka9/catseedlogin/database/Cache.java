package cc.baka9.catseedlogin.database;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.object.LoginPlayer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cache {
    private static final Set<LoginPlayer> set = new HashSet<>();
    public static boolean isLoaded = false;

    public static List<LoginPlayer> getAllLoginPlayer(){
        synchronized (set) {
            return new ArrayList<>(set);
        }

    }

    public static LoginPlayer getIgnoreCase(String name){
        synchronized (set) {
            for (LoginPlayer lp : set) {
                if (lp.getName().equalsIgnoreCase(name)) return lp;
            }
        }
        return null;
    }

    public static LoginPlayer get(String name){
        synchronized (set) {
            for (LoginPlayer lp : set) {
                if (lp.getName().equals(name)) return lp;
            }
        }
        return null;
    }

    public static void refreshAll(){
        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
            try {
                List<LoginPlayer> newCache = CatSeedLogin.sql.getAll();
                synchronized (set) {
                    set.clear();
                    set.addAll(newCache);
                }
                CatSeedLogin.getInstance().getLogger().info("缓存加载 " + set.size() + " 个数据");
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
                synchronized (set) {
                    set.remove(getIgnoreCase(name));
                    if (newLp != null) set.add(newLp);
                }
                CatSeedLogin.getInstance().getLogger().info("缓存加载 " + set.size() + " 个数据");
            } catch (Exception e) {
                CatSeedLogin.getInstance().getLogger().warning("数据库错误,无法更新缓存!");
                e.printStackTrace();
            }
        });
    }
}