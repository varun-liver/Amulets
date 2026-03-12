package com;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SpeedAmulet extends AmuletItem{
    public SpeedAmulet() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void onEquippedCurioTick(ServerPlayer player, ItemStack stack) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1));
    }
}
