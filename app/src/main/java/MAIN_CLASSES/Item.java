package MAIN_CLASSES;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;

public class Item {

    private String description;
    private int admission;
    private int discussion;
    private int verification;
    private int type;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getAdmission() {
        return admission;
    }

    public void setAdmission(int admission) {
        this.admission = admission;
    }

    public int getDiscussion() {
        return discussion;
    }

    public void setDiscussion(int discussion) {
        this.discussion = discussion;
    }

    public int getVerification() {
        return verification;
    }

    public void setVerification(int verification) {
        this.verification = verification;
    }

    public int getVoting() {
        return voting;
    }

    public void setVoting(int voting) {
        this.voting = voting;
    }

    public int getFirstquorum() {
        return firstquorum;
    }

    public void setFirstquorum(int firstquorum) {
        this.firstquorum = firstquorum;
    }

    public int getSecquorum() {
        return secquorum;
    }

    public void setSecquorum(int secquorum) {
        this.secquorum = secquorum;
    }

    public ImageButton getAddGroup() {
        return addGroup;
    }

    public void setAddGroup(ImageButton addGroup) {
        this.addGroup = addGroup;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int voting;
    private int firstquorum;
    private int secquorum;
    private ImageButton addGroup;


    public Item() {

    }
}
