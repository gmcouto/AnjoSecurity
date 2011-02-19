/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.anjosecurity;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 *
 * @author gabrielcouto
 */
public class AnjoSecurityEntityListener extends EntityListener {

    private AnjoSecurity plugin;
    private final RegistrationControl rc;

    public AnjoSecurityEntityListener(AnjoSecurity plugin) {
        this.plugin = plugin;
        rc = plugin.getRegistrationControl();
    }

    @Override
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            plugin.handleCancellable(p, event);
        }
    }

    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            plugin.handleCancellable(p, event);
        }
    }

    public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            plugin.handleCancellable(p, event);
        }
    }

    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            plugin.handleCancellable(p, event);
        }
    }

    public void onEntityDeath(EntityDeathEvent event) {
        plugin.handleEntityDeath(event);
    }

    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player p = (Player) event.getTarget();
            plugin.handleCancellable(p, event);
        }
    }
}
