package com;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class StrengthAmulet extends AmuletItem {
    public StrengthAmulet(int level) {
        super(new Item.Properties().stacksTo(1),level);
    }
    @Override
    public void onEquippedCurioTick(ServerPlayer player, ItemStack stack) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, getLevel(stack)));
    }
}
