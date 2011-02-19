/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.anjosecurity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author gabrielcouto
 */
public class Settings extends Configuration {

    public Settings(File f) {
        super(f);
        if(f.exists()){
            load();
        } else {
            writeFile();
        }
    }

    public String getMsgWelcomeGuest() {
        String key = "msg.welcome.guest";
        if(this.getString(key)==null){
            this.setProperty(key, "You Are Not Registered On The Server. Please Register using /register command.");
        }
        return this.getString(key);
    }

    public String getMsgWelcomeUser() {
        String key = "msg.welcome.user";
        if(this.getString(key)==null){
            this.setProperty(key, "Hello friend, please /login to your account keep playing.");
        }
        return this.getString(key);
    }

    public String getMsgLoginTime() {
        String key = "msg.login.time";
        if(this.getString(key)==null){
            this.setProperty(key, "Welcome Back... Hey, I remember you. You are logged in automatically then.");
        }
        return this.getString(key);
    }

    public String getMsgLoginPass() {
        String key = "msg.login.pass";
        if(this.getString(key)==null){
            this.setProperty(key, "Password correct! You are now logged in!");
        }
        return this.getString(key);
    }

    public String getMsgLoginIcorrect() {
        String key = "msg.login.incorrect";
        if(this.getString(key)==null){
            this.setProperty(key, "Password incorrect! Can't Log In!");
        }
        return this.getString(key);
    }

    public String getMsgRegisterSucessful() {
        String key = "msg.register.successful";
        if(this.getString(key)==null){
            this.setProperty(key, "You registered successfully.");
        }
        return this.getString(key);
    }

    public String getMsgRegisterFailed() {
        String key = "msg.register.failed";
        if(this.getString(key)==null){
            this.setProperty(key, "Registration failed. Report it to the administrator.");
        }
        return this.getString(key);
    }

    public String getMsgRegistrationUnallowed() {
        String key = "msg.alert.unallowedregistration";
        if(this.getString(key)==null){
            this.setProperty(key, "Sorry, you are not allowed to register.");
        }
        return this.getString(key);
    }

    public String getMsgUnregisterSucessful() {
        String key = "msg.unregister.successful";
        if(this.getString(key)==null){
            this.setProperty(key, "You cleared your registration info. You can register once again.");
        }
        return this.getString(key);
    }

    public String getMsgUnregisterFailed() {
        String key = "msg.unregister.failed";
        if(this.getString(key)==null){
            this.setProperty(key, "An error occured while trying to clear your info. (Wrong pass?)");
        }
        return this.getString(key);
    }

    public String getMsgDeleteSucessful() {
        String key = "msg.delete.successful";
        if(this.getString(key)==null){
            this.setProperty(key, "You cleared that user registration info.");
        }
        return this.getString(key);
    }

    public String getMsgDeleteFailed() {
        String key = "msg.delete.failed";
        if(this.getString(key)==null){
            this.setProperty(key, "An error occured while trying to clear his info.");
        }
        return this.getString(key);
    }
    public String getMsgAllowedSuccessful() {
        String key = "msg.allow.successful";
        if(this.getString(key)==null){
            this.setProperty(key, "The destination player was allowed");
        }
        return this.getString(key);
    }
    public String getMsgAllowedFailed() {
        String key = "msg.allow.failed";
        if(this.getString(key)==null){
            this.setProperty(key, "User not found.");
        }
        return this.getString(key);
    }
    public String getMsgAlertAllowed() {
        String key = "msg.alert.allowed";
        if(this.getString(key)==null){
            this.setProperty(key, "You have been allowed to register");
        }
        return this.getString(key);
    }
    public String getMsgNotAdmin() {
        String key = "msg.alert.notadmin";
        if(this.getString(key)==null){
            this.setProperty(key, "You are not allowed to use this command.");
        }
        return this.getString(key);
    }

    public String getMsgUnallowedNeedLogin() {
        String key = "msg.unallowed.needlogin";
        if(this.getString(key)==null){
            this.setProperty(key, "Use /login <password> to unlock this action.");
        }
        return this.getString(key);
    }

    public String getMsgUnallowedNeedRegister() {
        String key = "msg.unallowed.needregister";
        if(this.getString(key)==null){
            this.setProperty(key, "Use /register <password> to unlock this action.");
        }
        return this.getString(key);
    }

    public String getMsgActionResetting() {
        String key = "msg.action.resetting";
        if(this.getString(key)==null){
            this.setProperty(key, "You are not registered. Your char has been resetted.");
        }
        return this.getString(key);
    }

    public String getMsgRegistrationActivated() {
        String key = "msg.registrations.activated";
        if(this.getString(key)==null){
            this.setProperty(key, "Registrations are now activated.");
        }
        return this.getString(key);
    }

    public String getMsgRegistrationDeactivated() {
        String key = "msg.registrations.deactivated";
        if(this.getString(key)==null){
            this.setProperty(key, "Registrations are now deactiveted.");
        }
        return this.getString(key);
    }

    public List<String> getOptMainAdmins() {
        String key = "opt.main.admins";
        ArrayList<String> names = new ArrayList<String>();
        names.add("AnjoCaido");
        names.add("gmcouto");
        if(this.getProperty(key)==null){
            this.setProperty(key, names);
        }
        return this.getStringList(key, names);
    }

    public boolean isOptGuestsResetAtLogin() {
        String key = "opt.guests.resetatlogin";
        if(this.getProperty(key)==null){
            this.setProperty(key, true);
        }
        return this.getBoolean(key, true);
    }

    public boolean isOptGuestsLockdown() {
        String key = "opt.guests.lockdown";
        if(this.getProperty(key)==null){
            this.setProperty(key, false);
        }
        return this.getBoolean(key, false);
    }

    public boolean isOptGuestsSummonCommands() {
        String key = "opt.guests.summoncommands";
        if(this.getProperty(key)==null){
            this.setProperty(key, false);
        }
        return this.getBoolean(key, false);
    }

    public int getOptSessionMinutes() {
        String key = "opt.session.minutes";
        if(this.getProperty(key)==null){
            this.setProperty(key, 30);
        }
        return this.getInt(key, 30);
    }

    public boolean isRegistrationEnabled() {
        String key = "opt.registration.enabled";
        if(this.getProperty(key)==null){
            this.setProperty(key, true);
        }
        return this.getBoolean(key, true);
    }
    public void setRegistrationEnabled(boolean setting){
        String key = "opt.registration.enabled";
        this.setProperty(key, setting);
    }

    public void writeFile(){
        getMsgActionResetting();
        getMsgDeleteFailed();
        getMsgDeleteSucessful();
        getMsgLoginIcorrect();
        getMsgLoginPass();
        getMsgLoginTime();
        getMsgRegisterFailed();
        getMsgRegisterSucessful();
        getMsgRegistrationActivated();
        getMsgRegistrationDeactivated();
        getMsgUnallowedNeedLogin();
        getMsgUnallowedNeedRegister();
        getMsgUnregisterFailed();
        getMsgUnregisterSucessful();
        getMsgWelcomeGuest();
        getMsgWelcomeUser();
        getOptMainAdmins();
        getOptSessionMinutes();
        isOptGuestsLockdown();
        isOptGuestsResetAtLogin();
        isOptGuestsSummonCommands();
        isRegistrationEnabled();
        getMsgNotAdmin();
        getMsgAllowedSuccessful();
        getMsgAllowedFailed();
        getMsgAlertAllowed();
        getMsgRegistrationUnallowed();
        this.save();
    }
}
