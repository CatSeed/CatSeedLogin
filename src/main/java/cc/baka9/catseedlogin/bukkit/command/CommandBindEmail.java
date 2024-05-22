package cc.baka9.catseedlogin.bukkit.command;

import cc.baka9.catseedlogin.bukkit.CatSeedLogin;
import cc.baka9.catseedlogin.bukkit.Config;
import cc.baka9.catseedlogin.bukkit.database.Cache;
import cc.baka9.catseedlogin.bukkit.object.EmailCode;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayer;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Mail;
import cc.baka9.catseedlogin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;


public class CommandBindEmail implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
        if (args.length != 0 && sender instanceof Player player) {
            String name = player.getName();
            if (Config.Settings.BedrockLoginBypass && LoginPlayerHelper.isFloodgatePlayer(player)) {
                return true;
            }
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
                sender.sendMessage("§c服务器没有开启邮箱功能");
                return true;
            }

            // command set email
            if (args[0].equalsIgnoreCase("set") && args.length > 1) {
                if (lp.getEmail() != null && Util.checkMail(lp.getEmail())) {
                    sender.sendMessage("§c你已经绑定过邮箱了!");
                } else {
                    String mail = args[1];
                    Optional<EmailCode> bindEmailOptional = EmailCode.getByName(name, EmailCode.Type.Bind);
                    if (bindEmailOptional.isPresent() && bindEmailOptional.get().getEmail().equals(mail)) {
                        sender.sendMessage("§c已经向 " + mail + " 邮箱中发送验证码，请不要重复此操作");

                    } else if (Util.checkMail(mail)) {
                        //创建有效期为20分钟的验证码
                        EmailCode bindEmail = EmailCode.create(name, mail, 1000 * 60 * 20, EmailCode.Type.Bind);
                        sender.sendMessage("§6向邮箱发送验证码中...");
                        CatSeedLogin.instance.runTaskAsync(() -> {
                            try {
                                if (bindEmail != null) {
                                    Mail.sendMail(mail, "邮箱绑定",
                                            "你的验证码是 <strong>" + bindEmail.getCode() + "</strong>" +
                                                    "<br/>在服务器中使用帐号 " + name + " 输入指令<strong>/bindemail verify " + bindEmail.getCode() + "</strong> 来绑定邮箱" +
                                                    "<br/>绑定邮箱之后可用于忘记密码时重置自己的密码" +
                                                    "<br/>此验证码有效期为 " + (bindEmail.getDurability() / (1000 * 60)) + "分钟");
                                }
                                Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                                    sender.sendMessage("§6已经向邮箱 " + mail + " 发送了一串绑定验证码，请检查你的邮箱的收件箱");
                                    sender.sendMessage("§c如果未收到，请检查邮箱的垃圾箱!");
                                });
                            } catch (Exception e) {
                                Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> sender.sendMessage("§c发送邮件失败,服务器内部错误!"));
                                e.printStackTrace();
                            }
                        });


                    } else {
                        sender.sendMessage("§c邮箱格式不正确!");
                    }
                }
                return true;
            }

            // command verify code
            if (args[0].equalsIgnoreCase("verify") && args.length > 1) {
                if (lp.getEmail() != null && Util.checkMail(lp.getEmail())) {
                    sender.sendMessage("§c你已经绑定过邮箱了!");
                } else {
                    Optional<EmailCode> emailOptional = EmailCode.getByName(name, EmailCode.Type.Bind);
                    if (emailOptional.isPresent()) {
                        EmailCode bindEmail = emailOptional.get();
                        String code = args[1];
                        if (bindEmail.getCode().equals(code)) {
                            sender.sendMessage("§e绑定邮箱中..");
                            CatSeedLogin.instance.runTaskAsync(() -> {
                                try {
                                    lp.setEmail(bindEmail.getEmail());
                                    CatSeedLogin.sql.edit(lp);
                                    Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                                        Player syncPlayer = Bukkit.getPlayer(((Player) sender).getUniqueId());
                                        if (syncPlayer != null && syncPlayer.isOnline()) {
                                            syncPlayer.sendMessage("§a邮箱已绑定 " + bindEmail.getEmail() + " 忘记密码时可以用邮箱重置自己的密码");
                                            EmailCode.removeByName(name, EmailCode.Type.Bind);
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
                        sender.sendMessage("§c你没有待绑定的邮箱，或者验证码已过期");
                    }


                }
                return true;
            }

            return true;
        } else {
            return false;
        }
    }
}
