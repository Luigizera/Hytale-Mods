package com.ludas.plugin.clazz;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.util.NotificationUtil;

import java.util.Map;

public class Config {
    public static final String PARTICLE_BLACKFLASH_RED = "Ludas_Impact_Critical_BlackFlash_Red"; /*"Impact_Critical"*/
    public static final String PARTICLE_BLACKFLASH_BLUE = "Ludas_Impact_Critical_BlackFlash_Blue";
    public static final String SFX_AGILITY_CRIT = "SFX_GunPvP_Grenade_Frag_Death";
    public static final String ICON_PERK_MAIN = "NotificationIcons/Ludas_Icon_Perk_Main.png";
    public static final String ICON_PERK_STRENGTH = "NotificationIcons/Ludas_Icon_Perk_Strength.png";
    public static final String ICON_PERK_MAGIC = "NotificationIcons/Ludas_Icon_Perk_Magic.png";
    public static final String ICON_PERK_AGILITY = "NotificationIcons/Ludas_Icon_Perk_Agility.png";
    public static final String ICON_PERK_VITALITY = "NotificationIcons/Ludas_Icon_Perk_Vitality.png";
    public static final String ICON_PERK_POISON = "NotificationIcons/Ludas_Icon_Perk_Poison.png";

    public static boolean isItemAgilityRelated(Map<String, String[]> item) {
        return item.get("Family=Dagger") != null
                || item.get("Family=Axe") != null
                || item.get("Family=Sword") != null
                || item.get("Family=Bow") != null
                || item.get("Family=CrossBow") != null
                || item.get("Family=Spear") != null
                || item.get("Family=Arrow") != null
                || item.get("Family=Club") != null;
    }

    public static boolean isDamageCausePhysical(DamageCause damageCause) {
        return damageCause == DamageCause.PHYSICAL || damageCause.getInherits().equals(DamageCause.PHYSICAL.getId());
    }

    public static void perkUnlockedNotification(PacketHandler packet, String icon, String perkName, String perkType) {
        Message primaryMessage;
        Message secondaryMessage = Message.translation("server.perks.ludas.notification.unlocked").color("#A8B0B7");

        switch (perkType) {
            case PerkType.MAIN: {
                primaryMessage = Message.translation("server.perks.ludas.notification.unlocked.main." + perkName);
                break;
            }
            case PerkType.STRENGTH: {
                primaryMessage = Message.translation("server.perks.ludas.notification.unlocked.strength." + perkName);
                break;
            }
            case PerkType.AGILITY: {
                primaryMessage = Message.translation("server.perks.ludas.notification.unlocked.agility." + perkName);
                break;
            }
            case PerkType.VITALITY: {
                primaryMessage = Message.translation("server.perks.ludas.notification.unlocked.vitality." + perkName);
                break;
            }
            case PerkType.MAGIC: {
                primaryMessage = Message.translation("server.perks.ludas.notification.unlocked.magic." + perkName);
                break;
            }
            default: {
                primaryMessage = Message.translation("server.perks.ludas.notification.unlocked.unknown");
                break;
            }
        }

        NotificationUtil.sendNotification(
                packet,
                primaryMessage.color("#DBDBDC"),
                secondaryMessage,
                icon);
    }
}
