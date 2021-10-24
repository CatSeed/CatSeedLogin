package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.database.MySQL;
import cc.baka9.catseedlogin.database.SQLite;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandCatSeedLogin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
        return reload(sender, args)
                || setPwd(sender, args)
                || delPlayer(sender, args)
                || setIpCountLimit(sender, args)
                || limitChineseID(sender, args)
                || setIdLength(sender, args)
                || beforeLoginNoDamage(sender, args)
                || setReenterInterval(sender, args)
                || afterLoginBack(sender, args)
                || setSpawnLocation(sender, args)
                || commandWhiteListInfo(sender, args)
                || commandWhiteListAdd(sender, args)
                || commandWhiteListDel(sender, args)
                || canTpSpawnLocation(sender, args)
                || autoKick(sender, args)
                || setIpRegCountLimit(sender, args)
                || deathStateQuitRecordLocation(sender, args);
    }

    private boolean deathStateQuitRecordLocation(CommandSender sender, String[] args){
        if (args.length > 0 && args[0].equalsIgnoreCase("deathStateQuitRecordLocation")) {
            Config.Settings.DeathStateQuitRecordLocation = !Config.Settings.DeathStateQuitRecordLocation;
            Config.Settings.save();
            sender.sendMessage("§e死亡状态退出游戏记录退出位置" + (Config.Settings.DeathStateQuitRecordLocation ? "§a开启" : "§8关闭"));
            return true;
        }
        return false;
    }

    private boolean autoKick(CommandSender sender, String[] args){
        if (args.length > 1 && args[0].equalsIgnoreCase("setAutoKick")) {
            try {

                Config.Settings.AutoKick = Integer.parseInt(args[1]);
                Config.Settings.save();
                sender.sendMessage(Config.Settings.AutoKick > 0 ? "§e已设置未登录自动踢出累计时间为 §a" + Config.Settings.AutoKick + "秒" : "§e已关闭未登录自动踢出");
            } catch (NumberFormatException e) {
                sender.sendMessage("§e秒数必须是一个数字");
            }
            return true;
        }
        return false;
    }

    private boolean canTpSpawnLocation(CommandSender sender, String[] args){
        if (args.length > 0 && args[0].equalsIgnoreCase("canTpSpawnLocation")) {
            Config.Settings.CanTpSpawnLocation = !Config.Settings.CanTpSpawnLocation;
            Config.Settings.save();
            sender.sendMessage("§e登录之前强制在登陆地点 " + (Config.Settings.CanTpSpawnLocation ? "§a开启" : "§8关闭"));
            return true;
        }
        return false;
    }

    private boolean commandWhiteListDel(CommandSender sender, String[] args){
        if (args.length > 1 && args[0].equalsIgnoreCase("commandWhiteListDel")) {
            String[] cmd = new String[args.length - 1];
            System.arraycopy(args, 1, cmd, 0, cmd.length);
            String regex = String.join(" ", cmd);
            List<String> collect = Config.Settings.CommandWhiteList.stream().map(Pattern::toString).collect(Collectors.toList());
            if (collect.contains(regex)) {
                collect.remove(regex);
                Config.Settings.CommandWhiteList = collect.stream().map(Pattern::compile).collect(Collectors.toList());
                Config.Settings.save();
                sender.sendMessage("§e已删除登录前可执行指令 " + regex);
            } else {
                sender.sendMessage("§c不存在 " + regex);
            }
            return true;
        }
        return false;
    }

    private boolean commandWhiteListAdd(CommandSender sender, String[] args){
        if (args.length > 1 && args[0].equalsIgnoreCase("commandWhiteListAdd")) {
            String[] cmd = new String[args.length - 1];
            System.arraycopy(args, 1, cmd, 0, cmd.length);
            String regex = String.join(" ", cmd);
            Pattern pattern = Pattern.compile(regex);
            List<String> collect = Config.Settings.CommandWhiteList.stream().map(Pattern::toString).collect(Collectors.toList());
            if (collect.contains(regex)) {
                sender.sendMessage("§c已经存在 " + regex);
            } else {
                Config.Settings.CommandWhiteList.add(pattern);
                Config.Settings.save();
                sender.sendMessage("§e已添加登录前可执行指令 " + regex);
            }
            return true;
        }
        return false;
    }

    private boolean commandWhiteListInfo(CommandSender sender, String[] args){
        if (args.length > 0 && args[0].equalsIgnoreCase("commandWhiteListInfo")) {
            sender.sendMessage("§e登录前可执行指令: ");
            Config.Settings.CommandWhiteList.forEach(cmdRegex -> sender.sendMessage(cmdRegex.toString()));
            return true;
        }
        return false;
    }

    private boolean setSpawnLocation(CommandSender sender, String[] args){
        if (args.length > 0 && args[0].equalsIgnoreCase("setSpawnLocation")) {
            if (sender instanceof Player) {
                Config.Settings.SpawnLocation = ((Player) sender).getLocation();
                Config.Settings.save();
                sender.sendMessage("§e已设置玩家登陆坐标为你站着的位置");
            } else {
                sender.sendMessage("§c不能在控制台使用这个指令");
            }
            return true;
        }
        return false;
    }

    private boolean afterLoginBack(CommandSender sender, String[] args){
        if (args.length > 0 && args[0].equalsIgnoreCase("afterLoginBack")) {
            Config.Settings.AfterLoginBack = !Config.Settings.AfterLoginBack;
            Config.Settings.save();
            sender.sendMessage("§e登陆之后返回下线地点 " + (Config.Settings.AfterLoginBack ? "§a开启" : "§8关闭"));
            return true;
        }
        return false;
    }

    private boolean setReenterInterval(CommandSender sender, String[] args){
        if (args.length > 1 && args[0].equalsIgnoreCase("setReenterInterval")) {
            try {
                Config.Settings.ReenterInterval = Long.valueOf(args[1]);
                Config.Settings.save();
                sender.sendMessage("§e离开服务器重新进入的间隔限制 " + Config.Settings.ReenterInterval + "tick");

            } catch (NumberFormatException e) {
                sender.sendMessage("§c请输入一个数字");
            }

            return true;
        }
        return false;

    }

    private boolean beforeLoginNoDamage(CommandSender sender, String[] args){
        if (args.length > 0 && args[0].equalsIgnoreCase("beforeLoginNoDamage")) {
            Config.Settings.BeforeLoginNoDamage = !Config.Settings.BeforeLoginNoDamage;
            Config.Settings.save();
            sender.sendMessage("§e登陆之前不受到伤害 " + (Config.Settings.BeforeLoginNoDamage ? "§a开启" : "§8关闭"));
            return true;
        }
        return false;

    }

    private boolean setIdLength(CommandSender sender, String[] args){
        if (args.length > 2 && args[0].equalsIgnoreCase("setIdLength")) {

            try {
                Config.Settings.MinLengthID = Integer.valueOf(args[1]);
                Config.Settings.MaxLengthID = Integer.valueOf(args[2]);
                Config.Settings.save();
                sender.sendMessage("§e游戏名最小和最大长度为 " + Config.Settings.MinLengthID + " ~ " + Config.Settings.MaxLengthID);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c请输入数字");
            }
            return true;
        }
        return false;

    }

    private boolean limitChineseID(CommandSender sender, String[] args){
        if (args.length > 0 && args[0].equalsIgnoreCase("limitChineseID")) {
            Config.Settings.LimitChineseID = !Config.Settings.LimitChineseID;
            Config.Settings.save();
            sender.sendMessage("§e限制中文游戏名 " + (Config.Settings.LimitChineseID ? "§a开启" : "§8关闭"));
            return true;
        }
        return false;
    }

    private boolean setIpCountLimit(CommandSender sender, String[] args){
        if (args.length > 1 && args[0].equalsIgnoreCase("setIpCountLimit")) {
            try {
                Config.Settings.IpCountLimit = Integer.valueOf(args[1]);
                Config.Settings.save();
                sender.sendMessage("§e相同ip登录限制数量为 " + Config.Settings.IpCountLimit);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c请输入数字");
            }
            return true;
        }
        return false;
    }

    private boolean setIpRegCountLimit(CommandSender sender, String[] args){
        if (args.length > 1 && args[0].equalsIgnoreCase("setIpRegCountLimit")) {
            try {
                Config.Settings.IpRegisterCountLimit = Integer.valueOf(args[1]);
                Config.Settings.save();
                sender.sendMessage("§e相同ip注册限制数量为 " + Config.Settings.IpRegisterCountLimit);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c请输入数字");
            }
            return true;
        }
        return false;
    }

    private boolean delPlayer(CommandSender sender, String[] args){
        if (args.length > 1 && args[0].equalsIgnoreCase("delplayer")) {

            String name = args[1];
            LoginPlayer lp = Cache.getIgnoreCase(name);
            if (lp != null) {
                CatSeedLogin.instance.runTaskAsync(() -> {
                    try {
                        CatSeedLogin.sql.del(lp.getName());
                        LoginPlayerHelper.remove(lp);
                        sender.sendMessage("§e已删除账户 §a" + lp.getName());
                        Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                            Player p = Bukkit.getPlayerExact(lp.getName());
                            if (p != null && p.isOnline()) {
                                p.kickPlayer("§c你的账户已被删除!");
                            }

                        });
                    } catch (Exception e) {
                        sender.sendMessage("§c数据库异常!");
                        e.printStackTrace();
                    }

                });

            } else {
                sender.sendMessage(String.format("§c账户 §a%s §c不存在", name));
            }

            return true;
        }
        return false;
    }

    private boolean setPwd(CommandSender sender, String[] args){
        if (args.length > 2 && args[0].equalsIgnoreCase("setpwd")) {

            String name = args[1], pwd = args[2];
            if (!Util.passwordIsDifficulty(pwd)) {
                sender.sendMessage("§c密码必须是6~16位之间的数字和字母组成");
                return true;
            }
            sender.sendMessage("§e设置中..");
            CatSeedLogin.instance.runTaskAsync(() -> {
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
                        LoginPlayerHelper.remove(lp);
                        sender.sendMessage(String.join(" ", "§a玩家", lp.getName(), "密码已设置"));
                        LoginPlayer finalLp = lp;
                        Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                            Player p = Bukkit.getPlayer(finalLp.getName());
                            if (p != null && p.isOnline()) {
                                p.sendMessage("§c密码已被管理员重新设置,请重新登录");
                                if (Config.Settings.CanTpSpawnLocation) {
                                    p.teleport(Config.Settings.SpawnLocation);
                                    if (CatSeedLogin.loadProtocolLib) {
                                        LoginPlayerHelper.sendBlankInventoryPacket(p);
                                    }
                                }
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
        return false;
    }

    private boolean reload(CommandSender sender, String[] args){
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Config.reload();
            CatSeedLogin.sql = Config.MySQL.Enable ? new MySQL(CatSeedLogin.instance) : new SQLite(CatSeedLogin.instance);
            try {

                CatSeedLogin.sql.init();

                Cache.refreshAll();
            } catch (Exception e) {
                CatSeedLogin.instance.getLogger().warning("§c加载数据库时出错");
                e.printStackTrace();
            }
            sender.sendMessage("配置已重载!");
            return true;
        }
        return false;
    }
}
