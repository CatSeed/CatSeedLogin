package cc.baka9.catseedlogin.database;

import cc.baka9.catseedlogin.object.LoginPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public abstract class SQL {

    public abstract void createBD() throws Exception;

    public abstract void add(LoginPlayer lp) throws Exception;

    public abstract void del(String name) throws Exception;

    public abstract void edit(LoginPlayer lp) throws Exception;

    public abstract LoginPlayer get(String name) throws Exception;

    public abstract List<LoginPlayer> getAll() throws Exception;

    public abstract LoginPlayer getLike(String name) throws Exception;

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
