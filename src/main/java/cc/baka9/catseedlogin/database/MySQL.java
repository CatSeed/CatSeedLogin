package cc.baka9.catseedlogin.database;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends SQL {
    CatSeedLogin plugin = CatSeedLogin.getInstance();
    private Connection connection;


    @Override
    public Connection getConnection() throws SQLException{

        if (this.connection != null && !this.connection.isClosed()) {
            return this.connection;
        }
        if (plugin.getDataFolder().exists()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection(
                        "jdbc:mysql://" + Config.MySQL.Host + ":" + Config.MySQL.Port + "/" + Config.MySQL.Database + "?characterEncoding=UTF-8",
                        Config.MySQL.User, Config.MySQL.Password
                );
                return this.connection;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            final boolean mkdir = plugin.getDataFolder().mkdir();
            return this.getConnection();
        }
    }

}