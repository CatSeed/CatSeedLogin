package cc.baka9.catseedlogin;

import cc.baka9.catseedlogin.object.LoginPlayerHelper;

public class CatSeedLoginAPI {
    public static boolean isLogin(String name){
        return LoginPlayerHelper.isLogin(name);
    }

    public static boolean isRegister(String name){

        return LoginPlayerHelper.isRegister(name);

    }
}
