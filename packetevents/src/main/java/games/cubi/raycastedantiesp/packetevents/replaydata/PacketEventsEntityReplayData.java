package games.cubi.raycastedantiesp.packetevents.replaydata;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;

import games.cubi.raycastedantiesp.core.utils.Clearable;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public sealed interface PacketEventsEntityReplayData extends Clearable permits PacketEventsEntityReplayData.Impl, PacketEventsPlayerReplayData {

    void addEffectPacket(WrapperPlayServerEntityEffect packet);

    List<WrapperPlayServerEntityEffect> effectPackets();

    WrapperPlayServerEntityMetadata metadataPacket();

    void setMetadataPacket(WrapperPlayServerEntityMetadata metadataPacket);

    WrapperPlayServerEntityEquipment equipmentPacket();

    void setEquipmentPacket(WrapperPlayServerEntityEquipment equipmentPacket);

    WrapperPlayServerEntityVelocity velocityPacket();

    void setVelocityPacket(WrapperPlayServerEntityVelocity velocityPacket);

    WrapperPlayServerSetPassengers passengersPacket();

    void setPassengersPacket(WrapperPlayServerSetPassengers passengersPacket);

    /**
     * @return The same object as a {@link PacketEventsPlayerReplayData}, or null if this is not a player entity
     */
    PacketEventsPlayerReplayData asPlayerReplayData();

    static PacketEventsEntityReplayData create() {
        return new Impl();
    }

    sealed class Impl implements PacketEventsEntityReplayData permits PacketEventsPlayerReplayData.Impl {
        private final Queue<WrapperPlayServerEntityEffect> effectPackets = new ArrayDeque<>();
        private volatile WrapperPlayServerEntityMetadata metadataPacket;
        private volatile WrapperPlayServerEntityEquipment equipmentPacket;
        private volatile WrapperPlayServerEntityVelocity velocityPacket;
        private volatile WrapperPlayServerSetPassengers passengersPacket;

        public void addEffectPacket(WrapperPlayServerEntityEffect packet) {
            effectPackets.add(packet);
        }

        public List<WrapperPlayServerEntityEffect> effectPackets() {
            return List.copyOf(effectPackets);
        }

        public WrapperPlayServerEntityMetadata metadataPacket() {
            return metadataPacket;
        }

        public void setMetadataPacket(WrapperPlayServerEntityMetadata metadataPacket) {
            this.metadataPacket = metadataPacket;
        }

        public WrapperPlayServerEntityEquipment equipmentPacket() {
            return equipmentPacket;
        }

        public void setEquipmentPacket(WrapperPlayServerEntityEquipment equipmentPacket) {
            this.equipmentPacket = equipmentPacket;
        }

        public WrapperPlayServerEntityVelocity velocityPacket() {
            return velocityPacket;
        }

        public void setVelocityPacket(WrapperPlayServerEntityVelocity velocityPacket) {
            this.velocityPacket = velocityPacket;
        }

        public WrapperPlayServerSetPassengers passengersPacket() {
            return passengersPacket;
        }

        public void setPassengersPacket(WrapperPlayServerSetPassengers passengersPacket) {
            this.passengersPacket = passengersPacket;
        }

        public PacketEventsPlayerReplayData asPlayerReplayData() {
            return null;
        }

        @Override
        public void clear() {
            effectPackets.clear();
            metadataPacket = null;
            equipmentPacket = null;
            velocityPacket = null;
            passengersPacket = null;
        }
    }

}
