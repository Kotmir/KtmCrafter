package tech.kotmir.customcrafter;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.kotmir.customcrafter.network.OpenStationPayload;
import tech.kotmir.customcrafter.network.SaveRecipePayload;
import tech.kotmir.customcrafter.screen.RecipeEditorScreenHandler;

public class KtmCrafter implements ModInitializer {
    public static final String MOD_ID = "ktmcrafter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing KtmCrafter...");

        PayloadTypeRegistry.playC2S().register(OpenStationPayload.ID, OpenStationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SaveRecipePayload.ID, SaveRecipePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(OpenStationPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                var player = context.player();

                switch (payload.stationId()) {
                    case "editor" -> openEditor(player);
                    case "crafting_table" -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId, inv, p) -> new CraftingScreenHandler(syncId, inv, ScreenHandlerContext.EMPTY),
                            Text.literal("Верстак Крафта")
                    ));
                    case "stonecutter" -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId, inv, p) -> new StonecutterScreenHandler(syncId, inv, ScreenHandlerContext.EMPTY),
                            Text.literal("Камнерез")
                    ));
                    case "smithing_table" -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId, inv, p) -> new SmithingScreenHandler(syncId, inv, ScreenHandlerContext.EMPTY),
                            Text.literal("Кузнечный Верстак")
                    ));
                    case "loom" -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId, inv, p) -> new LoomScreenHandler(syncId, inv, ScreenHandlerContext.EMPTY),
                            Text.literal("Ткацкий Станок")
                    ));
                    case "cartography_table" -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId, inv, p) -> new CartographyTableScreenHandler(syncId, inv, ScreenHandlerContext.EMPTY),
                            Text.literal("Стол Картографа")
                    ));
                    case "grindstone" -> player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId, inv, p) -> new GrindstoneScreenHandler(syncId, inv, ScreenHandlerContext.EMPTY),
                            Text.literal("Точило")
                    ));
                }
            });
        });
    }

    public static void openEditor(net.minecraft.server.network.ServerPlayerEntity player) {
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inv, p) -> new RecipeEditorScreenHandler(syncId, inv),
                Text.literal("Редактор Кастомных Крафтов")
        ));
    }
}