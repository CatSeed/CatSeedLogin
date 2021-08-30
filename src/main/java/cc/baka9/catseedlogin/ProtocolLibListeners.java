package cc.baka9.catseedlogin;

import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;

public class ProtocolLibListeners extends PacketAdapter {


    public ProtocolLibListeners(){
        super(CatSeedLogin.instance, ListenerPriority.HIGHEST,
                PacketType.Play.Server.SET_SLOT,
                PacketType.Play.Server.WINDOW_ITEMS
        );

    }

    @Override
    public void onPacketSending(PacketEvent event){
        PacketType packetType = event.getPacketType();
        if (packetType == PacketType.Play.Server.SET_SLOT || packetType == PacketType.Play.Server.WINDOW_ITEMS) {
            Player player = event.getPlayer();
            PacketContainer packet = event.getPacket();
            int windowId = packet.getIntegers().read(0);
            if (windowId == 0 && !LoginPlayerHelper.isLogin(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event){
    }


}
