package games.cubi.raycastedantiesp.packetevents;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

// For replaying player-specific packets to other players
public sealed interface PacketEventsPlayerReplayData extends PacketEventsEntityReplayData permits PacketEventsPlayerReplayData.Impl {

    void addPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate packet);

    List<WrapperPlayServerPlayerInfoUpdate> playerInfoUpdates();

    static PacketEventsPlayerReplayData create() {
        return new Impl();
    }

    final class Impl extends PacketEventsEntityReplayData.Impl implements PacketEventsPlayerReplayData{
        private final ConcurrentLinkedQueue<WrapperPlayServerPlayerInfoUpdate> playerInfoUpdates = new ConcurrentLinkedQueue<>();

        public void addPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate packet) {
            playerInfoUpdates.add(packet);
        }

        public List<WrapperPlayServerPlayerInfoUpdate> playerInfoUpdates() {
            return List.copyOf(playerInfoUpdates);
        }

        public PacketEventsPlayerReplayData asPlayerReplayData() {
            return this;
        }
    }
}