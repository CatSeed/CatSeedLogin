package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.util.Util;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAdminSetPassword implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
        if (args.length != 2) return false;
        String name = args[0], pwd = args[1];
        if (!Util.passwordIsDifficulty(pwd)) {
            sender.sendMessage("§c密码必须是6~16位之间的数字和字母组成");
            return true;
        }
        sender.sendMessage("§e设置中..");
        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
            LoginPlayer lp = Cache.getIgnoreCase(name);
            if (lp == null) {
                lp = new LoginPlayer(name, pwd);
                lp.crypt();
                try {
                    CatSeedLogin.sql.add(lp);
                    sender.sendMessage("§a指定账户不存在,现已注册..");
                } catch (Exception e) {
                    sender.sendMessage("§c数据库异常!");
                    e.printStackTrace();
                }
            } else {
                lp.setPassword(pwd);
                lp.crypt();
                try {
                    CatSeedLogin.sql.edit(lp);
                    sender.sendMessage(String.join(" ", "§a玩家", lp.getName(), "密码已设置"));
                    LoginPlayer finalLp = lp;
                    Bukkit.getScheduler().runTask(CatSeedLogin.getInstance(), () -> {
                        Player p = Bukkit.getPlayer(finalLp.getName());
                        if (p != null && p.isOnline()) {
                            p.sendMessage("§c密码已被管理员重新设置,请重新登录");
                            p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                            LoginPlayerHelper.remove(finalLp);
                        }

                    });
                } catch (Exception e) {
                    sender.sendMessage("§c数据库异常!");
                    e.printStackTrace();
                }
            }


        });

        return true;
    }
}
