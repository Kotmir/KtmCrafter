package tech.kotmir.customcrafter.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import tech.kotmir.customcrafter.KtmCrafter;

import java.util.Set;

public class RecipeEditorScreenHandler extends ScreenHandler {
    private final Inventory inventory = new SimpleInventory(54);
    
    // Слоты сетки крафта 3x3 и слот результата 25
    private static final Set<Integer> EDITABLE_SLOTS = Set.of(
            10, 11, 12,
            19, 20, 21,
            28, 29, 30,
            25
    );

    public RecipeEditorScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId);

        // Заполнение фоновым стеклом
        ItemStack grayGlass = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 54; i++) {
            if (!EDITABLE_SLOTS.contains(i)) {
                inventory.setStack(i, grayGlass.copy());
            }
        }

        // Верстак и Изумруд
        inventory.setStack(23, new ItemStack(Items.CRAFTING_TABLE));
        ItemStack emerald = new ItemStack(Items.EMERALD);
        emerald.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME, Text.literal("§a§lСохранить рецепт"));
        inventory.setStack(41, emerald);

        // Добавление 54 слотов
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9;
                this.addSlot(new Slot(inventory, index, 8 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return EDITABLE_SLOTS.contains(index);
                    }

                    @Override
                    public boolean canTakeItems(PlayerEntity playerEntity) {
                        return EDITABLE_SLOTS.contains(index);
                    }
                });
            }
        }

        // Инвентарь игрока
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex == 41) { // Клик по изумруду
            saveRecipe(player);
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    private void saveRecipe(PlayerEntity player) {
        ItemStack output = inventory.getStack(25);
        if (output.isEmpty()) {
            player.sendMessage(Text.literal("§cОшибка: Положите предмет результата в слот 25!"), false);
            return;
        }

        KtmCrafter.LOGGER.info("Сохранение кастомного рецепта для: {}", output.getItem().getName().getString());
        player.sendMessage(Text.literal("§aРецепт для " + output.getItem().getName().getString() + " успешно сохранён!"), false);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}