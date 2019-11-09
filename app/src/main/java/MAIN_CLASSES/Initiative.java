package MAIN_CLASSES;

import java.util.HashMap;

public class Initiative {

    String currentDate;
    String description;
    String owner;

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    String rank; //shall present 0,1,2,... -1,-2 according to abstinence, first place in supported final voting ... or first place in opposed final voting
    HashMap<String,Boolean> supporters,opposers,abstinence;


    public HashMap<String, Boolean> getSupporters() {
        return supporters;
    }

    public void setSupporters(HashMap<String, Boolean> supporters) {
        this.supporters = supporters;
    }

    public HashMap<String, Boolean> getOpposers() {
        return opposers;
    }

    public void setOpposers(HashMap<String, Boolean> opposers) {
        this.opposers = opposers;
    }

    public HashMap<String, Boolean> getAbstinence() {
        return abstinence;
    }

    public void setAbstinence(HashMap<String, Boolean> abstinence) {
        this.abstinence = abstinence;
    }

    Integer ID;

    public HashMap<String, Boolean> getSuggestions() {
        return Suggestions;
    }

    public void setSuggestions(HashMap<String, Boolean> suggestions) {
        Suggestions = suggestions;
    }

    HashMap<String,Boolean> Suggestions;

    public Initiative() {

    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCurrentDate() {

        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }
}
