package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.Crypt;
import cc.baka9.catseedlogin.Util;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandChangePassword implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
        if (args.length != 3 || !(sender instanceof Player)) {
            return false;
        }
        String name = sender.getName();
        LoginPlayer lp = Cache.getIgnoreCase(name);
        if (lp == null) {
            sender.sendMessage("§c你还未注册无法修改密码");
            return true;
        }
        if (!LoginPlayerHelper.isLogin(name)) {
            sender.sendMessage("§c你还未登陆无法修改密码");
            return true;
        }
        if (!Objects.equals(Crypt.encrypt(name, args[0]), lp.getPassword().trim())) {
            sender.sendMessage("§c旧密码输入错误!");
            return true;

        }
        if (!args[1].equals(args[2])) {
            sender.sendMessage("§c你两次输入的密码不一样!");
            return true;
        }
        if (!Util.passwordIsDifficulty(args[1])) {
            sender.sendMessage("§c密码必须是6~16位之间的数字和字母组成");
            return true;
        }
        if (!Cache.isLoaded) {
            return true;
        }
        sender.sendMessage("§e修改中..");
        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
            try {
                lp.setPassword(args[1]);
                lp.crypt();
                CatSeedLogin.sql.edit(lp);
                LoginPlayerHelper.remove(lp);

                Bukkit.getScheduler().runTask(CatSeedLogin.getInstance(), () -> {
                    Player player = Bukkit.getPlayer(((Player) sender).getUniqueId());
                    if (player != null && player.isOnline()) {
                        player.sendMessage("§a修改成功! 请重新登录~");
                        Config.setOfflineLocation(player);
                        player.teleport(Bukkit.getWorld("world").getSpawnLocation());

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("§c服务器内部错误!");
            }
        });
        return true;
    }
}
