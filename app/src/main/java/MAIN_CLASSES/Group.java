package MAIN_CLASSES;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Group {

    String name;
    String image;
    HashMap<String,Boolean> members;
    HashMap<String,Boolean> issues;

    public HashMap<String, Boolean> getIssues() {
        return issues;
    }

    public void setIssues(HashMap<String, Boolean> issues) {
        this.issues = issues;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String description;

    public Group() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public HashMap<String,Boolean> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String,Boolean> members) {
        this.members = members;
    }
}
