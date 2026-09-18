package tech.kotmir.customcrafter.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tech.kotmir.customcrafter.network.OpenStationPayload;

import java.util.ArrayList;
import java.util.List;

public class CustomCrafterScreen extends Screen {
    private static final Identifier CONTAINER_TEXTURE = Identifier.of("minecraft", "textures/gui/container/generic_54.png");

    private static final int BG_WIDTH = 176;
    private static final int BG_HEIGHT = 222;
    private int x, y;
    private int currentPage = 0; // 0 = Стр. 1, 1 = Стр. 2

    private final ItemStack[] page1Slots = new ItemStack[54];
    private final ItemStack[] page2Slots = new ItemStack[54];

    public CustomCrafterScreen() {
        super(Text.literal("KtmCrafter"));
        setupPages();
    }

    private void setupPages() {
        ItemStack blackGlass = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        ItemStack arrowNext = new ItemStack(Items.ARROW);
        ItemStack arrowPrev = new ItemStack(Items.ARROW);
        ItemStack greenBook = new ItemStack(Items.KNOWLEDGE_BOOK);

        for (int i = 0; i < 54; i++) {
            page1Slots[i] = blackGlass.copy();
            page2Slots[i] = blackGlass.copy();
        }

        // === СТРАНИЦА 1 ===
        page1Slots[10] = new ItemStack(Items.CRAFTING_TABLE);  // Верстак
        page1Slots[12] = new ItemStack(Items.SMITHING_TABLE);  // Стол кузнеца
        page1Slots[14] = new ItemStack(Items.LOOM);            // Ткацкий станок
        page1Slots[16] = new ItemStack(Items.CARTOGRAPHY_TABLE); // Стол картографа

        page1Slots[22] = greenBook.copy();                     // Зеленая книга
        page1Slots[26] = arrowNext;                            // Переход на стр. 2

        page1Slots[28] = new ItemStack(Items.FURNACE);         // Печь
        page1Slots[30] = new ItemStack(Items.BLAST_FURNACE);   // Доменная печь
        page1Slots[32] = new ItemStack(Items.SMOKER);          // Коптильня
        page1Slots[34] = new ItemStack(Items.FLETCHING_TABLE); // Стол лучника

        // === СТРАНИЦА 2 ===
        page2Slots[10] = new ItemStack(Items.STONECUTTER);     // Камнерез
        page2Slots[12] = new ItemStack(Items.GRINDSTONE);      // Точило

        page2Slots[18] = arrowPrev;                            // Переход на стр. 1
        page2Slots[22] = greenBook.copy();                     // Зеленая книга

        page2Slots[28] = new ItemStack(Items.CAMPFIRE);        // Костер
        page2Slots[30] = new ItemStack(Items.SOUL_CAMPFIRE);   // Костер душ
        page2Slots[32] = new ItemStack(Items.BREWING_STAND);   // Зельеварение
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - BG_WIDTH) / 2;
        this.y = (this.height - BG_HEIGHT) / 2;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            int slot = getHoveredSlot(click.x(), click.y());
            if (slot != -1) {
                handleSlotClick(slot);
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    private void handleSlotClick(int slot) {
        if (currentPage == 0) {
            switch (slot) {
                case 26 -> currentPage = 1; // Переход на страницу 2
                case 10 -> openStation("crafting_table");
                case 12 -> openStation("smithing_table");
                case 14 -> openStation("loom");
                case 16 -> openStation("cartography_table");
                case 28 -> openStation("furnace");
                case 30 -> openStation("blast_furnace");
                case 32 -> openStation("smoker");
                case 34 -> openStation("fletching_table");
            }
        } else if (currentPage == 1) {
            switch (slot) {
                case 18 -> currentPage = 0; // Переход на страницу 1
                case 10 -> openStation("stonecutter");
                case 12 -> openStation("grindstone");
                case 28 -> openStation("campfire");
                case 30 -> openStation("soul_campfire");
                case 32 -> openStation("brewing_stand");
            }
        }
    }

    private void openStation(String stationId) {
        ClientPlayNetworking.send(new OpenStationPayload(stationId));
    }

    private int getHoveredSlot(double mouseX, double mouseY) {
        for (int i = 0; i < 54; i++) {
            int col = i % 9;
            int row = i / 9;
            int slotX = this.x + 7 + col * 18;
            int slotY = this.y + 17 + row * 18;

            if (mouseX >= slotX && mouseX < slotX + 18 && mouseY >= slotY && mouseY < slotY + 18) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                CONTAINER_TEXTURE,
                this.x,
                this.y,
                0.0f,
                0.0f,
                BG_WIDTH,
                BG_HEIGHT,
                256,
                256
        );

        context.drawText(this.textRenderer, this.title, this.x + 8, this.y + 6, 0x404040, false);

        ItemStack[] currentSlots = (currentPage == 0) ? page1Slots : page2Slots;
        int hoveredSlot = getHoveredSlot(mouseX, mouseY);

        for (int i = 0; i < 54; i++) {
            int col = i % 9;
            int row = i / 9;
            int slotX = this.x + 8 + col * 18;
            int slotY = this.y + 18 + row * 18;

            ItemStack stack = currentSlots[i];
            if (!stack.isEmpty()) {
                context.drawItem(stack, slotX, slotY);
            }
        }

        if (hoveredSlot != -1) {
            ItemStack hoveredStack = currentSlots[hoveredSlot];
            if (!hoveredStack.isEmpty() && hoveredStack.getItem() != Items.BLACK_STAINED_GLASS_PANE) {
                List<Text> tooltip = createCustomTooltip(hoveredSlot, currentPage);
                if (!tooltip.isEmpty()) {
                    context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
                }
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private List<Text> createCustomTooltip(int slot, int page) {
        List<Text> list = new ArrayList<>();

        if (slot == 22) {
            list.add(Text.literal("§a§l📖 KtmCrafter — Справка"));
            list.add(Text.literal("§7Нажимайте на блоки оборудования,"));
            list.add(Text.literal("§7чтобы открыть кастомные крафты."));
            list.add(Text.literal(""));
            list.add(Text.literal("§e💡 Навигация: §fСтрелочки по бокам"));
            return list;
        }

        if (page == 0) {
            switch (slot) {
                case 10 -> {
                    list.add(Text.literal("§6§l📦 Верстак Крафта"));
                    list.add(Text.literal("§7Основной верстак для создания"));
                    list.add(Text.literal("§7уникальных предметов и компонентов."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 12 -> {
                    list.add(Text.literal("§b§l⚒️ Кузнечный Верстак"));
                    list.add(Text.literal("§7Улучшение снаряжения, ковка Незерита"));
                    list.add(Text.literal("§7и нанесение особых рун."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 14 -> {
                    list.add(Text.literal("§d§l🧵 Ткацкий Станок"));
                    list.add(Text.literal("§7Создание кастомных флагов, узоров"));
                    list.add(Text.literal("§7и декоративных тканей."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 16 -> {
                    list.add(Text.literal("§e§l🗺️ Стол Картографа"));
                    list.add(Text.literal("§7Исследование территорий, копирование"));
                    list.add(Text.literal("§7и расширение секретных карт."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 26 -> {
                    list.add(Text.literal("§a§lВперёд ▶"));
                    list.add(Text.literal("§7Перейти на страницу 2"));
                }
                case 28 -> {
                    list.add(Text.literal("§c§l🔥 Плавильная Печь"));
                    list.add(Text.literal("§7Классическая обжарка руд и материалов."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 30 -> {
                    list.add(Text.literal("§7§l🌋 Доменная Печь"));
                    list.add(Text.literal("§7Ускоренная в 2 раза плавка металлов."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 32 -> {
                    list.add(Text.literal("§6§l🍗 Коптильня"));
                    list.add(Text.literal("§7Быстрое приготовление изысканной еды."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 34 -> {
                    list.add(Text.literal("§g§l🏹 Стол Лучника"));
                    list.add(Text.literal("§7Создание особых стрел с эффектами."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
            }
        } else if (page == 1) {
            switch (slot) {
                case 10 -> {
                    list.add(Text.literal("§f§l🪨 Камнерез"));
                    list.add(Text.literal("§7Быстрая обработка камня и минералов."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 12 -> {
                    list.add(Text.literal("§8§l⚙️ Точило"));
                    list.add(Text.literal("§7Снятие зачарований и ремонт вещей."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 18 -> {
                    list.add(Text.literal("§c§l◀ Назад"));
                    list.add(Text.literal("§7Вернуться на страницу 1"));
                }
                case 28 -> {
                    list.add(Text.literal("§6§l🔥 Костёр"));
                    list.add(Text.literal("§7Медленная жарка пищи без топлива."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 30 -> {
                    list.add(Text.literal("§b§l👻 Костёр Душ"));
                    list.add(Text.literal("§7Мистический огонь для ритуалов."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
                case 32 -> {
                    list.add(Text.literal("§2§l🧪 Зельеварение"));
                    list.add(Text.literal("§7Варочная стойка для редких зелий."));
                    list.add(Text.literal(""));
                    list.add(Text.literal("§e▶ Нажмите для открытия"));
                }
            }
        }

        return list;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}