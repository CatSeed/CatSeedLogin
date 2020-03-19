package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.event.CatSeedPlayerLoginEvent;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Crypt;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class CommandLogin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
        if (args.length == 0 || !(sender instanceof Player)) return false;
        Player player = (Player) sender;
        String name = player.getName();
        if (LoginPlayerHelper.isLogin(name)) {
            sender.sendMessage("§c你已经登录了,不需要再次登录");
            return true;
        }
        LoginPlayer lp = Cache.getIgnoreCase(name);
        if (lp == null) {
            sender.sendMessage("§c请先注册!");
            return true;
        }
        if (Objects.equals(Crypt.encrypt(name, args[0]), lp.getPassword().trim())) {
            LoginPlayerHelper.add(lp);
            CatSeedPlayerLoginEvent loginEvent = new CatSeedPlayerLoginEvent(player, lp.getEmail(), CatSeedPlayerLoginEvent.Result.SUCCESS);
            Bukkit.getServer().getPluginManager().callEvent(loginEvent);
            sender.sendMessage("§a已成功登录!");
            if (Config.Settings.AfterLoginBack) {
                player.teleport(Config.getOfflineLocation(player));
            }
        } else {
            sender.sendMessage("§c密码错误!");
            CatSeedPlayerLoginEvent loginEvent = new CatSeedPlayerLoginEvent(player, lp.getEmail(), CatSeedPlayerLoginEvent.Result.FAIL);
            Bukkit.getServer().getPluginManager().callEvent(loginEvent);
            if (Config.EmailVerify.Enable) {
                sender.sendMessage("§c密码错误,如果忘记密码需要重置密码请输入/resetpassword forget");
            }
        }
        return true;
    }
}
