package derrick.ward.blockmatch.models;

import com.google.firebase.database.ServerValue;

public class ChatMessage {
    public String userId;
    public String message;
    public Object timestamp;

    public ChatMessage() {
        this.timestamp= ServerValue.TIMESTAMP;
    }
}
