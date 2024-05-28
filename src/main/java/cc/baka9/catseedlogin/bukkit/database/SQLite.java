package cc.baka9.catseedlogin.bukkit.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends SQL {
    private Connection connection;

    public SQLite(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @Override
    public Connection getConnection() throws SQLException {

        if (this.connection != null && !this.connection.isClosed()) {
            return this.connection;
        }

        if (plugin.getDataFolder().exists()) {
            try {
                Class.forName("org.sqlite.JDBC");
                this.connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/accounts.db");
                return this.connection;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            if (plugin.getDataFolder().mkdir()) {
                return null;
            } else {
                return null;
            }
        }
    }
}