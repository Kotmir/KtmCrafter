package tech.kotmir.customcrafter.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import tech.kotmir.customcrafter.KtmCrafter;

public record SaveRecipePayload() implements CustomPayload {
    public static final CustomPayload.Id<SaveRecipePayload> ID = new CustomPayload.Id<>(Identifier.of(KtmCrafter.MOD_ID, "save_recipe"));
    public static final PacketCodec<RegistryByteBuf, SaveRecipePayload> CODEC = PacketCodec.unit(new SaveRecipePayload());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}