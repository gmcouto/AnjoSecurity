/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.anjosecurity;

import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

/**
 *
 * @author gabrielcouto
 */
public class AnjoSecurityPlayerListener extends PlayerListener {

    private final AnjoSecurity plugin;
    private final RegistrationControl rc;

    public AnjoSecurityPlayerListener(final AnjoSecurity instance) {
        plugin = instance;
        rc = plugin.getRegistrationControl();
    }
//Insert Player related code here
    /**
     * Called when a player joins a server
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerJoin(PlayerEvent event) {
        //System.out.println("Join");
        plugin.handlePlayerJoin(event.getPlayer());
    }

    /**
     * Called when a player leaves a server
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerQuit(PlayerEvent event) {
        //System.out.println("LogOut!");
        plugin.handlePlayerLogOut(event.getPlayer());
    }

    /**
     * Called when a player gets kicked from the server
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerKick(PlayerKickEvent event) {
    }

    /**
     * Called when a player sends a chat message
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
    }

    /**
     * Called when a player attempts to use a command
     *
     * @param event Relevant event details
     */
    public void onPlayerCommand(PlayerChatEvent event) {
        //plugin.handleCommand(event);
    }

    /**
     * Called when a player attempts to move location in a world
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
        if(event.isCancelled()){
            p.teleportTo(event.getFrom());
        }
    }

    /**
     * Called when a player attempts to teleport to a new location in a world
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerTeleport(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
        if(event.isCancelled()){
            p.teleportTo(event.getFrom());
        }
    }

    /**
     * Called when a player respawns
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {

    }

    /**
     * Called when a player uses an item
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerItem(PlayerItemEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
    }

    /**
     * Called when a player attempts to log in to the server
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerLogin(PlayerLoginEvent event) {
        //System.out.println("Login");
        //plugin.handlePlayerLogin(event);
    }

    /**
     * Called when a player throws an egg and it might hatch
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {

    }

    /**
     * Called when a player plays an animation, such as an arm swing
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerAnimation(PlayerAnimationEvent event) {

    }

    /**
     * Called when a player opens an inventory
     *
     * @param event Relevant event details
     */
    @Override
    public void onInventoryOpen(PlayerInventoryEvent event) {
    }

    /**
     * Called when a player changes their held item
     *
     * @param event Relevant event details
     */
    @Override
    public void onItemHeldChange(PlayerItemHeldEvent event) {

    }

    /**
     * Called when a player drops an item from their inventory
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
    }

    /**
     * Called when a player picks an item up off the ground
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
    }

    /**
     * Called when a player toggles sneak mode
     *
     * @param event Relevant event details
     */
    @Override
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
    }
}
