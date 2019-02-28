package cc.baka9.catseedlogin.object;

import cc.baka9.catseedlogin.database.Cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoginPlayerHelper {
    private static final Set<LoginPlayer> set = new HashSet<>();

    public static List<LoginPlayer> getList(){
        return new ArrayList<>(set);
    }

    public static void add(LoginPlayer lp){
        set.add(lp);
    }

    public static void remove(LoginPlayer lp){
        set.remove(lp);
    }

    public static void remove(String name){
        for (LoginPlayer lp : set) {
            if (lp.getName().equals(name)) {
                set.remove(lp);
                break;
            }
        }
    }

    public static boolean isLogin(String name){
        for (LoginPlayer lp : set) {
            if (lp.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRegister(String name){

        return Cache.getIgnoreCase(name) != null;

    }
}
