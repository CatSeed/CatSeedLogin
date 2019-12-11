package cc.baka9.catseedlogin.database;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.object.LoginPlayer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cache {
    private static final Set<LoginPlayer> SET = new HashSet<>();
    public static boolean isLoaded = false;

    public static List<LoginPlayer> getAllLoginPlayer(){
        synchronized (SET) {
            return new ArrayList<>(SET);
        }

    }

    public static LoginPlayer getIgnoreCase(String name){
        synchronized (SET) {
            for (LoginPlayer lp : SET) {
                if (lp.getName().equalsIgnoreCase(name)) {
                    return lp;
                }
            }
        }
        return null;
    }

    public static LoginPlayer get(String name){
        synchronized (SET) {
            for (LoginPlayer lp : SET) {
                if (lp.getName().equals(name)) {
                    return lp;
                }
            }
        }
        return null;
    }

    public static void refreshAll(){
        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
            try {
                List<LoginPlayer> newCache = CatSeedLogin.sql.getAll();
                synchronized (SET) {
                    SET.clear();
                    SET.addAll(newCache);
                }
                CatSeedLogin.getInstance().getLogger().info("缓存加载 " + SET.size() + " 个数据");
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
                synchronized (SET) {
                    SET.remove(getIgnoreCase(name));
                    if (newLp != null) {
                        SET.add(newLp);
                    }
                }
                CatSeedLogin.getInstance().getLogger().info("缓存加载 " + SET.size() + " 个数据");
            } catch (Exception e) {
                CatSeedLogin.getInstance().getLogger().warning("数据库错误,无法更新缓存!");
                e.printStackTrace();
            }
        });
    }
}