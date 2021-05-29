package hackOn2021.cheerymate.module;

public class PostComment {

    String comment;
    String uid;
    String commentId;
    String time;

    public PostComment()
    {

    }

    public PostComment(String uid, String comment, String time, String commentId)
    {
        this.comment = comment;
        this.uid = uid;
        this.time = time;
        this.commentId = commentId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

}
