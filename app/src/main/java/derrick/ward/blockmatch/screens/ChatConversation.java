package derrick.ward.blockmatch.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.models.ChatMessage;
import derrick.ward.blockmatch.services.FirebaseUtility;
import derrick.ward.blockmatch.services.adapters.ChatConversationAdapter;

public class ChatConversation extends AppCompatActivity {
    private TextView messageBox;
    private String messageRecipient;
    private String conversationChatMessagesPrimaryKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_chat_messages);

        messageBox = findViewById(R.id.messageBox);

        Intent receivedIntent = getIntent();
        if (receivedIntent == null) {
            Toast.makeText(this, "Error Receiving Intent", Toast.LENGTH_LONG).show();
        } else {
            messageRecipient = receivedIntent.getStringExtra("messageRecipient");
            if (messageRecipient == null) {
                Toast.makeText(this, "Error Receiving Message Recipient from Intent", Toast.LENGTH_LONG).show();
            } else {
                String signInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                int uidComparison = signInUserId.toUpperCase().trim().compareTo(this.messageRecipient.toUpperCase().trim());

                if (uidComparison > 0) {
                    this.conversationChatMessagesPrimaryKey = this.messageRecipient.trim()+"-"+signInUserId.trim();
                } else if (uidComparison < 0) {
                    this.conversationChatMessagesPrimaryKey = signInUserId.trim()+"-"+this.messageRecipient.trim();
                }
            }
        }


        // Register and setup Adapter
        RecyclerView recyclerView = findViewById(R.id.singleChatRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Create Adapter
        ChatConversationAdapter chatConversationAdapter = new ChatConversationAdapter(recyclerView, this.conversationChatMessagesPrimaryKey);

        // Set Adapter
        recyclerView.setAdapter(chatConversationAdapter);
    }

    /**
     * Submits chat message
     * @param view view that invoked this method
     */
    public void sendMessage(View view) {
        String message = this.messageBox.getText().toString().trim();

        if (!message.isEmpty()) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference conversationChatMessagesDBRef = FirebaseDatabase.getInstance().getReference("ConversationChatMessages");
            DatabaseReference currentConversationDBRef = conversationChatMessagesDBRef.child(this.conversationChatMessagesPrimaryKey);

            ChatMessage newChatMessage = new ChatMessage();
            newChatMessage.userId = firebaseUser.getUid();
            newChatMessage.message = message;

            currentConversationDBRef.runTransaction(updateMessageConversation(newChatMessage));
        }
    }

    /**
     * Generates a Transaction Handler to submit new message and update Message Conversation
     * @return
     */
    private Transaction.Handler updateMessageConversation(ChatMessage chatMessage) {
        return new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                HashMap<String, HashMap<String, Object>> currentChatMessages = (HashMap<String, HashMap<String, Object>>)currentData.getValue();

                if (currentChatMessages == null) {
                    currentChatMessages = new HashMap<String, HashMap<String, Object>>();
                }

                String messageUserId = chatMessage.userId + "-" + FirebaseUtility.getUniqueName();

                HashMap<String, Object> messageDetails = new HashMap<>();
                messageDetails.put("message", chatMessage.message);
                messageDetails.put("messageId", chatMessage.messageId);
                messageDetails.put("timestamp", chatMessage.timestamp);
                messageDetails.put("userId", messageUserId);

                currentChatMessages.put(messageUserId, messageDetails);

                currentData.setValue(currentChatMessages);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error == null) {
                    messageBox.setText("");
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        // Build Intent to Conversations Fragment
        Intent intentToConversationsFragment = new Intent(this, LandingScreen.class);
        intentToConversationsFragment.putExtra("fragmentToLoad", "conversations");

        // Go to Conversations
        startActivity(intentToConversationsFragment);
        finish();
    }
}
