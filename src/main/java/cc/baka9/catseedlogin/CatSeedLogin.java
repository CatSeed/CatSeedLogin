package cc.baka9.catseedlogin;

import cc.baka9.catseedlogin.command.*;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.database.MySQL;
import cc.baka9.catseedlogin.database.SQL;
import cc.baka9.catseedlogin.database.SQLite;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CatSeedLogin extends JavaPlugin {

    private static CatSeedLogin instance;
    public static SQL sql;

    @Override
    public void onEnable(){
        getServer().getLogger().info("                                                                                 \n" +
                "                                                                                 \n" +
                "  ______   ______   ______   ______   ______   ______   ______   ______   ______ \n" +
                " /_____/  /_____/  /_____/  /_____/  /_____/  /_____/  /_____/  /_____/  /_____/ \n" +
                "                                                                                 \n" +
                "                                                                                 \n" +
                "_________         __   _________                 .___                            \n" +
                "\\_   ___ \\_____ _/  |_/   _____/ ____   ____   __| _/                            \n" +
                "/    \\  \\/\\__  \\\\   __\\_____  \\_/ __ \\_/ __ \\ / __ |                             \n" +
                "\\     \\____/ __ \\|  | /        \\  ___/\\  ___// /_/ |                             \n" +
                " \\______  (____  /__|/_______  /\\___  >\\___  >____ |                             \n" +
                "        \\/     \\/            \\/     \\/     \\/     \\/                             \n" +
                ".____                 .__                                                        \n" +
                "|    |    ____   ____ |__| ____                                                  \n" +
                "|    |   /  _ \\ / ___\\|  |/    \\                                                 \n" +
                "|    |__(  <_> ) /_/  >  |   |  \\                                                \n" +
                "|_______ \\____/\\___  /|__|___|  /                                                \n" +
                "        \\/    /_____/         \\/                                                 \n" +
                "                                                                                 \n" +
                "                                                                                 \n" +
                "  ______   ______   ______   ______   ______   ______   ______   ______   ______ \n" +
                " /_____/  /_____/  /_____/  /_____/  /_____/  /_____/  /_____/  /_____/  /_____/ \n" +
                "                                                                                 \n" +
                "                                                                                 ");
        instance = this;
        //Config
        Config.load();
        sql = Config.MySQL.Enable ? new MySQL(this) : new SQLite(this);
        try {

            sql.init();

            Cache.refreshAll();
        } catch (Exception e) {
            getLogger().warning("§c加载数据库时出错");
            e.printStackTrace();
        }
        //Listeners
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        //Commands
        getServer().getPluginCommand("login").setExecutor(new CommandLogin());
        getServer().getPluginCommand("login").setTabCompleter((commandSender, command, s, args)
                -> args.length == 1 ? Collections.singletonList("密码") : new ArrayList<>(0));

        getServer().getPluginCommand("register").setExecutor(new CommandRegister());
        getServer().getPluginCommand("register").setTabCompleter((commandSender, command, s, args)
                -> args.length == 1 ? Collections.singletonList("密码 重复密码") : new ArrayList<>(0));

        getServer().getPluginCommand("changepassword").setExecutor(new CommandChangePassword());
        getServer().getPluginCommand("changepassword").setTabCompleter((commandSender, command, s, args)
                -> args.length == 1 ? Collections.singletonList("旧密码 新密码 重复新密码") : new ArrayList<>(0));

        getServer().getPluginCommand("adminsetpassword").setExecutor(new CommandAdminSetPassword());

        PluginCommand bindemail = getServer().getPluginCommand("bindemail");
        bindemail.setExecutor(new CommandBindEmail());
        bindemail.setTabCompleter((commandSender, command, s, args) -> {
            if (args.length == 1) {
                return Arrays.asList("set 需要绑定的邮箱", "verify 邮箱验证码");
            }
            if (args.length == 2) {
                if (args[0].equals("set")) {
                    return Collections.singletonList("需要绑定的邮箱");
                }
                if (args[0].equals("verify")) {
                    return Collections.singletonList("邮箱获取的验证码");
                }
            }
            return Collections.emptyList();
        });
        PluginCommand resetpassword = getServer().getPluginCommand("resetpassword");
        resetpassword.setExecutor(new CommandResetPassword());
        resetpassword.setTabCompleter((commandSender, command, s, args) -> {
            if (args.length == 1) {
                return Arrays.asList("forget", "re 验证码 新密码");
            }
            if (args[0].equals("re")) {
                if (args.length == 2) {
                    return Collections.singletonList("验证码 新密码");
                }
                if (args.length == 3) {
                    return Collections.singletonList("新密码");
                }
            }
            return Collections.emptyList();
        });


        //Task
        getServer().getScheduler().runTaskTimer(this, () -> {
            if (!Cache.isLoaded) return;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!LoginPlayerHelper.isLogin(player.getName())) {
                    if (!LoginPlayerHelper.isRegister(player.getName())) {
                        player.sendMessage("§a你还没有注册,请输入§e/reg 密码 重复密码 §a来注册");
                        continue;
                    }
                    player.sendMessage("§a请输入§e/l 密码 §a来登录游戏");
                    player.sendMessage("§b如果你从未注册,请换个游戏名!");

                }
            }
        }, 0, 20 * 5);


    }

    @Override
    public void onDisable(){
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!LoginPlayerHelper.isLogin(p.getName())) return;
            Config.setOfflineLocation(p);

        });
        try {
            sql.getConnection().close();
        } catch (Exception e) {
            getLogger().warning("获取数据库连接时出错");
            e.printStackTrace();
        }
        super.onDisable();
    }

    public static CatSeedLogin getInstance(){
        return instance;
    }

}
