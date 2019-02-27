package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Util;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandRegister implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String lable, String[] args){
        if (args.length != 2) return false;

        String name = commandSender.getName();
        if (LoginPlayerHelper.isLogin(name)) {
            commandSender.sendMessage("§a你已经注册过了,需要修改密码请输入 §e/changepw 旧密码 新密码 重复新密码");
            return true;
        }
        if (Cache.get(name) != null) {
            commandSender.sendMessage("§c注册失败!这个游戏名已经被人注册了,如果不是你注册的请换个游戏名吧!");
            return true;
        }
        if (!args[0].equals(args[1])) {
            commandSender.sendMessage("§c你两次输入的密码不一样!");
            return true;
        }
        if (!Util.passwordIsDifficulty(args[0])) {
            commandSender.sendMessage("§c密码太简单啦!");
            return true;
        }
        if (!Cache.isLoaded) {
            return true;
        }
        commandSender.sendMessage("§e注册中..");
        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
            try {
                LoginPlayer lp = new LoginPlayer(name, args[0]);
                lp.crypt();
                CatSeedLogin.sql.add(lp);
                LoginPlayerHelper.add(lp);
                commandSender.sendMessage("§a注册成功!");


            } catch (Exception e) {
                e.printStackTrace();
                commandSender.sendMessage("§c服务器内部错误!");
            }
        });
        return true;

    }
}
