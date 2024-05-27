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
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * bukkit 与 bc 的通讯交流
 */
public class Communication {
    private static ServerSocket serverSocket;

    /**
     * 异步关闭 socket server
     */
    public static void socketServerStopAsync() {
        CatSeedLogin.instance.runTaskAsync(Communication::socketServerStop);
    }

    public static void socketServerStop() {

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 异步启动 socket server 监听bc端发来的请求
     */
    public static void socketServerStartAsync() {
        // 指定一个合适的线程池，例如 Bukkit 的调度器 `Bukkit.getScheduler()`
        Bukkit.getScheduler().runTaskAsynchronously(CatSeedLogin.instance, Communication::socketServerStart);
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
                    e.printStackTrace(); // 处理接受客户端连接时的异常
                    continue; // 继续监听新的连接
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
    private static void handleRequest(Socket socket) {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleKeepLoggedInRequest(String playerName, String time, String sign) {
        // 验证请求的合法性
        // 对比玩家名，时间戳，和authKey加密的结果（加密是因为如果登录服不在内网环境下，则可能会被人使用这个功能给发包来直接绕过登录）
        if (Objects.equals(CommunicationAuth.encryption(playerName, time, Config.BungeeCord.AuthKey), sign)) {
            // 验证通过，切换主线程给予登录状态
            Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                LoginPlayer lp = Cache.getIgnoreCase(playerName);
                if (lp != null) {
                    LoginPlayerHelper.add(lp);
                    Player player = Bukkit.getPlayerExact(playerName);
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
                    // 将输出操作放在异步线程中
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(result ? 1 : 0);
                    outputStream.flush(); // 需要手动刷新输出流
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

        });
    }


}
