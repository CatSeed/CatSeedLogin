package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.event.CatSeedPlayerRegisterEvent;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandRegister implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
        if (args.length != 2) return false;

        String name = sender.getName();
        if (LoginPlayerHelper.isLogin(name)) {
            sender.sendMessage(Config.Language.REGISTER_AFTER_LOGIN_ALREADY);
            return true;
        }
        if (Cache.getIgnoreCase(name) != null) {
            sender.sendMessage(Config.Language.REGISTER_BEFORE_LOGIN_ALREADY);
            return true;
        }
        if (!args[0].equals(args[1])) {
            sender.sendMessage(Config.Language.REGISTER_PASSWORD_CONFIRM_FAIL);
            return true;
        }
        if (!Util.passwordIsDifficulty(args[0])) {
            sender.sendMessage(Config.Language.COMMON_PASSWORD_SO_SIMPLE);
            return true;
        }
        if (!Cache.isLoaded) {
            return true;
        }
        sender.sendMessage("§e注册中..");
        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
            try {
                LoginPlayer lp = new LoginPlayer(name, args[0]);
                lp.crypt();
                CatSeedLogin.sql.add(lp);
                LoginPlayerHelper.add(lp);
                Bukkit.getScheduler().runTask(CatSeedLogin.getInstance(), () -> {
                    CatSeedPlayerRegisterEvent event = new CatSeedPlayerRegisterEvent(Bukkit.getPlayer(sender.getName()));
                    Bukkit.getServer().getPluginManager().callEvent(event);
                });
                sender.sendMessage(Config.Language.REGISTER_SUCCESS);


            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("§c服务器内部错误!");
            }
        });
        return true;

    }
}
