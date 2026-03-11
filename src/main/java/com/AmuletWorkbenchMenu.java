package com;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AmuletWorkbenchMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_COUNT = 2;
    public static final int SLOT1X = 36;
    public static final int SLOT1Y = 15;
    public static final int SLOT2X = 125;
    public static final int SLOT2Y = 15;
    public static final int RESULT_SLOT_X = 80;
    public static final int RESULT_SLOT_Y = 48;

    private static final int RESULT_SLOT_INDEX = 2;
    private static final int PLAYER_INV_START_INDEX = 3;

    private final ContainerLevelAccess access;
    private final Container amuletContainer = new SimpleContainer(INPUT_SLOT_COUNT);
    private final ResultContainer resultContainer = new ResultContainer();

    public AmuletWorkbenchMenu(int containerId, Inventory inventory, FriendlyByteBuf data) {
        this(containerId, inventory, ContainerLevelAccess.create(inventory.player.level(), data.readBlockPos()));
    }

    public AmuletWorkbenchMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(amulets.Amulet_WORKBENCH_MENU.get(), containerId);
        this.access = access;

        // Two input amulet slots
        this.addSlot(new Slot(this.amuletContainer, 0, SLOT1X, SLOT1Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof Item;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                AmuletWorkbenchMenu.this.slotsChanged(AmuletWorkbenchMenu.this.amuletContainer);
            }
        });
        this.addSlot(new Slot(this.amuletContainer, 1, SLOT2X, SLOT2Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof Item;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                AmuletWorkbenchMenu.this.slotsChanged(AmuletWorkbenchMenu.this.amuletContainer);
            }
        });

        // Output slot
        this.addSlot(new Slot(this.resultContainer, 0, RESULT_SLOT_X, RESULT_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                AmuletWorkbenchMenu.this.consumeInputs();
                AmuletWorkbenchMenu.this.updateResult();
                super.onTake(player, stack);
            }
        });

        // Player inventory (3 rows x 9 columns)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, amulets.Amulet_Maker.get());
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.amuletContainer) {
            this.updateResult();
        }
    }

    private void updateResult() {
        ItemStack first = this.amuletContainer.getItem(0);
        ItemStack second = this.amuletContainer.getItem(1);
        ItemStack result = ItemStack.EMPTY;
        if(matches(first,second,amulets.AZULI.get(),amulets.AZULI.get())){
            result = new ItemStack(Items.DIAMOND);
        }
        this.resultContainer.setItem(0,result);
        this.broadcastChanges();
    }
    private boolean matches(ItemStack first, ItemStack second, Item firstItem, Item secondItem) {
        return first.getItem() == firstItem && second.getItem() == secondItem;
    }
    private void consumeInputs() {
        if (!this.resultContainer.getItem(0).isEmpty()) {
            this.amuletContainer.removeItem(0, 1);
            this.amuletContainer.removeItem(1, 1);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack quickMoved = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack rawStack = slot.getItem();
        quickMoved = rawStack.copy();

        if (index == RESULT_SLOT_INDEX) {
            if (!this.moveItemStackTo(rawStack, PLAYER_INV_START_INDEX, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(rawStack, quickMoved);
            slot.onTake(player, rawStack);
        } else if (index < PLAYER_INV_START_INDEX) {
            // Move from input slots to player inventory/hotbar
            if (!this.moveItemStackTo(rawStack, PLAYER_INV_START_INDEX, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Move from player inventory/hotbar to input slots
            if (!this.moveItemStackTo(rawStack, 0, INPUT_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (rawStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return quickMoved;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, this.amuletContainer);
        this.clearContainer(player, this.resultContainer);
    }
}
