package de.yolacraft.worldswap;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.Difficulty;

import java.util.ArrayList;
import java.util.Collection;

public class PlayerBackup {

    public DefaultedList<ItemStack> inventory;
    public Collection<StatusEffectInstance> effects;
    public float health;
    public int foodLevel;
    public float saturation;
    public int experience;
    public float expProgress;
    public int expLevel;
    public Difficulty difficulty;

    public PlayerBackup(ServerPlayerEntity player) {

        this.inventory = DefaultedList.ofSize(player.inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < player.inventory.size(); i++) {
            this.inventory.set(i, player.inventory.getStack(i).copy());
        }

        this.difficulty = player.getServerWorld().getDifficulty();

        this.effects = new ArrayList<>();
        for (StatusEffectInstance effect : player.getStatusEffects()) {
            this.effects.add(new StatusEffectInstance(effect));
        }

        this.health = player.getHealth();
        this.foodLevel = player.getHungerManager().getFoodLevel();
        this.saturation = player.getHungerManager().getSaturationLevel();

        this.experience = player.totalExperience;
        this.expLevel = player.experienceLevel;
        this.expProgress = player.experienceProgress;
    }

    public static void restore(ServerPlayerEntity player, PlayerBackup data) {

        for (int i = 0; i < player.inventory.size(); i++) {
            player.inventory.setStack(i, data.inventory.get(i).copy());
        }
        player.currentScreenHandler.sendContentUpdates();

        player.clearStatusEffects();
        for (StatusEffectInstance effect : data.effects) {
            player.addStatusEffect(new StatusEffectInstance(effect));
        }

        player.setHealth(data.health);

        player.getHungerManager().setFoodLevel(data.foodLevel);

        player.totalExperience = data.experience;
        player.experienceLevel = data.expLevel;
        player.experienceProgress = data.expProgress;

        player.getServer().setDifficulty(data.difficulty, false);
    }

}
