/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.anjosecurity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 *
 * @author gabrielcouto
 */
public class RegistrationControl {

    private static RegistrationControl mainInstance;
    private final Connection connDB;
    private final AnjoSecurity plugin;
    private Map<String, Player> loggedUsers = new HashMap<String, Player>();

    public RegistrationControl(AnjoSecurity plugin) {
        this.plugin = plugin;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RegistrationControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Connection temp = null;
        try {
            temp = DriverManager.getConnection("jdbc:sqlite:AnjoSecurityDB.db");
        } catch (SQLException ex) {
            Logger.getLogger(RegistrationControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        connDB = temp;
        if (temp == null) {
            return;
        }
        try {
            Statement statement = connDB.createStatement();
            String query = plugin.getQuery("sql-create-table");
            System.out.println("Executing "+query);
            statement.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(RegistrationControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean registerPlayer(Player player, String password) {
        try {
            PreparedStatement stm = connDB.prepareStatement(plugin.getQuery("sql-register"));
            stm.setString(1, player.getName());
            stm.setString(2, md5(password));
            stm.setLong(3, System.currentTimeMillis());
            stm.setLong(4, System.currentTimeMillis());
            stm.setString(5, player.getAddress().getHostName());
            stm.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(RegistrationControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isLoggedIn(Player player) {
        boolean logged = false;
        if (loggedUsers.containsKey(player.getName())) {
            logged = true;
        }
        return logged;
    }

    public boolean isRegistered(Player player) {
         try {
            PreparedStatement stm = connDB.prepareStatement(plugin.getQuery("sql-verify-registration"));
            stm.setString(1, player.getName());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(RegistrationControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void logOut(Player player) {
        try {
            loggedUsers.remove(player.getName());
        } catch (Exception ex) {
        }
    }

    public boolean logInByTime(Player player) {
        try {
            PreparedStatement stm = connDB.prepareStatement(plugin.getQuery("sql-login-time"));
            stm.setString(1, player.getName());
            stm.setString(2, player.getAddress().getHostName());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                long lastL = rs.getLong(1);
                if ((lastL + 600000) > System.currentTimeMillis()) { //if last login was less than 10 minutes ago
                    loggedUsers.put(player.getName(), player);
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RegistrationControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean logInByPass(Player player, String password) {
        try {
            PreparedStatement stm = connDB.prepareStatement(plugin.getQuery("sql-login-pass"));
            stm.setLong(1, System.currentTimeMillis());
            stm.setString(2, player.getAddress().getHostName());
            stm.setString(3, player.getName());
            stm.setString(4, md5(password));
            int r = stm.executeUpdate();
            if (r > 0) {
                loggedUsers.put(player.getName(), player);
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(RegistrationControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //Função para criar hash da senha informada
    public static String md5(String senha) {
        String sen = null;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));
            sen = hash.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sen;
    }
}
