package cc.baka9.catseedlogin.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


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


    private Player player;

    public Player getPlayer(){
        return player;
    }


}
