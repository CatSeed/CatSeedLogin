package cc.baka9.catseedlogin.bukkit.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


@Getter
public class CatSeedPlayerRegisterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    public CatSeedPlayerRegisterEvent(Player player){
        this.player = player;
    }


    private final Player player;


}
