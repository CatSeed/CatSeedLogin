package cc.baka9.catseedlogin.database;

import cc.baka9.catseedlogin.object.LoginPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class SQL {

    public void createBD() throws Exception{
        flush(new BufferStatement("CREATE TABLE accounts (name CHAR(255),password CHAR(255),lastAction TIMESTAMP)"));
    }


    public void add(LoginPlayer lp) throws Exception{
        flush(new BufferStatement("INSERT INTO accounts VALUES(?,?,?)",
                lp.getName(), lp.getPassword(), new Date()));
        Cache.refresh(lp.getName());
    }

    public void del(String name) throws Exception{
        flush(new BufferStatement("DELETE FROM accounts WHERE name = ?",
                name));
        Cache.refresh(name);
    }

    public void edit(LoginPlayer lp) throws Exception{
        flush(new BufferStatement("UPDATE accounts SET password = ?, lastAction = ? WHERE name= ?"
                , lp.getPassword(), new Date(), lp.getName()));
        Cache.refresh(lp.getName());
    }

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

    public abstract Connection getConnection() throws Exception;


    public boolean hasTable(String tabName) throws Exception{
        ResultSet re = getConnection().getMetaData().getTables(null, null, null, null);
        while (re.next()) {
            if (re.getString("TABLE_NAME").equals(tabName)) {
                re.close();
                return true;
            }
        }
        re.close();
        return false;
    }

    public void flush(BufferStatement bufferStatement) throws Exception{
        PreparedStatement ps = bufferStatement.prepareStatement(getConnection());
        ps.executeUpdate();
        ps.close();
    }
}
