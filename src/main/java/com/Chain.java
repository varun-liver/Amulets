package com;

import net.minecraft.world.item.Item;

public class Chain extends Item {
    public final String type;
    public final int level;
    public Chain(String type,Item.Properties properties,int level) {
        super(properties);
        this.type = type;
        this.level = level;
    }

}
