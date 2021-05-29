package hackOn2021.cheerymate.module;

public class Post {

    String uid;
    String time;
    String postImageUri;
    String postText;
    String postId;

    public Post()
    {

    }

    public Post(String uid, String time, String postText)
    {
        this.uid = uid;
        this.time = time;
        this.postText = postText;
    }

    public Post(String uid, String time, String postText, String imageUri, String postId)
    {
        this.uid = uid;
        this.time = time;
        this.postText = postText;
        this.postImageUri = imageUri;
        this.postId = postId;
    }

    public String getPostText() {
        return postText;
    }

    public String getTime() {
        return time;
    }

    public String getUid() {
        return uid;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImageUri() {
        return postImageUri;
    }

    public void setPostImageUri(String postImageUri) {
        this.postImageUri = postImageUri;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}