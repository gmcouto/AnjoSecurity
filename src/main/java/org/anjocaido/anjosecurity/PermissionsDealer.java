/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.anjosecurity;

import java.util.HashMap;
import java.util.Map;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
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
    public void markAsNotLoggedIn(String userName) {
        if (loaded) {
            Group lockDown = gm.getData().getGroup("NotLoggedIn");
            if(lockDown == null){
                lockDown = gm.getData().createGroup("NotLoggedIn");
            }
            gm.getOverloadedClassData().overloadUser(userName);
            gm.getData().getUser(userName).setGroup(lockDown);
            //System.out.println("Logou, grupo: "+gm.getData().getUser(userName).getGroup().getName());
        }
    }
    public void markAsNotRegistered(String userName) {
        if (loaded) {
            Group lockDown = gm.getData().getGroup("NotRegistered");
            if(lockDown == null){
                lockDown = gm.getData().createGroup("NotRegistered");
                Map<String, Object> vars = new HashMap<String, Object>();
                for(String key: gm.getData().getDefaultGroup().getVariables().getVarKeyList()){
                    vars.put(key, gm.getData().getDefaultGroup().getVariables().getVarObject(key));
                }
                lockDown.setVariables(vars);
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
