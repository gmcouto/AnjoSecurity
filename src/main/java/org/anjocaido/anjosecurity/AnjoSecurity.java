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
                settings.store(os, "Settings of this plugin");
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
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_TOGGLE_SNEAK, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);

        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_INTERACT, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);


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

    public RegistrationControl getRegistrationControl() {
        return rc;
    }

    public void handleCancellable(Player p, Cancellable event) {
        if (rc.isLoggedIn(p)) {
            //everything OK
        } else {
            if (rc.isRegistered(p)) {
                //deny actions
                event.setCancelled(true);
            } else {
                //verify if not registered users are denied
            }
        }
    }

    public void handleCanBuild(Player p, BlockCanBuildEvent event) {
        if (rc.isLoggedIn(p)) {
            //everything OK
        } else {
            if (rc.isRegistered(p)) {
                //deny actions
                event.setBuildable(false);
            } else {
                //verify if not registered users are denied
            }
        }
    }
    public void handlePlayerJoin(Player p) {
        if (rc.isRegistered(p)) {
            if (rc.logInByTime(p)) {
                p.sendMessage(getSetting("msg-login-time"));
            } else {
                p.sendMessage(getSetting("msg-welcome-user"));
            }
        } else {
            p.sendMessage(getSetting("msg-welcome-guest"));
        }
    }
    public void handlePlayerLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        if (rc.isRegistered(p)) {
            if (rc.logInByTime(p)) {
                p.sendMessage(getSetting("msg-login-time"));
            } else {
                p.sendMessage(getSetting("msg-welcome-user"));
            }
        } else {
            p.sendMessage(getSetting("msg-welcome-guest"));
        }
    }
    public void handlePlayerLogOut(Player p) {
        if (rc.isLoggedIn(p)) {
            rc.logOut(p);
        }
    }
    public void handleCommand(PlayerChatEvent event){
        Player p = event.getPlayer();
        String[] command = event.getMessage().split(" ");
        if (rc.isLoggedIn(p)) {
            //leaves blank, it means everything is OK
        } else {
            if (rc.isRegistered(p)) {
                //should deny actions not expected


                if(command[0].equalsIgnoreCase("/login")){
                    if(command.length!=2){
                        p.sendMessage(ChatColor.LIGHT_PURPLE+"Usage: /login <password>");
                    } else {
                        if(rc.logInByPass(p, command[1])){
                            p.sendMessage(ChatColor.YELLOW+getSetting("msg-login-pass"));
                        } else {
                            p.sendMessage(ChatColor.LIGHT_PURPLE+getSetting("msg-login-incorrect"));
                        }
                    }
                } else {
                    //any command other than /login while not logged in should be ignored
                    event.setCancelled(true);
                }
            } else {
                //need verify if not registered users are denied do other commands
                if(command[0].equalsIgnoreCase("/register")){
                    if(command.length!=2){
                        p.sendMessage(ChatColor.LIGHT_PURPLE+"Usage: /register <password>");
                    } else {
                        if(rc.registerPlayer(p, command[1])){
                            p.sendMessage(ChatColor.YELLOW+getSetting("msg-register-successful").replaceFirst("?", (ChatColor.RED+command[1]+ChatColor.YELLOW))+" ");
                        } else {
                            p.sendMessage(ChatColor.YELLOW+getSetting("msg-register-failed"));
                        }
                    }
                } else {
                    //here you should allow other commands to run (or not)
                    event.setCancelled(true);
                }
            }
        }
    }
}
