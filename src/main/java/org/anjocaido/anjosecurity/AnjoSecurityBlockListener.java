/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.anjosecurity;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 *
 * @author gabrielcouto
 */
public class AnjoSecurityBlockListener extends BlockListener {

    private final AnjoSecurity plugin;
    private final RegistrationControl rc;

    public AnjoSecurityBlockListener(final AnjoSecurity plugin) {
        this.plugin = plugin;
        rc = plugin.getRegistrationControl();
    }

    //put all Block related code here
    /**
     * Called when a block is damaged (or broken)
     *
     * @param event Relevant event details
     */
    public void onBlockDamage(BlockDamageEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
    }

    /**
     * Called when we try to place a block, to see if we can build it
     */
    public void onBlockCanBuild(BlockCanBuildEvent event) {
    }

    /**
     * Called when a block flows (water/lava)
     *
     * @param event Relevant event details
     */
    public void onBlockFlow(BlockFromToEvent event) {
    }

    /**
     * Called when a block gets ignited
     *
     * @param event Relevant event details
     */
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player p = event.getPlayer();
        if (p != null) {
            plugin.handleCancellable(p, event);
        }
    }

    /**
     * Called when block physics occurs
     *
     * @param event Relevant event details
     */
    public void onBlockPhysics(BlockPhysicsEvent event) {
    }

    /**
     * Called when a player places a block
     *
     * @param event Relevant event details
     */
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
    }

    /**
     * Called when a block is interacted with
     *
     * @param event Relevant event details
     */
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.isPlayer()) {
            Player p = (Player) event.getEntity();
            plugin.handleCancellable(p, event);
        }
    }

    /**
     * Called when a player right clicks a block
     *
     * @param event Relevant event details
     */
    public void onBlockRightClick(BlockRightClickEvent event) {
    }

    /**
     * Called when redstone changes
     * From: the source of the redstone change
     * To: The redstone dust that changed
     *
     * @param event Relevant event details
     */
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
    }

    /**
     * Called when leaves are decaying naturally
     *
     * @param event Relevant event details
     */
    public void onLeavesDecay(LeavesDecayEvent event) {
    }

    /**
     * Called when a sign is changed
     *
     * @param event Relevant event details
     */
    public void onSignChange(SignChangeEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
    }

    /**
     * Called when a block is destroyed from burning
     *
     * @param event Relevant event details
     */
    public void onBlockBurn(BlockBurnEvent event) {
    }

    /**
     * Called when a block is destroyed by a player.
     *
     * @param event Relevant event details
     */
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        plugin.handleCancellable(p, event);
    }
}
