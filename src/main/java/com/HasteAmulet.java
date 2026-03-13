package com;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HasteAmulet extends AmuletItem{
    public final int level;
    public HasteAmulet(int level) {
        super(new Item.Properties().stacksTo(1),level);
        this.level = level;
    }
    public void onEquippedCurioTick(ServerPlayer player, ItemStack stack) {
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 40, getLevel(stack)));
    }
}
