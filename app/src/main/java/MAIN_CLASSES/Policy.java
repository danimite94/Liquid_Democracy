package MAIN_CLASSES;

public class Policy {

    Integer admission_time;
    Integer discussion_time;
    Integer verification_time;
    Integer voting_time;

    Integer quorum_admission;
    Integer quorum_verification;

    String name;

    String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String Description;


    public Integer getQuorum_admission() {
        return quorum_admission;
    }

    public void setQuorum_admission(Integer quorum_admission) {
        this.quorum_admission = quorum_admission;
    }

    public Integer getQuorum_verification() {
        return quorum_verification;
    }

    public void setQuorum_verification(Integer quorum_verification) {
        this.quorum_verification = quorum_verification;
    }

    public Policy(){

    }
    public Integer getAdmission_time() {
        return admission_time;
    }

    public void setAdmission_time(Integer admission_time) {
        this.admission_time = admission_time;
    }

    public Integer getDiscussion_time() {
        return discussion_time;
    }

    public void setDiscussion_time(Integer discussion_time) {
        this.discussion_time = discussion_time;
    }

    public Integer getVerification_time() {
        return verification_time;
    }

    public void setVerification_time(Integer verification_time) {
        this.verification_time = verification_time;
    }

    public Integer getVoting_time() {
        return voting_time;
    }

    public void setVoting_time(Integer voting_time) {
        this.voting_time = voting_time;
    }

}
