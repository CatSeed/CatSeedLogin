package cc.baka9.catseedlogin.object;

import cc.baka9.catseedlogin.util.Crypt;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ToString
@Getter
@Setter
public class LoginPlayer {
    private String name;
    private String password;
    private String email;
    private String ips;
    private long lastAction;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginPlayer that = (LoginPlayer) o;
        return Objects.equals(name, that.name);
    }

    public List<String> getIpsList(){
        List<String> ipList = new ArrayList<>();
        if (this.ips != null) {
            ipList.addAll(Arrays.asList(this.ips.split(";")));
        }
        return ipList;
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
