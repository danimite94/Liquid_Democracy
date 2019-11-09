package MAIN_CLASSES;

import java.util.HashMap;

public class Suggestion {

    String text;
    String owner;
    HashMap<String,Boolean> supporters,opposers;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

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
}
