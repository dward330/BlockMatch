package derrick.ward.blockmatch.services.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.models.ChatMessage;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ChatMessageItemViewHolder> {
    private Context context;
    private List<ChatMessage> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private String conversationChatMessagesPrimaryKey;

    public ChatConversationAdapter(RecyclerView recyclerView, String conversationChatMessagesPrimaryKey) {
        this.recyclerView = recyclerView;
        this.conversationChatMessagesPrimaryKey = conversationChatMessagesPrimaryKey;

        DatabaseReference conversationChatMessagesDBRef = FirebaseDatabase.getInstance().getReference("ConversationChatMessages");
        DatabaseReference currentConversationDBRef = conversationChatMessagesDBRef.child(this.conversationChatMessagesPrimaryKey);
        this.listenForConversationChatMessageChanges(currentConversationDBRef);
    }

    @NonNull
    @Override
    public ChatConversationAdapter.ChatMessageItemViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        //Create a new instance of this layout as a View
        View v = LayoutInflater.from(this.context).inflate(R.layout.conversation_chat_message_item, parent, false);

        final ChatConversationAdapter.ChatMessageItemViewHolder viewHolder = new ChatConversationAdapter.ChatMessageItemViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatConversationAdapter.ChatMessageItemViewHolder holder, int position) {
        ChatMessage entry = this.messages.get(position);

        // If this there is already a leadership board database table reference && If there is a leadership board item change event listener
        if(holder.userDBRef !=null && holder.userValueEventListener !=null)
        {
            // Remove current item change event listener from the leadership board database table reference
            holder.userDBRef.removeEventListener(holder.userValueEventListener);
        }

        holder.userMessage.setText(entry.message);

        /* Get a reference to this exact user in the Users Database */
        holder.userDBRef = FirebaseDatabase.getInstance().getReference("Users/"+entry.userId);

        /* When User entry is updated, lets display correct information */
        holder.userValueEventListener = holder.userDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> latestUserProfileInfo = (HashMap<String, Object>) snapshot.getValue();

                if (latestUserProfileInfo != null) {
                    holder.userDisplayName.setText((String)latestUserProfileInfo.get("displayName"));

                    String profilePhotoLocation = (String)latestUserProfileInfo.get("profilePhoto");

                    // Download User Profile Image Image
                    if (profilePhotoLocation != null) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReferenceFromUrl(profilePhotoLocation);
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(holder.userProfilePhoto); // Load image into supplied ImageView Element
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(context, "Error Downloading User Profile Photo! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.messages == null) {
            return 0;
        }

        return this.messages.size();
    }

    /**
     * Listen for Conversation Chat message Changes
     * @param databaseReference Database Table/Child to Reference
     */
    private void listenForConversationChatMessageChanges(DatabaseReference databaseReference) {
        databaseReference.addChildEventListener(getChildEventListenerForConversationChatMessageChanges());
    }

    /* Listen for Conversation Chat Message CRUD Operations and update View */
    private ChildEventListener getChildEventListenerForConversationChatMessageChanges() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                HashMap<String, Object> currentChatMessage = (HashMap<String, Object>)dataSnapshot.getValue();


                if (currentChatMessage != null) {

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.messageId = (String)currentChatMessage.get("messageId");
                    chatMessage.message = (String)currentChatMessage.get("message");
                    chatMessage.timestamp = currentChatMessage.get("timestamp");
                    String messageUserId = (String)currentChatMessage.get("userId");
                    chatMessage.userId = messageUserId.substring(0, messageUserId.indexOf('-'));

                    // Add Entry to collection
                    messages.add(chatMessage);

                    Collections.sort(messages, new Comparator<ChatMessage>() {
                        @Override
                        public int compare(ChatMessage m1, ChatMessage m2) {
                            return (int)(((long)m1.timestamp) - ((long)m2.timestamp));
                        }
                    });


                    ChatConversationAdapter.this.notifyDataSetChanged(); // Trigger adapter to reprocess all conversation entries
                    ChatConversationAdapter.this.recyclerView.scrollToPosition(messages.size()-1); // Tell adapter to scroll down to the last conversation

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
    }

    // Class used to communicate with UI Elements used for each instance of an entry
    public static class ChatMessageItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView userProfilePhoto;
        public TextView userDisplayName;
        public TextView userMessage;

        public DatabaseReference userDBRef; // Holds a reference to a specific user in the users database table
        public ValueEventListener userValueEventListener; // Holds a reference to listener to invoke when this entry is changed in the users database table


        public ChatMessageItemViewHolder(View v){
            super(v);

            // Bind Layout UI Elements to properties in View Holder Instance
            this.userProfilePhoto = v.findViewById(R.id.chatMessageProfilePhoto);
            this.userDisplayName = v.findViewById(R.id.chatMessagesDisplayName);
            this.userMessage = v.findViewById(R.id.chatMessage);
        }
    }
}
