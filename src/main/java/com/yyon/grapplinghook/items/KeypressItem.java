package com.yyon.grapplinghook.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public interface KeypressItem {
    void onCustomKeyDown(ItemStack stack, Player player, Keys key, boolean ismainhand);

    void onCustomKeyUp(ItemStack stack, Player player, Keys key, boolean ismainhand);

    enum Keys {
        LAUNCHER, THROWLEFT, THROWRIGHT, THROWBOTH, ROCKET
    }
}
