package ru.example.zombiehealth;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public final class ZombieHealthPlugin extends JavaPlugin implements Listener {

    private double minHealth;
    private double maxHealth;
    private boolean roundToInteger;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.minHealth      = getConfig().getDouble("min-health", 62.0);
        this.maxHealth      = getConfig().getDouble("max-health", 92.0);
        this.roundToInteger = getConfig().getBoolean("round-to-integer", true);

        if (minHealth > maxHealth) {
            getLogger().warning("min-health больше max-health, меняю местами.");
            double tmp = minHealth;
            minHealth = maxHealth;
            maxHealth = tmp;
        }

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("ZombieHealth включён. HP зомби: "
                + minHealth + "–" + maxHealth
                + (roundToInteger ? " (целые)" : " (дробные)"));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) {
            return;
        }

        AttributeInstance attr = zombie.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) {
            return;
        }

        double hp;
        if (roundToInteger) {
            hp = ThreadLocalRandom.current()
                    .nextInt((int) minHealth, (int) maxHealth + 1);
        } else {
            hp = ThreadLocalRandom.current().nextDouble(minHealth, maxHealth);
        }

        attr.setBaseValue(hp);
        zombie.setHealth(hp);
    }
}
