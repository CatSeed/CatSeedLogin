package cc.baka9.catseedlogin.object;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.database.Cache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LoginPlayerHelper {
    private static final Set<LoginPlayer> set = new HashSet<>();

    public static List<LoginPlayer> getList(){
        return new ArrayList<>(set);
    }

    public static void add(LoginPlayer lp){
        synchronized (set) {

            set.add(lp);
        }
    }

    public static void remove(LoginPlayer lp){
        synchronized (set) {

            set.remove(lp);
        }
    }

    public static void remove(String name){
        synchronized (set) {
            for (LoginPlayer lp : set) {
                if (lp.getName().equals(name)) {
                    set.remove(lp);
                    break;
                }
            }
        }
    }

    public static boolean isLogin(String name){
        synchronized (set) {
            for (LoginPlayer lp : set) {
                if (lp.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isRegister(String name){

        return Cache.getIgnoreCase(name) != null;

    }

    // 记录登录IP
    public static void recordCurrentIP(Player player, LoginPlayer lp){
        String currentIp = player.getAddress().getHostName();
        List<String> ipsList = lp.getIpsList();
        ipsList.add(currentIp);
        ipsList = ipsList.stream().distinct().collect(Collectors.toList());
        if (ipsList.size() > 5) {
            ipsList.remove(0);
        }
        lp.setIps(String.join(";", ipsList.toArray(new String[0])));
        CatSeedLogin.instance.runTaskAsync(() -> {
            try {
                CatSeedLogin.sql.edit(lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
