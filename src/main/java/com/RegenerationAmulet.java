package com;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RegenerationAmulet extends AmuletItem{
    public RegenerationAmulet(int level) {
        super(new Item.Properties().stacksTo(1),level);
    }

    @Override
    public void onEquippedCurioTick(ServerPlayer player, ItemStack stack) {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,40,getLevel(stack)));
    }
}
