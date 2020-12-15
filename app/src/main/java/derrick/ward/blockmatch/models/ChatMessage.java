package derrick.ward.blockmatch.models;

import com.google.firebase.database.ServerValue;

import derrick.ward.blockmatch.services.FirebaseUtility;

public class ChatMessage {
    public String userId;
    public String message;
    public String messageId;
    public Object timestamp;

    public ChatMessage() {
        this.timestamp= ServerValue.TIMESTAMP;
        this.messageId = FirebaseUtility.getUniqueName();
    }
}
