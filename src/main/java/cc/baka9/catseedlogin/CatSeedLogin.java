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
        instance = this;
        //Config
        try{
            Config.load();
            Config.save();
        }catch (Exception e){
            e.printStackTrace();
            getServer().getLogger().info("加载配置文件时出错，请检查你的配置文件。");
        }
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
        PluginCommand catseedlogin = getServer().getPluginCommand("catseedlogin");
        catseedlogin.setExecutor(new CommandCatSeedLogin());
//        catseedlogin.setTabCompleter((commandSender, command, s, args) -> {
//            if (args.length == 1) {
//                return Collections.singletonList("reload 重载配置文件");
//            }
//            return Collections.emptyList();
//        });


        //Task
        getServer().getScheduler().runTaskTimer(this, () -> {
            if (!Cache.isLoaded) return;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!LoginPlayerHelper.isLogin(player.getName())) {
                    if (!LoginPlayerHelper.isRegister(player.getName())) {
                        player.sendMessage(Config.Language.REGISTER_REQUEST);
                        continue;
                    }
                    player.sendMessage(Config.Language.LOGIN_REQUEST);

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
