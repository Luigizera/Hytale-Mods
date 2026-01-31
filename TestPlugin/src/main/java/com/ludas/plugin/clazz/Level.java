package com.ludas.plugin.clazz;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class Level {
    public static final BuilderCodec<Level> CODEC;
    private float experience;

    public Level() {
        experience = 0;
    }

    public Level(float experience) {
        this.experience = Math.max(0f, experience);
    }

    public Level(Level other) {
        experience = other.getExperience();
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = Math.max(0L, experience);
    }

    public int getLevel() {
        return XPTable.getLevelForXP(experience);
    }

    public float getCurrentLevelXP() {
        return XPTable.getXPInCurrentLevel(experience);
    }

    public float getXPToNextLevel() {
        return XPTable.getXPToNextLevel(experience);
    }

    public float getProgress() {
        return XPTable.getProgressToNextLevel(experience);
    }

    public boolean isMaxLevel() {
        return getLevel() >= XPTable.MAX_LEVEL;
    }

    public boolean addExperience(float amount) {
        if (amount <= 0) return false;

        int oldLevel = getLevel();
        experience += amount;
        int newLevel = getLevel();

        return newLevel > oldLevel;
    }

    @NullableDecl
    @Override
    public Level clone() {
        return new Level(this);
    }

    @Override
    public String toString() {
        return "Level{level=" + getLevel() +
                ", experience=" + experience +
                ", toNext=" + getXPToNextLevel() + "}";
    }

    static {
        CODEC = BuilderCodec
                .builder(Level.class, Level::new)
                .append(
                        new KeyedCodec<>("Experience", Codec.FLOAT),
                        (component, value) -> component.experience = value,
                        component -> component.experience
                )
                .add()
                .build();
    }

    public static final class XPTable {
        private static final float[] LEVEL_THRESHOLDS = {
                0,
                100,
                300,
                500,
                700,
                900,
                1100,
                1300,
                1500,
                1700,
                1900,
                2100,
                2300,
                2500,
                2700,
                2900,
                3100,
                3300,
                3500,
                3700
        };

        public static final int MAX_LEVEL = LEVEL_THRESHOLDS.length;
        public static final int START_LEVEL = 1;

        private XPTable() {
        }

        public static float[] levelTable() {
            float[] arr = new float[MAX_LEVEL];
            arr[0] = 0;
            for (int i = 1; i < MAX_LEVEL; ++i) {
                arr[i] = fibb(i);
            }
            return arr;
        }

        private static int fibb(int i) {
            return (i + i - 1) * 100;
        }

        public static int getLevelForXP(float totalXP) {
            if (totalXP < 0) return START_LEVEL;

            for (int level = MAX_LEVEL; level >= START_LEVEL; level--) {
                if (totalXP >= LEVEL_THRESHOLDS[level - 1]) {
                    return level;
                }
            }
            return START_LEVEL;
        }

        public static float getXPForLevel(int level) {
            if (level < START_LEVEL) return 0L;
            if (level > MAX_LEVEL) return LEVEL_THRESHOLDS[MAX_LEVEL - 1];
            return LEVEL_THRESHOLDS[level - 1];
        }

        public static float getXPInCurrentLevel(float totalXP) {
            var level = getLevelForXP(totalXP);
            return totalXP - getXPForLevel(level);
        }

        public static float getXPToNextLevel(float totalXP) {
            var level = getLevelForXP(totalXP);
            if (level >= MAX_LEVEL) return 0L;
            return LEVEL_THRESHOLDS[level] - totalXP;
        }

        public static float getProgressToNextLevel(float totalXP) {
            var level = getLevelForXP(totalXP);
            if (level >= MAX_LEVEL) return 1.0f;

            var currentThreshold = LEVEL_THRESHOLDS[level - 1];
            var nextThreshold = LEVEL_THRESHOLDS[level];
            var xpInLevel = totalXP - currentThreshold;
            var xpNeeded = nextThreshold - currentThreshold;

            return xpNeeded == 0 ? 1.0f : (float) xpInLevel / xpNeeded;
        }
    }
}