package cc.baka9.catseedlogin.object;

import cc.baka9.catseedlogin.util.Crypt;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@Setter
public class LoginPlayer {
    private String name;
    private String password;
    private String email;
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

    public LoginPlayer(String name, String password){
        this.name = name;
        this.password = password;
    }

    public void crypt(){
        password = Crypt.encrypt(name, password);
    }


}
