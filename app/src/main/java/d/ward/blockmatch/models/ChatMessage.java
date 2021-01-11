package d.ward.blockmatch.models;

import com.google.firebase.database.ServerValue;

import d.ward.blockmatch.services.FirebaseUtility;

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
