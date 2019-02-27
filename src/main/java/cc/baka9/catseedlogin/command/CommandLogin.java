package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Crypt;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class CommandLogin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String lable, String[] args){
        if (args.length == 0) return false;
        String name = commandSender.getName();
        if (LoginPlayerHelper.isLogin(name)) {
            commandSender.sendMessage("§c你已经登录了,不需要再次登录");
            return true;
        }
        LoginPlayer lp = Cache.get(name);
        if (lp == null) {
            commandSender.sendMessage("§c请先注册!");
            return true;
        }
        if (Objects.equals(Crypt.encrypt(name, args[0]), lp.getPassword().trim())) {
            LoginPlayerHelper.add(lp);
            commandSender.sendMessage("§a已成功登录!");

        } else {
            commandSender.sendMessage("§c密码错误!");
        }
        return true;
    }
}
