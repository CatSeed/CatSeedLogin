package cc.baka9.catseedlogin.database;

import cc.baka9.catseedlogin.object.LoginPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class SQL {
    protected JavaPlugin plugin;

    public SQL(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void init() throws Exception{
        boolean create = true;
        ResultSet re = getConnection().getMetaData().getTables(null, null, null, null);
        while (re.next()) {
            if (re.getString("TABLE_NAME").equals("accounts")) {
                create = false;
            }
        }
        re.close();
        if (create) {
            flush(new BufferStatement("CREATE TABLE accounts (name CHAR(255),password CHAR(255),email CHAR(255),lastAction TIMESTAMP)"));
        }

        try {
            flush(new BufferStatement("ALTER TABLE accounts ADD email CHAR(255)"));
        } catch (Exception e) {
            if (!e.getMessage().toLowerCase().contains("duplicate column name")) {
                throw e;
            }
        }

    }


    public void add(LoginPlayer lp) throws Exception{
        flush(new BufferStatement("INSERT INTO accounts (name,password,lastAction,email) VALUES(?,?,?,?)",
                lp.getName(), lp.getPassword(), new Date(), lp.getEmail()));
        Cache.refresh(lp.getName());
    }

    public void del(String name) throws Exception{
        flush(new BufferStatement("DELETE FROM accounts WHERE name = ?",
                name));
        Cache.refresh(name);
    }

    public void edit(LoginPlayer lp) throws Exception{
        flush(new BufferStatement("UPDATE accounts SET password = ?, lastAction = ?, email = ? WHERE name= ?"
                , lp.getPassword(), new Date(), lp.getEmail(), lp.getName()));
        Cache.refresh(lp.getName());
    }

    public LoginPlayer get(String name) throws Exception{
        PreparedStatement ps = new BufferStatement("SELECT * FROM accounts WHERE name = ?",
                name).prepareStatement(getConnection());

        ResultSet resultSet = ps.executeQuery();
        LoginPlayer lp = null;
        if (resultSet.next()) {
            lp = new LoginPlayer(name, resultSet.getString("password"));
            lp.setLastAction(resultSet.getLong("lastAction"));
            lp.setEmail(resultSet.getString("email"));
        }
        resultSet.close();
        ps.close();
        return lp;
    }

    public List<LoginPlayer> getAll() throws Exception{
        PreparedStatement ps = new BufferStatement("SELECT * FROM accounts").prepareStatement(getConnection());
        ResultSet resultSet = ps.executeQuery();
        List<LoginPlayer> lps = new ArrayList<>();
        LoginPlayer lp;
        while (resultSet.next()) {
            lp = new LoginPlayer(resultSet.getString("name"), resultSet.getString("password"));
            lp.setLastAction(resultSet.getLong("lastAction"));
            lp.setEmail(resultSet.getString("email"));
            lps.add(lp);
        }
        return lps;

    }

    public LoginPlayer getLike(String name) throws Exception{
        PreparedStatement ps = new BufferStatement("SELECT * FROM accounts WHERE name like ?",
                name).prepareStatement(getConnection());

        ResultSet resultSet = ps.executeQuery();
        LoginPlayer lp = null;
        if (resultSet.next()) {
            lp = new LoginPlayer(name, resultSet.getString("password"));
            lp.setLastAction(resultSet.getLong("lastAction"));
            lp.setEmail(resultSet.getString("email"));
        }
        resultSet.close();
        ps.close();
        return lp;
    }

    public abstract Connection getConnection() throws Exception;


    public void flush(BufferStatement bufferStatement) throws Exception{
        PreparedStatement ps = bufferStatement.prepareStatement(getConnection());
        ps.executeUpdate();
        ps.close();
    }
}
