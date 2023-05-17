package cc.baka9.catseedlogin.bungee;

import cc.baka9.catseedlogin.util.CommunicationAuth;
import net.md_5.bungee.api.ProxyServer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Objects;

/**
 * bc 与 bukkit 的通讯交流
 */
public class Communication {

    public static int sendConnectRequest(String playerName) {
        try (Socket socket = getSocket();
                BufferedWriter bufferedWriter = getSocketBufferedWriter(socket)) {
            // 请求类型
            bufferedWriter.write("Connect");
            bufferedWriter.newLine();
            // 玩家名
            bufferedWriter.write(playerName);
            bufferedWriter.newLine();

            bufferedWriter.flush();
            return socket.getInputStream().read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void sendKeepLoggedInRequest(String playerName) {
        try (Socket socket = getSocket(); BufferedWriter bufferedWriter = getSocketBufferedWriter(socket)) {
            // 请求类型
            bufferedWriter.write("KeepLoggedIn");
            bufferedWriter.newLine();
            // 玩家名
            bufferedWriter.write(playerName);
            bufferedWriter.newLine();
            // 时间戳
            String time = String.valueOf(System.currentTimeMillis());
            bufferedWriter.write(time);
            bufferedWriter.newLine();
            // 根据玩家名，时间戳，和authKey加密的结果（加密是因为如果登录服不在内网环境下，则可能会被人使用这个功能给发包来绕过登录）
            String sign = CommunicationAuth.encryption(playerName, time, Config.AuthKey);
            bufferedWriter.write(Objects.requireNonNull(sign));
            bufferedWriter.newLine();

            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Socket getSocket() throws IOException {
        try {
            return new Socket(Config.Host, Config.Port);
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().warning("§c请检查装载登录插件的子服是否在 bungeecord.yml 中开启了bungeecord功能，以及Host和Port是否与bc端的配置相同");
            throw new IOException(e);
        }
    }

    private static BufferedWriter getSocketBufferedWriter(Socket socket) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }


}
