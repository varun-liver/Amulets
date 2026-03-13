package com;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class AmuletItem extends Item {
    public final int level;
    public AmuletItem(Properties properties, int level) {
        super(properties);
        this.level = level;
    }

    public static boolean hasAmuletEquipped(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player).map(inv -> {
            ICurioStacksHandler handler = inv.getCurios().get("amulet");
            if (handler == null) return false;

            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStacks().getStackInSlot(i);

                if (!stack.isEmpty() && stack.getItem() instanceof AmuletItem) {
                    return true;
                }
            }

            return false;
        }).orElse(false);
    }

    public void onEquippedCurioTick(ServerPlayer player, ItemStack stack) {
        // Override in subclasses for per-tick amulet behavior.
    }

    protected int getLevel(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("level")) {
            return stack.getTag().getInt("level");
        }
        return this.level;
    }
}
