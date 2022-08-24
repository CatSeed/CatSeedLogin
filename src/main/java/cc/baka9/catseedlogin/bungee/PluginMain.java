package cc.baka9.catseedlogin.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

/**
 * Bungee Cord 主类
 */
public class PluginMain extends Plugin {
    public static PluginMain instance;

    @Override
    public void onEnable() {
        instance = this;
        Config.load();
        getProxy().getPluginManager().registerListener(this, new Listeners());
        getProxy().getPluginManager().registerCommand(this, new Commands("CatSeedLoginBungee", "catseedlogin.admin", "cslb"));

    }

    public static ScheduledTask runAsync(Runnable runnable) {
        return instance.getProxy().getScheduler().runAsync(instance, runnable);
    }

}
