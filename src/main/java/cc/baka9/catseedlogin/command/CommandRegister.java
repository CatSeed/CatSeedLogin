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
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class CommandRegister implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
        if (args.length != 2) return false;

        String name = sender.getName();
        if (LoginPlayerHelper.isLogin(name)) {
            sender.sendMessage("§a你已经注册过了,需要修改密码请输入 §e/changepw 旧密码 新密码 重复新密码");
            return true;
        }
        if (Cache.getIgnoreCase(name) != null) {
            sender.sendMessage("§c注册失败!这个游戏名已经被人注册了,如果不是你注册的请换个游戏名吧!");
            return true;
        }
        if (!args[0].equals(args[1])) {
            sender.sendMessage("§c你两次输入的密码不一样!");
            return true;
        }
        if (!Util.passwordIsDifficulty(args[0])) {
            sender.sendMessage("§c密码必须是6~16位之间的数字和字母组成");
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
                CatSeedPlayerRegisterEvent event = new CatSeedPlayerRegisterEvent((Player) sender);
                Bukkit.getServer().getPluginManager().callEvent(event);
                sender.sendMessage("§a注册成功!");


            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("§c服务器内部错误!");
            }
        });
        return true;

    }
}
