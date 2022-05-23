package cc.baka9.catseedlogin.bukkit;

import cc.baka9.catseedlogin.bukkit.database.Cache;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayer;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.CommunicationAuth;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * bukkit 与 bc 的通讯交流
 */
public class Communication {
    private static ServerSocket serverSocket;

    /**
     * 异步关闭 socket server
     */
    public static void socketServerStopAsync() {
        CatSeedLogin.instance.runTaskAsync(() -> {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 异步启动 socket server 监听bc端发来的请求
     */
    public static void socketServerStartAsync() {
        CatSeedLogin.instance.runTaskAsync(Communication::socketServerStart);
    }

    /**
     * 启动 socket server 监听bc端发来的请求
     */
    private static void socketServerStart() {
        try {
            InetAddress inetAddress = InetAddress.getByName(Config.BungeeCord.Host);
            serverSocket = new ServerSocket(Integer.parseInt(Config.BungeeCord.Port), 50, inetAddress);
            while (!serverSocket.isClosed()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                    handleRequest(socket);
                } catch (IOException e) {
                    break;
                }
            }
        } catch (UnknownHostException e) {
            CatSeedLogin.instance.getLogger().warning("无法解析域名或IP地址");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理请求
     */
    private static void handleRequest(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String requestType = bufferedReader.readLine();
        String playerName = bufferedReader.readLine();
        switch (requestType) {
            case "Connect":
                handleConnectRequest(socket, playerName);
                break;
            case "KeepLoggedIn":
                String time = bufferedReader.readLine();
                String sign = bufferedReader.readLine();
                handleKeepLoggedInRequest(playerName, time, sign);
                break;
            default:
                break;
        }
    }

    private static void handleKeepLoggedInRequest(String playerName, String time, String sign) {
        // 验证请求的合法性
        // 对比玩家名，时间戳，和authKey加密的结果（加密是因为如果登录服不在内网环境下，则可能会被人使用这个功能给发包来直接绕过登录）
        if (CommunicationAuth.encryption(playerName, time, Config.BungeeCord.AuthKey).equals(sign)) {
            // 切换主线程给予登录状态
            Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                LoginPlayer lp = Cache.getIgnoreCase(playerName);
                Player player = Bukkit.getPlayerExact(playerName);
                if (lp != null && player != null && player.isOnline()) {
                    LoginPlayerHelper.add(lp);
                    if (player != null) {
                        player.updateInventory();
                    }
                }

            });
        }
    }

    private static void handleConnectRequest(Socket socket, String playerName) {
        // 切换主线程获取是否已登录
        Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
            boolean result = LoginPlayerHelper.isLogin(playerName);

            // 切换异步线程返回结果
            CatSeedLogin.instance.runTaskAsync(() -> {
                try {
                    socket.getOutputStream().write(result ? 1 : 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

        });
    }


}
