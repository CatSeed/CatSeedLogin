package cc.baka9.catseedlogin.bukkit;

import cc.baka9.catseedlogin.bukkit.object.LoginPlayerHelper;

/**
 * API
 *
 * @author handy
 */
public class CatSeedLoginAPI {

    /**
     * 是否登陆
     *
     * @param name 玩家名
     * @return true 是
     */
    public static boolean isLogin(String name) {
        return LoginPlayerHelper.isLogin(name);
    }

    /**
     * 是否注册
     *
     * @param name 玩家名
     * @return true 注册
     */
    public static boolean isRegister(String name) {
        return LoginPlayerHelper.isRegister(name);
    }

    /**
     * 获取最后登陆时间戳
     *
     * @param name 玩家名
     * @return 时间戳- 未注册为null
     * @since 1.4.2
     */
    public static Long getLastLoginTime(String name) {
        return LoginPlayerHelper.getLastLoginTime(name);
    }

}
