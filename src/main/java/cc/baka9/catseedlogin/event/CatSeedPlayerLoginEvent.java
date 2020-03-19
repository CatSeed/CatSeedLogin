package cc.baka9.catseedlogin.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Optional;

public class CatSeedPlayerLoginEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public CatSeedPlayerLoginEvent(Player player, String email, Result result){
        this.player = player;
        this.email = email;
        this.result = result;
    }

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    private Player player;
    private String email;
    private Result result;


    public Player getPlayer(){
        return player;
    }

    public Result getResult(){
        return result;
    }

    public Optional<String> getEmail(){
        return Optional.ofNullable(email);
    }


    public enum Result {
        SUCCESS,
        FAIL;

        Result(){
        }

    }
}
