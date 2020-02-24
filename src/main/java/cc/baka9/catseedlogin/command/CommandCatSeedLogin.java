package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.database.MySQL;
import cc.baka9.catseedlogin.database.SQLite;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandCatSeedLogin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String lable, String[] args){
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Config.reload();
            CatSeedLogin.sql = Config.MySQL.Enable ? new MySQL(CatSeedLogin.getInstance()) : new SQLite(CatSeedLogin.getInstance());
            try {

                CatSeedLogin.sql.init();

                Cache.refreshAll();
            } catch (Exception e) {
                CatSeedLogin.getInstance().getLogger().warning("§c加载数据库时出错");
                e.printStackTrace();
            }
            commandSender.sendMessage("配置已重载!");
            return true;
        }
        return false;
    }
}
