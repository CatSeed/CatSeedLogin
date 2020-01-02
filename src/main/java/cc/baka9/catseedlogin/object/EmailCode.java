package cc.baka9.catseedlogin.object;

import cc.baka9.catseedlogin.util.Util;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class EmailCode {

    public enum Type {
        Bind, ResetPassword
    }

    private static Map<String, EmailCode> bindMap = new HashMap<>(10);

    private static Map<String, EmailCode> resetPasswordMap = new HashMap<>(10);

    private String name;
    private String email;
    private String code;
    private long durability;
    private long createTime;

    private EmailCode(String name, String email, long durability){
        this.name = name;
        this.email = email;
        this.durability = durability;
        this.createTime = System.currentTimeMillis();
        this.code = Util.randomStr();
    }

    public static EmailCode create(String name, String email, long durability, Type type){
        if (type == Type.Bind) {
            bindMap.put(name, new EmailCode(name, email, durability));
            return bindMap.get(name);
        }
        if (type == Type.ResetPassword) {

            resetPasswordMap.put(name, new EmailCode(name, email, durability));
            return resetPasswordMap.get(name);
        }
        return null;
    }

    public static Optional<EmailCode> getByName(String name, Type type){
        clear();
        if (type == Type.Bind && bindMap.containsKey(name)) {
            return Optional.of(bindMap.get(name));
        }
        if (type == Type.ResetPassword && resetPasswordMap.containsKey(name)) {
            return Optional.of(resetPasswordMap.get(name));
        }
        return Optional.empty();
    }


    public static void removeByName(String name, Type type){
        clear();
        if (type == Type.Bind) {

            bindMap.remove(name);
        }
        if (type == Type.ResetPassword) {
            resetPasswordMap.remove(name);
        }

    }

    private static void clear(){
        long now = System.currentTimeMillis();
        bindMap.entrySet().removeIf(next -> now - next.getValue().getCreateTime() > next.getValue().getDurability());
        resetPasswordMap.entrySet().removeIf(next -> now - next.getValue().getCreateTime() > next.getValue().getDurability());
    }

}
