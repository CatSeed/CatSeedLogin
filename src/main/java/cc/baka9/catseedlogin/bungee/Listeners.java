package cc.baka9.catseedlogin.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Bungee Cord 监听事件类
 */
public class Listeners implements Listener {

    private final ProxyServer proxyServer = ProxyServer.getInstance();

    private final List<String> loggedInPlayerList = new ArrayList<>();

    /**
     * 登录之前不能输入bc指令
     */
    @EventHandler
    public void onChat(ChatEvent event) {
        Connection sender = event.getSender();
        if (!event.isProxyCommand() || !(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
        String message = event.getMessage();
        boolean loggedIn;
        String playerName = proxiedPlayer.getName();
        synchronized (loggedInPlayerList) {
            loggedIn = loggedInPlayerList.contains(playerName);
        }
        if (!loggedIn) {
            event.setCancelled(true);

            PluginMain.runAsync(() -> {
                if (Communication.sendConnectRequest(playerName) == 1) {
                    synchronized (loggedInPlayerList) {
                        loggedInPlayerList.add(playerName);
                    }
                    PluginMain.instance.getProxy().getPluginManager().dispatchCommand(proxiedPlayer, message.substring(1));
                }
            });
        }


    }

    /**
     * 玩家切换子服时，检查bc端该玩家的登录状态，
     * 如果没有登录，就请求登录服获取登录状态然后更新bc端该玩家的登录状态，
     * 如果登录服获取结果还是没有登录，就强制把切换目标服务器改为登录服
     */
    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ServerInfo target = event.getTarget();
        if (event.isCancelled() || target.getName().equals(Config.LoginServerName)) return;
        ProxiedPlayer player = event.getPlayer();
        boolean loggedIn;
        synchronized (loggedInPlayerList) {
            loggedIn = loggedInPlayerList.contains(player.getName());
        }
        if (!loggedIn) {
            PluginMain.runAsync(() -> {
                if (Communication.sendConnectRequest(player.getName()) == 1) {
                    synchronized (loggedInPlayerList) {
                        loggedInPlayerList.add(player.getName());
                        player.connect(target);
                    }
                }
            });

            event.setTarget(proxyServer.getServerInfo(Config.LoginServerName));


        }

    }


    /**
     * 玩家切换到登录服务之后，如果bc端是已登录的状态，就使用bc端的登录状态去更新子服的登录状态，
     * 避免使玩家每次切换到登录服时需要重新进行登录
     */
    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        if (event.getServer().getInfo().getName().equals(Config.LoginServerName)) {
            ProxiedPlayer player = event.getPlayer();
            String playerName = player.getName();

            PluginMain.runAsync(() -> {
                boolean loggedIn;
                synchronized (loggedInPlayerList) {
                    loggedIn = loggedInPlayerList.contains(playerName);
                }
                if (loggedIn) {
                    Communication.sendKeepLoggedInRequest(playerName);
                }

            });
        }

    }

    /**
     * 玩家离线时，删除玩家在bc端的登录状态
     */
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String playerName = player.getName();
        PluginMain.runAsync(() -> {
            synchronized (loggedInPlayerList) {
                loggedInPlayerList.remove(playerName);
            }
        });
    }

//    /**
//     * 玩家在登录之前，检查bc端和子服的登录状态，如果是其中一项是已登录，则禁止连接
//     */
//    @EventHandler
//    public void onPreLogin(PreLoginEvent event) {
//        TextComponent zh = new TextComponent("§c您已经有一个账号在线了.");
//        String playerName = event.getConnection().getName();
//        boolean loggedIn;
//        synchronized (loggedInPlayerList) {
//            loggedIn = loggedInPlayerList.contains(playerName);
//        }
//        if (loggedIn || Communication.sendConnectRequest(playerName) == 1) {
//            event.setCancelReason(zh);
//            event.setCancelled(true);
//        }
//    }
}
