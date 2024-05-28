package cc.baka9.catseedlogin.bukkit;

import cc.baka9.catseedlogin.bukkit.object.LoginPlayerHelper;

public class CatSeedLoginAPI {
    public static boolean isLogin(String name) {
        try {
            return LoginPlayerHelper.isLogin(name);
        } catch (Exception e) {
            System.err.println("登录验证出现异常: " + e.getMessage());
            return false;
        }
    }

    public static boolean isRegister(String name) {
        try {
            return LoginPlayerHelper.isRegister(name);
        } catch (Exception e) {
            System.err.println("检查玩家注册时发生了错误: " + e.getMessage());
            return false;
        }
    }
}