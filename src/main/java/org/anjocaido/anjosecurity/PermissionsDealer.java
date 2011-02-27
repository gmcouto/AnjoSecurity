/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.anjosecurity;

import org.anjocaido.groupmanager.data.*;
import org.anjocaido.groupmanager.permissions.*;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Server;
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
            gm = (GroupManager) plugin;
            loaded = true;
            System.out.println("AnjoSecurity : GroupManager connection well etablished");
        } else {
            loaded = false;
            System.out.println("AnjoSecurity : Failed to connect to GroupManager");
        }
    }
    public void markAsNotLoggedIn(String userName) {
        if (loaded) {
            Group lockDown = gm.getData().getGroup("NotLoggedIn");
            if(lockDown == null){
                lockDown = gm.getData().createGroup("NotLoggedIn");
            }
            gm.getOverloadedClassData().overloadUser(userName);
            gm.getData().getUser(userName).setGroup(lockDown);
            
        }
    }
    public void markAsNotRegistered(String userName) {
        if (loaded) {
            Group lockDown = gm.getData().getGroup("NotRegistered");
            if(lockDown == null){
                lockDown = gm.getData().createGroup("NotRegistered");
                //lockDown.variables = gm.getData().getDefaultGroup().variables;
            }
            gm.getOverloadedClassData().overloadUser(userName);
            gm.getData().getUser(userName).setGroup(lockDown);
            
        }
    }
    public void restorePermissions(String userName){
        if (loaded) {
            gm.getOverloadedClassData().removeOverload(userName);
            
        }
    }
}
