package com;

import net.minecraft.world.item.Item;

public class Chain extends Item {
    public final String type;
    public Chain(String type,Item.Properties properties) {
        super(properties);
        this.type = type;
    }

}
