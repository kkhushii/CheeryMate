package hackOn2021.cheerymate.module;

public class MessageChat {

    public String userName;
    public String message;
    public String uid;
    public String messageId;
    public String time;

    public MessageChat()
    {

    }

    public MessageChat(String name, String messageTyped, String userId, String time, String messageId)
    {
        this.userName = name;
        this.message = messageTyped;
        this.uid = userId;
        this.time = time;
        this.messageId = messageId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimeStamp() {
        return time;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "MessageChat{" +
                "userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", uid='" + uid + '\'' +
                ", key='" + messageId + '\'' +
                '}';
    }
}