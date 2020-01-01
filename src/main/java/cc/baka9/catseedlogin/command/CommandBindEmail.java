package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Mail;
import cc.baka9.catseedlogin.util.Util;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


public class CommandBindEmail implements CommandExecutor {

    @AllArgsConstructor
    private static class BindEmail {
        private String email;
        private String code;
    }

    private Map<String, BindEmail> bindEmails = new HashMap<>(10);

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
        if (!LoginPlayerHelper.isLogin(name)) {
            sender.sendMessage("§c你还未登陆!");
            return true;
        }
        if (!Config.EmailVerify.Enable) {
            sender.sendMessage("§c服务器没有开启邮箱绑定功能");
            return true;
        }

        // command set email
        if (args[0].equalsIgnoreCase("set") && args.length > 1) {
            if (lp.getEmail() != null) {
                sender.sendMessage("§c你已经绑定过邮箱了!");
            } else {
                String mail = args[1];
                if (Util.checkMail(mail)) {
                    if (bindEmails.containsKey(name) && bindEmails.get(name).email.equals(mail)) {
                        sender.sendMessage("§c已经向 "+ mail + " 邮箱中发送验证码，请不要重复此操作");
                    } else {
                        String code = Util.randomStr();
                        bindEmails.put(name, new BindEmail(mail, code));
                        sender.sendMessage("§6向邮箱发送验证码中...");
                        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
                            try {
                                Mail.sendMail(mail, "邮箱绑定",
                                        "你的验证码是 <strong>" + code + "</strong>" +
                                                "<br/>在服务器中使用帐号 " + name + " 输入指令<strong>/bindemail verify " + code + "</strong> 来绑定邮箱" +
                                                "<br/>绑定邮箱之后可用于忘记密码时重置自己的密码");
                                Bukkit.getScheduler().runTask(CatSeedLogin.getInstance(), () -> {
                                    sender.sendMessage("§6已经向你的邮箱发送了一串绑定验证码，请检查你的邮箱的收件箱");
                                    sender.sendMessage("§c如果未收到，请检查邮箱的垃圾箱!");
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                } else {
                    sender.sendMessage("§c邮箱格式不正确!");
                }
            }
            return true;
        }

        // command verify code
        if (args[0].equalsIgnoreCase("verify") && args.length > 1) {
            if (lp.getEmail() != null) {
                sender.sendMessage("§c你已经绑定过邮箱了!");
            } else {

                if (bindEmails.containsKey(name)) {

                    String code = args[1];
                    BindEmail bindEmail = this.bindEmails.get(name);
                    if (bindEmail.code.equals(code)) {

                        sender.sendMessage("§e绑定邮箱中..");

                        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.getInstance(), () -> {
                            try {
                                lp.setEmail(bindEmail.email);
                                CatSeedLogin.sql.edit(lp);
                                Bukkit.getScheduler().runTask(CatSeedLogin.getInstance(), () -> {
                                    Player syncPlayer = Bukkit.getPlayer(((Player) sender).getUniqueId());
                                    if (syncPlayer != null && syncPlayer.isOnline()) {
                                        syncPlayer.sendMessage("§a邮箱已绑定 " + bindEmail.email + " 忘记密码时可以用邮箱重置自己的密码");
                                        bindEmails.remove(name);
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                                sender.sendMessage("§c服务器内部错误!");
                            }

                        });

                    } else {
                        sender.sendMessage("§c验证码错误!");
                    }

                } else {
                    sender.sendMessage("§c你没有设置过绑定邮箱!");
                }


            }
            //TODO
            return true;
        }

        return true;
    }
}
