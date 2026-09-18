package tech.kotmir.customcrafter.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenStationPayload(String stationId) implements CustomPayload {
    public static final CustomPayload.Id<OpenStationPayload> ID = new CustomPayload.Id<>(Identifier.of("ktmcrafter", "open_station"));
    public static final PacketCodec<RegistryByteBuf, OpenStationPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, OpenStationPayload::stationId,
            OpenStationPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}