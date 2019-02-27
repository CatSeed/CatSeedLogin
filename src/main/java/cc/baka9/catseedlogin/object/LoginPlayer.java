package cc.baka9.catseedlogin.object;

import cc.baka9.catseedlogin.Crypt;

import java.util.Objects;

public class LoginPlayer {
    private String name;
    private String password;
    private long lastAction;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginPlayer that = (LoginPlayer) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

    @Override
    public String toString(){
        return "LoginPlayer{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", lastAction=" + lastAction +
                '}';
    }

    public LoginPlayer(String name, String password){
        this.name = name;
        this.password = password;
    }

    public void crypt(){
        password = Crypt.encrypt(name, password);
    }

    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String encryptedPassword){
        this.password = encryptedPassword;
    }


    public long getLastAction(){
        return lastAction;
    }

    public void setLastAction(long lastAction){
        this.lastAction = lastAction;
    }
}
