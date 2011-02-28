/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.anjosecurity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.*;
import org.bukkit.event.Event.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import com.nijiko.permissions.PermissionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author gabrielcouto
 */
public class AnjoSecurity extends JavaPlugin {

    private AnjoSecurityPlayerListener playerListener;
    private AnjoSecurityBlockListener blockListener;
    private AnjoSecurityEntityListener entityListener;
    public Settings settings;
    private Properties queries;
    private RegistrationControl rc;
    private String settingsFileName = "config.yml";
    private File settingsFile;
    private Map<String, Long> lastAlert = new HashMap<String, Long>();
    private boolean registrationsAllowed;
    public static PermissionHandler Permissions = null;
    public PermissionsDealer permDealer;
    public ArrayList<String> godModeList = new ArrayList<String>();
    ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(2);
    public ArrayList<String> allowedList = new ArrayList<String>();

    public class GodModeRemover implements Runnable {

        public String name;

        public GodModeRemover(String playerName) {
            name = playerName;
        }

        @Override
        public void run() {
            //System.out.println("NOT GOD ANYMORE");
            AnjoSecurity.this.godModeList.remove(name);
        }
    }

    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        //System.out.println("Goodbye world!");
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");

    }

    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        settingsFile = new File(this.getDataFolder(), settingsFileName);
        //
        //
        //
        settings = new Settings(settingsFile);
        registrationsAllowed = settings.isRegistrationEnabled();
        //
        loadQueries();
        //
        rc = new RegistrationControl(this);
        //
        playerListener = new AnjoSecurityPlayerListener(this);
        blockListener = new AnjoSecurityBlockListener(this);
        entityListener = new AnjoSecurityEntityListener(this);


        // TODO: Place any custom enable code here including the registration of any events
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_TOGGLE_SNEAK, playerListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Highest, this);
        //pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Highest, this);

        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.BLOCK_INTERACT, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Highest, this);

        pm.registerEvent(Event.Type.ENTITY_DAMAGED, entityListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Priority.Highest, this);


        //Permissions
        permDealer = new PermissionsDealer(this.getServer());

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public String getQuery(String name) {
        return queries.getProperty(name);
    }

    public RegistrationControl getRegistrationControl() {
        return rc;
    }

    public void handleCancellable(Player p, Cancellable event) {
        if (p == null) {
            event.setCancelled(true);
            return;
        }
        if (event instanceof EntityDamageEvent && godModeList.contains(p.getName().toLowerCase())) {
            event.setCancelled(true);
        }
        PlayerStatus status = rc.getStatus(p);
        if (status.equals(PlayerStatus.LOGGED_IN)) {
            if (godModeList.contains(p.getName().toLowerCase())) {
                if (p.getHealth() != 0) {
                    p.setHealth(20);
                }
            }
        } else if (status.equals(PlayerStatus.NOT_LOGGED_IN)) {
            event.setCancelled(true);
            alertLogin(p);
            //if (p.getHealth() > 0) {
            p.setHealth(20);
            //}

        } else if (status.equals(PlayerStatus.NOT_REGISTERED)) {
            if (settings.isOptGuestsLockdown()) {
                event.setCancelled(true);
                alertRegister(p);
                //if (p.getHealth() > 0) {
                p.setHealth(20);
                //}
            }
        }

        //System.out.println("Returned"+Boolean.toString(event.isCancelled()));
    }

    public void handleEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            PlayerStatus status = rc.getStatus(p);
            if (status.equals(PlayerStatus.LOGGED_IN)) {
                p.getInventory().clear();
            } else if (status.equals(PlayerStatus.NOT_LOGGED_IN)) {
                event.getDrops().clear();
            } else if (status.equals(PlayerStatus.NOT_REGISTERED)) {
                if (settings.isOptGuestsLockdown()) {
                    event.getDrops().clear();
                } else {
                    p.getInventory().clear();
                }
            }
        }
    }

    public void handlePlayerJoin(Player p) {
        if (p != null) {
            if (rc.isRegistered(p)) {
                if (rc.logInByTime(p)) {
                    p.sendMessage(ChatColor.YELLOW + settings.getMsgLoginTime());
                } else {
                    if (permDealer == null) {
                        permDealer = new PermissionsDealer(this.getServer());
                    }
                    p.sendMessage(ChatColor.YELLOW + settings.getMsgWelcomeUser());
                    if (permDealer != null) {
                        permDealer.markAsNotLoggedIn(p.getName());
                    }
                    p.setHealth(20);
                }
            } else {
                p.sendMessage(ChatColor.YELLOW + settings.getMsgWelcomeGuest());
                if (settings.isOptGuestsResetAtLogin()) {
                    World w = p.getWorld();
                    if (w != null) {
                        Location l = w.getSpawnLocation();
                        if (l != null) {
                            p.teleportTo(l);
                        }
                    }
                    PlayerInventory inv = p.getInventory();
                    if (inv != null) {
                        p.getInventory().clear();
                    }
                    p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgActionResetting());
                }
                if (!settings.isOptGuestsSummonCommands()) {
                    permDealer.markAsNotRegistered(p.getName());
                }
            }
        }
    }

    public void handlePlayerLogOut(Player p) {
        if (p != null) {
            if (rc.isLoggedIn(p)) {
                rc.logOut(p);
            }
        }
    }

    public void handleCommand(PlayerChatEvent event) {
        //System.out.println("Event Cancellable: " + event.getClass().getName());
        //System.out.println("Message: " + event.getMessage());
        Player p = event.getPlayer();
        if (p == null) {
            event.setCancelled(true);
            return;
        }
        PlayerStatus status = rc.getStatus(p);
        if (status.equals(PlayerStatus.LOGGED_IN)) {
            //everything OK
            handleCommandsWhileLoggedIn(event);
        } else if (status.equals(PlayerStatus.NOT_LOGGED_IN)) {
            //should deny actions not expected
            handleCommandsWhileNOTLoggetIn(event);
        } else if (status.equals(PlayerStatus.NOT_REGISTERED)) {
            handleCommandsWhileGuestMode(event);
        }
        //System.out.println("Returned: " + Boolean.toString(event.isCancelled()));
    }

    public void handleCommandsWhileLoggedIn(PlayerChatEvent event) {
        Player p = event.getPlayer();
        String[] command = event.getMessage().split(" ");
        if (command[0].equalsIgnoreCase("/reset")) {
            event.setMessage("/reset ********");
            if (command.length != 2) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /reset <password>");
            } else {
                if (rc.unregisterPlayer(p, command[1])) {
                    p.sendMessage(ChatColor.YELLOW + settings.getMsgUnregisterSucessful());
                } else {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgUnregisterFailed());
                }
            }
        } else if (command[0].equalsIgnoreCase("/adminreset")) {
            if (command.length != 2) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /reset <username>");
            } else {
                if (isAdmin(p.getName())) {
                    if (rc.deletePlayer(command[1])) {
                        p.sendMessage(ChatColor.YELLOW + settings.getMsgDeleteSucessful());
                    } else {
                        p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgDeleteFailed());
                    }
                } else {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgNotAdmin());
                }
            }
        } else if (command[0].equalsIgnoreCase("/adminallow")) {
            if (command.length != 2) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /adminallow <username>");
            } else {
                if (isAdmin(p.getName())) {
                    List<Player> victim = this.getServer().matchPlayer(command[1]);
                    if (victim.size() == 1) {
                        allowedList.add(victim.get(0).getName().toLowerCase());
                        p.sendMessage(ChatColor.YELLOW + settings.getMsgAllowedSuccessful());
                        victim.get(0).sendMessage(ChatColor.GOLD + settings.getMsgAlertAllowed());
                    } else {
                        p.sendMessage(ChatColor.YELLOW + settings.getMsgAllowedFailed());
                    }
                } else {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgNotAdmin());
                }
            }
        } else if (command[0].equalsIgnoreCase("/toggleregistration")) {
            if (command.length != 1) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /toggleregistration");
            } else {
                if (isAdmin(p.getName())) {
                    registrationsAllowed = !registrationsAllowed;
                    settings.setRegistrationEnabled(registrationsAllowed);
                    settings.save();
                    if (registrationsAllowed) {
                        p.sendMessage(ChatColor.YELLOW + settings.getMsgRegistrationActivated());
                    } else {
                        p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgRegistrationDeactivated());
                    }
                }
            }
        }
    }

    public void handleCommandsWhileNOTLoggetIn(PlayerChatEvent event) {
        Player p = event.getPlayer();
        String[] command = event.getMessage().split(" ");
        if (command[0].equalsIgnoreCase("/login")) {
            event.setMessage("/login ********");
            if (command.length != 2) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /login <password>");
            } else {
                if (rc.logInByPass(p, command[1])) {
                    p.sendMessage(ChatColor.YELLOW + settings.getMsgLoginPass());
                    permDealer.restorePermissions(p.getName());
                    godModeList.add(p.getName().toLowerCase());
                    scheduler.schedule(new GodModeRemover(p.getName().toLowerCase()), 5, TimeUnit.SECONDS);
                } else {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgLoginIcorrect());
                }
            }
        } else {
            event.setMessage("/unallowedcommand");
            event.setCancelled(true);
        }
    }

    public void handleCommandsWhileGuestMode(PlayerChatEvent event) {
        Player p = event.getPlayer();
        String[] command = event.getMessage().split(" ");
        //need verify if not registered users are denied do other commands
        if (command[0].equalsIgnoreCase("/register")) {
            event.setMessage("/register ********");
            if (registrationsAllowed || allowedList.contains(p.getName().toLowerCase())) {

                if (command.length != 2) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /register <password>");
                } else {
                    if (rc.registerPlayer(p, command[1])) {
                        p.sendMessage(ChatColor.YELLOW + settings.getMsgRegisterSucessful());
                        p.sendMessage(ChatColor.RED + "Remember, your password is: " + command[1]);
                        permDealer.restorePermissions(p.getName());
                        permDealer.markAsNotRegistered(p.getName());
                        if (allowedList.contains(p.getName().toLowerCase())) {
                            allowedList.remove(p.getName().toLowerCase());
                        }
                    } else {
                        p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgRegisterFailed());
                    }
                }
            } else {
                p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgRegistrationUnallowed());
            }
        } else {
            if (!settings.isOptGuestsSummonCommands() || settings.isOptGuestsLockdown()) {
                event.setMessage("/unallowedcommand");
                event.setCancelled(true);
            }
        }
    }

    public void alertLogin(Player p) {
        Long lastA = 0L;
        if (lastAlert.containsKey(p.getName().toLowerCase())) {
            lastA = lastAlert.remove(p.getName().toLowerCase());
        }
        if ((lastA + 5000) < System.currentTimeMillis()) {
            p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgUnallowedNeedLogin());
            lastA = System.currentTimeMillis();
        }
        lastAlert.put(p.getName().toLowerCase(), lastA);
    }

    public void alertRegister(Player p) {
        Long lastA = 0L;
        if (lastAlert.containsKey(p.getName().toLowerCase())) {
            lastA = lastAlert.remove(p.getName().toLowerCase());
        }
        if ((lastA + 5000) < System.currentTimeMillis()) {
            p.sendMessage(ChatColor.LIGHT_PURPLE + settings.getMsgUnallowedNeedRegister());
            lastA = System.currentTimeMillis();
        }
        lastAlert.put(p.getName().toLowerCase(), lastA);
    }

    private boolean isAdmin(String playerName) {
        if (playerName.length() < 3) {
            return false;
        }
        List<String> admins = settings.getOptMainAdmins();
        for (String adm : admins) {
            if (adm.equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    private void loadQueries() {
        try {
            InputStream queriesStream = this.getClassLoader().getResourceAsStream("queries.properties");
            queries = new Properties();
            queries.load(queriesStream);
        } catch (IOException ex) {
            Logger.getLogger(AnjoSecurity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
