package com.ludas.plugin.clazz;

import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;

import java.util.Map;

public class Config {
    public static final String PARTICLE_BLACKFLASH_RED = "Ludas_Impact_Critical_BlackFlash_Red"; /*"Impact_Critical"*/
    public static final String PARTICLE_BLACKFLASH_BLUE = "Ludas_Impact_Critical_BlackFlash_Blue";
    public static final String SFX_AGILITY_CRIT = "SFX_GunPvP_Grenade_Frag_Death";

    public static boolean isItemAgilityRelated(Map<String, String[]> item) {
        return item.get("Family=Dagger") != null
                || item.get("Family=Axe") != null
                || item.get("Family=Sword") != null
                || item.get("Family=Bow") != null
                || item.get("Family=Spear") != null
                || item.get("Family=Arrow") != null
                || item.get("Family=Club") != null;
    }

    public static boolean isDamageCausePhysical(DamageCause damageCause) {
        return damageCause == DamageCause.PHYSICAL || damageCause.getInherits().equals(DamageCause.PHYSICAL.getId());
    }
}
