/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.anjosecurity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author gabrielcouto
 */
public class AnjoSecurity extends JavaPlugin {

    private final AnjoSecurityPlayerListener playerListener;
    private final AnjoSecurityBlockListener blockListener;
    private final HashMap<Player, Boolean> debugees;
    private Properties settings;
    private Properties queries;
    private RegistrationControl rc;
    private String settingsFileName = "settings.properties";
    private Map<String, Long> lastAlert = new HashMap<String, Long>();

    public AnjoSecurity(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        File settingsFile = new File(this.getDataFolder(), settingsFileName);
        if (!(settingsFile).exists()) {
            InputStream is = this.getClassLoader().getResourceAsStream("settings.properties");
            settings = new Properties();
            try {
                settings.load(is);
                OutputStream os = new FileOutputStream(settingsFile);
                settings.store(os, "Settings for AnjoSecurity. 'msg keys are pure strings, edit as you please. 'opt keys might not be always string, you should be careful.  opt-main-admins are the admins that can delete registrations, separate their names with commas.");
            } catch (IOException ex) {
                Logger.getLogger(AnjoSecurity.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                InputStream is = new FileInputStream(settingsFile);
                settings = new Properties();
                settings.load(is);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AnjoSecurity.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                InputStream is = this.getClassLoader().getResourceAsStream("settings.properties");
                settings = new Properties();
                try {
                    settings.load(is);
                } catch (IOException ex1) {
                    Logger.getLogger(AnjoSecurity.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        InputStream is = this.getClassLoader().getResourceAsStream("queries.properties");
        queries = new Properties();
        try {
            queries.load(is);
        } catch (IOException ex) {
            Logger.getLogger(AnjoSecurity.class.getName()).log(Level.SEVERE, null, ex);
        }
        rc = new RegistrationControl(this);

        //
        debugees = new HashMap<Player, Boolean>();
        playerListener = new AnjoSecurityPlayerListener(this);
        blockListener = new AnjoSecurityBlockListener(this);

    }

    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println("Goodbye world!");
    }

    public void onEnable() {
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


        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }

    public String getQuery(String name) {
        return queries.getProperty(name);
    }

    public String getSetting(String name) {
        return settings.getProperty(name);
    }

    public boolean getBoolSetting(String name) {
        String setting = getSetting(name);
        return Boolean.parseBoolean(setting);
    }

    public RegistrationControl getRegistrationControl() {
        return rc;
    }

    public void handleCancellable(Player p, Cancellable event) {
        PlayerStatus status = rc.getStatus(p);
        if (status.equals(PlayerStatus.LOGGED_IN)) {
        } else if (status.equals(PlayerStatus.NOT_LOGGED_IN)) {
            event.setCancelled(true);
            alertLogin(p);
            p.setHealth(20);
        } else if (status.equals(PlayerStatus.NOT_REGISTERED)) {
            if (getBoolSetting("opt-guests-lockdown")) {
                event.setCancelled(true);
                alertRegister(p);
                p.setHealth(20);
            }
        }
    }

    public void handlePlayerJoin(Player p) {
        p.setHealth(20);
        if (rc.isRegistered(p)) {
            if (rc.logInByTime(p)) {
                p.sendMessage(ChatColor.YELLOW + getSetting("msg-login-time"));
            } else {
                p.sendMessage(ChatColor.YELLOW + getSetting("msg-welcome-user"));
            }
        } else {
            p.sendMessage(ChatColor.YELLOW + getSetting("msg-welcome-guest"));
            if (getBoolSetting("opt-guests-resetatlogin")) {
                p.teleportTo(p.getWorld().getSpawnLocation());
                p.getInventory().clear();
                p.sendMessage(ChatColor.LIGHT_PURPLE + getSetting("msg-action-resetting"));
            }
        }
    }

    public void handlePlayerLogOut(Player p) {
        if (rc.isLoggedIn(p)) {
            rc.logOut(p);
        }
    }

    public void handleCommand(PlayerChatEvent event) {
        Player p = event.getPlayer();
        String[] command = event.getMessage().split(" ");
        PlayerStatus status = rc.getStatus(p);
        if (status.equals(PlayerStatus.LOGGED_IN)) {
            //everything OK
            if (command[0].equalsIgnoreCase("/reset")) {
                event.setMessage("/reset ********");
                if (command.length != 2) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /reset <password>");
                } else {
                    if (rc.unregisterPlayer(p, command[1])) {
                        p.sendMessage(ChatColor.YELLOW + getSetting("msg-unregister-successful"));
                    } else {
                        p.sendMessage(ChatColor.LIGHT_PURPLE + getSetting("msg-unregister-failed"));
                    }
                    event.setCancelled(false);
                }
            } else if (command[0].equalsIgnoreCase("/adminreset")) {
                if (command.length != 2) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /reset <username>");
                } else {
                    event.setMessage("/adminreset " + command[1]);
                    String[] admins = getSetting("opt-main-admins").split(",");
                    boolean isAdmin = false;
                    for (String adm : admins) {
                        if (adm.equalsIgnoreCase(p.getName())) {
                            isAdmin = true;
                            break;
                        }
                    }
                    if (isAdmin) {
                        if (rc.deletePlayer(command[1])) {
                            p.sendMessage(ChatColor.YELLOW + getSetting("msg-delete-successful"));
                        } else {
                            p.sendMessage(ChatColor.LIGHT_PURPLE + getSetting("msg-delete-failed"));
                        }
                    }
                    event.setCancelled(false);
                }
            }
        } else if (status.equals(PlayerStatus.NOT_LOGGED_IN)) {
            //should deny actions not expected
            if (command[0].equalsIgnoreCase("/login")) {
                event.setMessage("/login ********");
                if (command.length != 2) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /login <password>");
                } else {
                    if (rc.logInByPass(p, command[1])) {
                        p.sendMessage(ChatColor.YELLOW + getSetting("msg-login-pass"));
                        p.setHealth(20);
                    } else {
                        p.sendMessage(ChatColor.LIGHT_PURPLE + getSetting("msg-login-incorrect"));
                        p.setHealth(20);
                    }
                    event.setCancelled(false);
                }
            } else {
                //any command other than /login while not logged in should be ignored
                event.setCancelled(true);
                event.setMessage("/unallowedcommand");
                alertLogin(p);
            }
        } else if (status.equals(PlayerStatus.NOT_REGISTERED)) {
            //need verify if not registered users are denied do other commands
            if (command[0].equalsIgnoreCase("/register")) {
                event.setMessage("/register ********");
                if (command.length != 2) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "Usage: /register <password>");
                } else {
                    if (rc.registerPlayer(p, command[1])) {
                        p.sendMessage(ChatColor.YELLOW + getSetting("msg-register-successful"));
                        p.sendMessage(ChatColor.RED + "Remember, your password is: " + command[1]);
                    } else {
                        p.sendMessage(ChatColor.LIGHT_PURPLE + getSetting("msg-register-failed"));
                    }
                    event.setCancelled(false);
                }
            } else {
                //here you should allow other commands to run (or not)
                if (!getBoolSetting("opt-guests-summoncommands")) {
                    event.setCancelled(true);
                    event.setMessage("/unallowedcommand");
                    alertRegister(p);
                }
            }
        }
    }

    public void alertLogin(Player p) {
        Long lastA = 0L;
        if (lastAlert.containsKey(p.getName().toLowerCase())) {
            lastA = lastAlert.remove(p.getName().toLowerCase());
        }
        if ((lastA + 5000) < System.currentTimeMillis()) {
            p.sendMessage(ChatColor.LIGHT_PURPLE + getSetting("msg-unallowed-needlogin"));
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
            p.sendMessage(ChatColor.LIGHT_PURPLE + getSetting("msg-unallowed-needregister"));
            lastA = System.currentTimeMillis();
        }
        lastAlert.put(p.getName().toLowerCase(), lastA);
    }
}
