package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.EmailCode;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Mail;
import cc.baka9.catseedlogin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CommandResetPassword implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
        if (args.length == 0 || !(sender instanceof Player)) return false;
        Player player = (Player) sender;
        String name = player.getName();
        LoginPlayer lp = Cache.getIgnoreCase(name);

        if (lp == null) {
            sender.sendMessage("§c你还未注册!");
            return true;
        }
        if (!Config.EmailVerify.Enable) {
            sender.sendMessage("§c服务器没有开启邮箱功能");
            return true;
        }
        //command forget
        if (args[0].equalsIgnoreCase("forget")) {
            if (lp.getEmail() == null) {
                sender.sendMessage("§c没有设置过邮箱，无法使用此功能重设密码");
            } else {
                Optional<EmailCode> optionalEmailCode = EmailCode.getByName(name, EmailCode.Type.ResetPassword);
                if (optionalEmailCode.isPresent()) {
                    sender.sendMessage("§c已经向 " + optionalEmailCode.get().getEmail() + " 邮箱中发送验证码，请不要重复此操作");
                } else {
                    //20分钟有效期的验证码
                    EmailCode emailCode = EmailCode.create(name, lp.getEmail(), 1000 * 60 * 20, EmailCode.Type.ResetPassword);
                    sender.sendMessage("§6向邮箱发送验证码中...");
                    Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
                        try {
                            Mail.sendMail(emailCode.getEmail(), "重置密码",
                                    "你的验证码是 <strong>" + emailCode.getCode() + "</strong>" +
                                            "<br/>在服务器中使用帐号 " + name + " 输入指令<strong>/resetpassword re " + emailCode.getCode() + " 新密码</strong> 来重置新密码" +
                                            "<br/>此验证码有效期为 " + (emailCode.getDurability() / (1000 * 60)) + "分钟");
                            Bukkit.getScheduler().runTask(CatSeedLogin.getInstance(), () -> {
                                sender.sendMessage("§6已经向邮箱" + emailCode.getEmail() + "发送了一串绑定验证码，请检查你的邮箱的收件箱");
                                sender.sendMessage("§c如果未收到，请检查邮箱的垃圾箱!");
                            });
                        } catch (Exception e) {
                            Bukkit.getScheduler().runTask(CatSeedLogin.getInstance(), () -> sender.sendMessage("§c发送邮件失败,服务器内部错误!"));
                            e.printStackTrace();
                        }
                    });
                }
            }
            return true;
        }
        //command re
        if (args[0].equalsIgnoreCase("re") && args.length > 2) {
            if (lp.getEmail() == null) {
                sender.sendMessage("§c没有设置过邮箱，无法使用此功能重设密码");
            } else {
                Optional<EmailCode> optionalEmailCode = EmailCode.getByName(name, EmailCode.Type.ResetPassword);
                if (optionalEmailCode.isPresent()) {
                    EmailCode emailCode = optionalEmailCode.get();
                    String code = args[1], pwd = args[2];

                    if (emailCode.getCode().equals(code)) {
                        if (!Util.passwordIsDifficulty(pwd)) {
                            sender.sendMessage("§c密码必须是6~16位之间的数字和字母组成");
                            return true;
                        }
                        sender.sendMessage("§e密码重置中..");
                        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
                            lp.setPassword(pwd);
                            lp.crypt();
                            try {
                                CatSeedLogin.sql.edit(lp);
                                EmailCode.removeByName(name, EmailCode.Type.ResetPassword);
                                Bukkit.getScheduler().runTask(CatSeedLogin.getInstance(), () -> {
                                    Player p = Bukkit.getPlayer(lp.getName());
                                    if (p != null && p.isOnline()) {
                                        if (LoginPlayerHelper.isLogin(name)) {
                                            p.sendMessage("§c密码已重置，请重新登陆");
                                            p.teleport(Bukkit.getWorld(Config.Settings.spawnWorld).getSpawnLocation());
                                            LoginPlayerHelper.remove(lp);

                                        } else {
                                            p.sendMessage("§c密码已重置");
                                        }
                                    }

                                });
                            } catch (Exception e) {
                                Bukkit.getScheduler().runTask(CatSeedLogin.getInstance(), () -> sender.sendMessage("§c数据库异常!"));
                                e.printStackTrace();
                            }


                        });
                    } else {
                        sender.sendMessage("§c验证码错误!");
                    }

                } else {
                    sender.sendMessage("§c你没有待重置密码的请求操作，或者验证码已过期");
                    sender.sendMessage("§6如果忘记密码需要重置密码请输入/resetpassword forget");
                }
            }
            return true;
        }
        return true;
    }
}
