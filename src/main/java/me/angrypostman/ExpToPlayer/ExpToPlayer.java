package me.angrypostman.ExpToPlayer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class ExpToPlayer extends JavaPlugin {

    private static ExpToPlayer plugin = null;

    public static ExpToPlayer getPlugin() {
        return plugin;
    }

    private Multimap<UUID, UUID> bottles = ArrayListMultimap.create();

    @Override
    public void onEnable() {

        ExpToPlayer.plugin = this;

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new Listener() {

            private ExpToPlayer plugin = ExpToPlayer.getPlugin();
            private Multimap<UUID, UUID> bottles = plugin.getBottles();

            @EventHandler
            public void onProjectileThrow(ProjectileLaunchEvent event) {
                if(event.getEntity() instanceof ThrownExpBottle && event.getEntity().getShooter() instanceof Player) {
                    ThrownExpBottle bottle = (ThrownExpBottle)event.getEntity();
                    Player player = (Player)bottle.getShooter();
                    bottles.put(player.getUniqueId(), bottle.getUniqueId());
                }

            }

            @EventHandler
            public void onExpSmash(ExpBottleEvent event) {
                ThrownExpBottle bottle = event.getEntity();
                if(bottle.getShooter() instanceof Player) {
                    Player player = (Player)bottle.getShooter();
                    if(this.bottles.get(player.getUniqueId()) != null && this.bottles.get(player.getUniqueId()).contains(bottle.getUniqueId())) {
                        int experience = event.getExperience();
                        player.giveExp(experience);

                        event.setExperience(0);
                        event.setShowEffect(false);

                        bottle.remove();
                        bottles.remove(player.getUniqueId(), bottle.getUniqueId());
                    }
                }

            }
        }, this);
    }

    @Override
    public void onDisable() {

        bottles.clear();
        HandlerList.unregisterAll(this);
        ExpToPlayer.plugin = null;

    }

    public Multimap<UUID, UUID> getBottles() {
        return bottles;
    }
}
