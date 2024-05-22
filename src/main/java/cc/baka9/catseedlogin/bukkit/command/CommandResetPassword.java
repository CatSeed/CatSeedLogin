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

public class CommandResetPassword implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
        if (args.length != 0 && sender instanceof Player player) {
            String name = player.getName();
            if (Config.Settings.BedrockLoginBypass && LoginPlayerHelper.isFloodgatePlayer(player)) {
                return true;
            }
            LoginPlayer lp = Cache.getIgnoreCase(name);

            if (lp == null) {
                sender.sendMessage(Config.Language.RESETPASSWORD_NOREGISTER);
                return true;
            }
            if (!Config.EmailVerify.Enable) {
                sender.sendMessage(Config.Language.RESETPASSWORD_EMAIL_DISABLE);
                return true;
            }
            //command forget
            if (args[0].equalsIgnoreCase("forget")) {
                if (lp.getEmail() == null) {
                    sender.sendMessage(Config.Language.RESETPASSWORD_EMAIL_NO_SET);
                } else {
                    Optional<EmailCode> optionalEmailCode = EmailCode.getByName(name, EmailCode.Type.ResetPassword);
                    if (optionalEmailCode.isPresent()) {
                        sender.sendMessage(Config.Language.RESETPASSWORD_EMAIL_REPEAT_SEND_MESSAGE.replace("{email}", optionalEmailCode.get().getEmail()));
                    } else {
                        //20分钟有效期的验证码
                        EmailCode emailCode = EmailCode.create(name, lp.getEmail(), 1000 * 60 * 20, EmailCode.Type.ResetPassword);
                        sender.sendMessage(Config.Language.RESETPASSWORD_EMAIL_SENDING_MESSAGE.replace("{email}", lp.getEmail()));
                        CatSeedLogin.instance.runTaskAsync(() -> {
                            try {
                                if (emailCode != null) {
                                    Mail.sendMail(emailCode.getEmail(), "重置密码",
                                            "你的验证码是 <strong>" + emailCode.getCode() + "</strong>" +
                                                    "<br/>在服务器中使用帐号 " + name + " 输入指令<strong>/resetpassword re " + emailCode.getCode() + " 新密码</strong> 来重置新密码" +
                                                    "<br/>此验证码有效期为 " + (emailCode.getDurability() / (1000 * 60)) + "分钟");
                                }
                                Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                                    if (emailCode != null) {
                                        sender.sendMessage(Config.Language.RESETPASSWORD_EMAIL_SENT_MESSAGE.replace("{email}", emailCode.getEmail()));
                                    }
                                });
                            } catch (Exception e) {
                                Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> sender.sendMessage(Config.Language.RESETPASSWORD_EMAIL_WARN));
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
                    sender.sendMessage(Config.Language.RESETPASSWORD_EMAIL_NO_SET);
                } else {
                    Optional<EmailCode> optionalEmailCode = EmailCode.getByName(name, EmailCode.Type.ResetPassword);
                    if (optionalEmailCode.isPresent()) {
                        EmailCode emailCode = optionalEmailCode.get();
                        String code = args[1], pwd = args[2];

                        if (emailCode.getCode().equals(code)) {
                            if (Util.passwordIsDifficulty(pwd)) {
                                sender.sendMessage(Config.Language.COMMON_PASSWORD_SO_SIMPLE);
                                return true;
                            }
                            sender.sendMessage("§e密码重置中..");
                            CatSeedLogin.instance.runTaskAsync(() -> {
                                lp.setPassword(pwd);
                                lp.crypt();
                                try {
                                    CatSeedLogin.sql.edit(lp);
                                    LoginPlayerHelper.remove(lp);
                                    EmailCode.removeByName(name, EmailCode.Type.ResetPassword);
                                    Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                                        Player p = Bukkit.getPlayer(lp.getName());
                                        if (p != null && p.isOnline()) {
                                            if (Config.Settings.CanTpSpawnLocation) {
    //                                            PlayerTeleport.teleport(p, Config.Settings.SpawnLocation);
                                                p.teleport(Config.Settings.SpawnLocation);
                                            }
                                            p.sendMessage(Config.Language.RESETPASSWORD_SUCCESS);
                                            if (CatSeedLogin.loadProtocolLib) {
                                                LoginPlayerHelper.sendBlankInventoryPacket(player);
                                            }
                                        }

                                    });
                                } catch (Exception e) {
                                    Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> sender.sendMessage("§c数据库异常!"));
                                    e.printStackTrace();
                                }


                            });
                        } else {
                            sender.sendMessage(Config.Language.RESETPASSWORD_EMAILCODE_INCORRECT);
                        }

                    } else {
                        sender.sendMessage(Config.Language.RESETPASSWORD_FAIL);
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
