package cc.baka9.catseedlogin.bukkit;

import cc.baka9.catseedlogin.bukkit.object.LoginPlayerHelper;

public class CatSeedLoginAPI {
    public static boolean isLogin(String name){
        return LoginPlayerHelper.isLogin(name);
    }

    public static boolean isRegister(String name){

        return LoginPlayerHelper.isRegister(name);

    }
}
