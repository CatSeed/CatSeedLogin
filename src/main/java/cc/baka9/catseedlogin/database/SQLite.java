package cc.baka9.catseedlogin.database;

import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.object.LoginPlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLite extends SQL {
    CatSeedLogin plugin = CatSeedLogin.getInstance();
    private Connection connection;

    @Override
    public void createBD() throws Exception{
        flush(new BufferStatement("CREATE TABLE accounts (name CHAR(255),password CHAR(255),lastAction TIMESTAMP)"));
    }

    @Override
    public void add(LoginPlayer loginPlayer) throws Exception{
        flush(new BufferStatement("INSERT INTO accounts VALUES(?,?,?)",
                loginPlayer.getName(), loginPlayer.getPassword(), new Date()));
        Cache.refresh(loginPlayer.getName());
    }

    @Override
    public void del(String name) throws Exception{
        flush(new BufferStatement("DELETE FROM accounts WHERE name = ?",
                name));
        Cache.refresh(name);
    }

    @Override
    public void edit(LoginPlayer loginPlayer) throws Exception{
        flush(new BufferStatement("UPDATE accounts SET password = ?, lastAction = ? WHERE name= ?"
                , loginPlayer.getPassword(), new Date(), loginPlayer.getName()));
        Cache.refresh(loginPlayer.getName());
    }

    @Override
    public LoginPlayer get(String name) throws Exception{
        PreparedStatement ps = new BufferStatement("SELECT * FROM accounts WHERE name = ?",
                name).prepareStatement(getConnection());

        ResultSet resultSet = ps.executeQuery();
        LoginPlayer lp = null;
        if (resultSet.next()) {
            lp = new LoginPlayer(name, resultSet.getString(2));
            lp.setLastAction(resultSet.getLong(3));
        }
        resultSet.close();
        ps.close();
        return lp;
    }

    @Override
    public List<LoginPlayer> getAll() throws Exception{
        PreparedStatement ps = new BufferStatement("SELECT * FROM accounts").prepareStatement(getConnection());
        ResultSet resultSet = ps.executeQuery();
        List<LoginPlayer> lps = new ArrayList<>();
        LoginPlayer lp;
        while (resultSet.next()) {
            lp = new LoginPlayer(resultSet.getString(1), resultSet.getString(2));
            lp.setLastAction(resultSet.getLong(3));
            lps.add(lp);
        }
        return lps;

    }

    @Override
    public LoginPlayer getLike(String name) throws Exception{
        PreparedStatement ps = new BufferStatement("SELECT * FROM accounts WHERE name like ?",
                name).prepareStatement(getConnection());

        ResultSet resultSet = ps.executeQuery();
        LoginPlayer lp = null;
        if (resultSet.next()) {
            lp = new LoginPlayer(name, resultSet.getString(2));
            lp.setLastAction(resultSet.getLong(3));
        }
        resultSet.close();
        ps.close();
        return lp;
    }

    @Override
    public Connection getConnection() throws SQLException{

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
            final boolean mkdir = plugin.getDataFolder().mkdir();
            return this.getConnection();
        }
    }

}