package hackOn2021.cheerymate.module;

public class UserData {

    String uid;
    String position;
    String fullName;
    String imageUrlSmall;
    String imageUrl;

    public UserData(String uid, String position, String fullName, String imageUrlSmall, String imageUrl)
    {
        this.uid = uid;
        this.position = position;
        this.fullName = fullName;
        this.imageUrlSmall = imageUrlSmall;
        this.imageUrl = imageUrl;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImageUrlSmall() {
        return imageUrlSmall;
    }

    public void setImageUrlSmall(String imageUrlSmall) {
        this.imageUrlSmall = imageUrlSmall;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
