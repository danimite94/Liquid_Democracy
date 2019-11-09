package MAIN_CLASSES;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class User {

    String email;
    String address;
    Integer coins;
    String device_token;

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    String password;
    String groups;
    String metadata;
    HashMap<String, Boolean> Supporting;
    HashMap<String, Boolean> Opposing;
    private boolean isSelected = false;
    public HashMap<String, Boolean> getSupporting() {
        return Supporting;
    }

    public void setSupporting(HashMap<String, Boolean> supporting) {
        Supporting = supporting;
    }

    public HashMap<String, Boolean> getOpposing() {
        return Opposing;
    }

    public void setOpposing(HashMap<String, Boolean> opposing) {
        Opposing = opposing;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

}
