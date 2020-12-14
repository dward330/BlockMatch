package derrick.ward.blockmatch.models;

import com.google.firebase.database.ServerValue;

public class Conversation {
    public String recipientId;
    public Object timestamp;

    public Conversation() {
        this.timestamp= ServerValue.TIMESTAMP;
    }
}
