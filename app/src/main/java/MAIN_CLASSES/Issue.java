package MAIN_CLASSES;

import java.util.HashMap;

public class Issue {

    String change_status_date;

    public String getChange_status_date() {
        return change_status_date;
    }

    public void setChange_status_date(String change_status_date) {
        this.change_status_date = change_status_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;
    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    String currentDate;

    String followerID;

    public String getModerator() {
        return moderator;
    }

    public void setModerator(String moderator) {
        this.moderator = moderator;
    }

    String delegatorID;

    String moderator;
    public HashMap<String, Boolean> getInitiatives() {
        return Initiatives;
    }

    public void setInitiatives(HashMap<String, Boolean> initiatives) {
        Initiatives = initiatives;
    }

    HashMap<String,Boolean> Initiatives;

    private int type; //type of decision

    public void setPolicies(String policies) {
        this.policies = policies;
    }

    public String getPolicies() {
        return policies;
    }

    String policies;


    public Issue() {
    }


    public void setGroups(String groups) {
        Groups = groups;
    }

    public String getGroups() {
        return Groups;
    }

    String Groups;


}
