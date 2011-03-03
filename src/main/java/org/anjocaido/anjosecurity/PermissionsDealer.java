/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.anjosecurity;

import java.util.HashMap;
import java.util.Map;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 *
 * @author gabrielcouto
 */
public class PermissionsDealer {

    private Server server;
    private GroupManager gm;
    private boolean loaded = false;

    public PermissionsDealer(Server server) {
        Plugin plugin = server.getPluginManager().getPlugin("GroupManager");
        if (plugin != null && plugin instanceof GroupManager) {
            if(!plugin.isEnabled()){
                server.getPluginManager().enablePlugin(plugin);
            }
            gm = (GroupManager) plugin;
            loaded = true;
            System.out.println("AnjoSecurity loaded GroupManager Successfully!");
        } else {
            loaded = false;
            System.out.println("AnjoSecurity couldn't load GroupManager!");
        }
    }
    public void markAsNotLoggedIn(Player player) {
        if (loaded) {
            OverloadedWorldHolder perm = gm.getWorldsHolder().getWorldData(player);
            Group lockDown = perm.getGroup("NotLoggedIn");
            if(lockDown == null){
                lockDown = perm.createGroup("NotLoggedIn");
            }
            System.out.println("Antes "+perm.getUser(player.getName()).getGroupName());
            perm.overloadUser(player.getName());
            perm.getUser(player.getName()).setGroup(lockDown);
            System.out.println("Depois "+perm.getUser(player.getName()).getGroupName());
        }
    }
    public void markAsNotRegistered(Player player) {
        if (loaded) {
            OverloadedWorldHolder perm = gm.getWorldsHolder().getWorldData(player);
            Group lockDown = perm.getGroup("NotRegistered");
            if(lockDown == null){
                lockDown = perm.createGroup("NotRegistered");
                Map<String, Object> vars = new HashMap<String, Object>();
                for(String key: perm.getDefaultGroup().getVariables().getVarKeyList()){
                    vars.put(key, perm.getDefaultGroup().getVariables().getVarObject(key));
                }
                lockDown.setVariables(vars);
            }
            perm.overloadUser(player.getName());
            perm.getUser(player.getName()).setGroup(lockDown);
        }
    }
    public void restorePermissions(Player player){
        if (loaded) {
            OverloadedWorldHolder perm = gm.getWorldsHolder().getWorldData(player);
            System.out.println("Antes "+perm.getUser(player.getName()).getGroupName());
            perm.removeOverload(player.getName());
            System.out.println("Depois "+perm.getUser(player.getName()).getGroupName());
        }
    }
}
