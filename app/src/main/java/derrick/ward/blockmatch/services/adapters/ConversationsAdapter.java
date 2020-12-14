package derrick.ward.blockmatch.services.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import derrick.ward.blockmatch.models.Conversation;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConversationItemViewHolder> implements PopupMenu.OnMenuItemClickListener {
    private Context context;
    private List<Conversation> conversations = new ArrayList<>();
    private Conversation currentConversationSelected;
    private RecyclerView recyclerView;
    private String signedInUser;

    public ConversationsAdapter(RecyclerView recyclerView, String signedInUser) {
        this.recyclerView = recyclerView;
        this.signedInUser = signedInUser;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference signedInUsersConversations = firebaseDatabase.getReference("ConversationsGroups/"+signedInUser);
        this.listenForConversationGroupChanges(signedInUsersConversations);
    }

    @NonNull
    @Override
    public ConversationsAdapter.ConversationItemViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        //Create a new instance of this layout as a View
        View v = LayoutInflater.from(this.context).inflate(R.layout.chat_message_group_item, parent, false);

        final ConversationsAdapter.ConversationItemViewHolder viewHolder = new ConversationsAdapter.ConversationItemViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationsAdapter.ConversationItemViewHolder holder, int position) {
        Conversation entry = this.conversations.get(position);

        // If this there is already a leadership board database table reference && If there is a leadership board item change event listener
        if(holder.userDBRef !=null && holder.userValueEventListener !=null)
        {
            // Remove current item change event listener from the leadership board database table reference
            holder.userDBRef.removeEventListener(holder.userValueEventListener);
        }

        holder.userUID.setText(entry.recipientId);

        /* Get a reference to this exact user in the Users Database */
        holder.userDBRef = FirebaseDatabase.getInstance().getReference("Users/"+entry.recipientId);

        /* When User entry is first published to client, let display correct information */
        holder.userValueEventListener = holder.userDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> latestUserProfileInfo = (HashMap<String, Object>) snapshot.getValue();

                if (latestUserProfileInfo != null) {
                    holder.userDisplayName.setText((String)latestUserProfileInfo.get("displayName"));
                    holder.userEmail.setText((String)latestUserProfileInfo.get("email"));
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

        // Set card click should open up conversation Chat Details

        // Set Options On Click
        holder.convoOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set Conversation Clicked on
                currentConversationSelected = new Conversation();
                currentConversationSelected.recipientId = entry.recipientId;

                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(ConversationsAdapter.this);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.message_menu, popupMenu.getMenu());
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.conversations == null) {
            return 0;
        }

        return this.conversations.size();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.deleteConversation:

                // Dialog Asking if user is sure they want to delete conversation

                // Delete Conversation

                return true;
            default:
                return false;
        }
    }

    /**
     * Listen for Conversation Group Changes
     * @param databaseReference Database Table to Reference
     */
    private void listenForConversationGroupChanges(DatabaseReference databaseReference) {
        databaseReference.addChildEventListener(getChildEventListenerForConversationGroupChanges());
    }

    /* Listen for Conversation Group CRUD Operations and update View */
    private ChildEventListener getChildEventListenerForConversationGroupChanges() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Get User's Id
                String userId = dataSnapshot.getKey();

                // Get Leadership Board Entry
                // HashMap<String, Object> usersConversations = (HashMap<String, Object>)dataSnapshot.getValue();

                if (dataSnapshot != null) {
                    /*
                    // Map to Strong Model for a conversation
                    Conversation conversation = new Conversation();
                    conversation.recipientId = userId;

                    // Add Entry Collection of entries
                    conversations.add(conversation);

                    Collections.sort(conversations, new Comparator<Conversation>() {
                        @Override
                        public int compare(Conversation m1, Conversation m2) {
                            return (int)(((float)m1.timestamp) - ((float)m2.timestamp));
                        }
                    });

                    ConversationsAdapter.this.notifyDataSetChanged(); // Trigger adapter to reprocess all conversation entries
                    ConversationsAdapter.this.recyclerView.scrollToPosition(conversations.size()-1); // Tell adapter to scroll down to the last conversation
                    */
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                /*
                Movie movieChanged = getMovieDataFromHashMap((HashMap<String, Object>)dataSnapshot.getValue());

                boolean positionFound = false;
                int position = 0;
                for (Movie movie : movies) {
                    if (movie.name.toUpperCase().equals(movieChanged.name.toUpperCase())) {
                        positionFound = true;
                        break;
                    }
                    position++;
                }

                if (positionFound == true) {
                    movies.set(position, movieChanged);
                    MoviesRecyclerAdapter.this.notifyItemChanged(position);

                    MoviesRecyclerAdapter.this.recyclerView.scrollToPosition(position);

                }*/
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
    public static class ConversationItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView userProfilePhoto;
        public ImageView convoOptions;
        public TextView userUID;
        public TextView userDisplayName;
        public TextView userEmail;

        public DatabaseReference userDBRef; // Holds a reference to a specific user in the users database table
        public ValueEventListener userValueEventListener; // Holds a reference to listener to invoke when this entry is changed in the users database table


        public ConversationItemViewHolder(View v){
            super(v);

            // Bind Layout UI Elements to properties in View Holder Instance
            this.userUID = v.findViewById(R.id.convoUserUID);
            this.userDisplayName = v.findViewById(R.id.convoDisplayName);
            this.userEmail = v.findViewById(R.id.convoEmail);
            this.userProfilePhoto = v.findViewById(R.id.convoUserProfilePhoto);
            this.convoOptions = v.findViewById(R.id.convoOptions);
        }
    }
}
