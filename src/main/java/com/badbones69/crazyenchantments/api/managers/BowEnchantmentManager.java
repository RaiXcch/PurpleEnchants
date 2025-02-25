package com.badbones69.crazyenchantments.api.managers;

import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.objects.BowEnchantment;
import com.badbones69.crazyenchantments.api.objects.PotionEffects;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BowEnchantmentManager {

    private final List<BowEnchantment> bowEnchantments = new ArrayList<>();

    public void load() {
        bowEnchantments.clear();

        if (CEnchantments.PIERCING.isActivated()) bowEnchantments.add(new BowEnchantment(CEnchantments.PIERCING, 2, false));

        if (CEnchantments.ICEFREEZE.isActivated()) bowEnchantments.add(new BowEnchantment(CEnchantments.ICEFREEZE, Arrays.asList(new PotionEffects(PotionEffectType.SLOW, 5 * 20, 1)), false));

        if (CEnchantments.VENOM.isActivated()) bowEnchantments.add(new BowEnchantment(CEnchantments.VENOM, Arrays.asList(new PotionEffects(PotionEffectType.POISON, 2 * 20, -1)), true));

        if (CEnchantments.SNIPER.isActivated()) bowEnchantments.add(new BowEnchantment(CEnchantments.SNIPER, Arrays.asList(new PotionEffects(PotionEffectType.POISON, 5 * 20, 1)), false));
    }
    
    public List<BowEnchantment> getBowEnchantments() {
        return bowEnchantments;
    }
}