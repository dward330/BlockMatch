package derrick.ward.blockmatch.services.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
import derrick.ward.blockmatch.models.LeadershipBoardEntry;
import derrick.ward.blockmatch.screens.ChatConversationDetails;
import derrick.ward.blockmatch.screens.GameModeChooser;

/**
 * Leadership Section Adapter
 */
public class LeadershipSectionAdapter extends RecyclerView.Adapter<LeadershipSectionAdapter.LeaderShipSectionItemViewHolder> implements PopupMenu.OnMenuItemClickListener {
    private Context context;
    private List<LeadershipBoardEntry> leadershipSectionEntries = new ArrayList<LeadershipBoardEntry>();
    private RecyclerView recyclerView;
    private GameModeChooser.GameMode gameMode;
    private LeadershipBoardEntry currentLeaderSelected;

    public LeadershipSectionAdapter(RecyclerView recyclerView, GameModeChooser.GameMode gameMode) {
        this.recyclerView = recyclerView;
        this.gameMode = gameMode;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference leadershipBoardSection = firebaseDatabase.getReference("LeadershipBoard/"+this.gameMode.name());
        this.listenForLeadershipBoardSectionChanges(leadershipBoardSection);
    }

    @NonNull
    @Override
    public LeaderShipSectionItemViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();

        //Create a new instance of this layout as a View
        View v = LayoutInflater.from(this.context).inflate(R.layout.leadership_section_item_details, parent, false);

        final LeaderShipSectionItemViewHolder viewHolder = new LeaderShipSectionItemViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderShipSectionItemViewHolder holder, int position) {
        LeadershipBoardEntry entry = this.leadershipSectionEntries.get(position);

        /* Get a reference to this exact user in the Users Database */
        if(holder.userDBRef !=null && holder.userValueEventListener !=null)
        {
            // Remove current item change event listener from the leadership board database table reference
            holder.userDBRef.removeEventListener(holder.userValueEventListener);
        }

        holder.userUID.setText(entry.id);
        holder.userScore.setText("Score: " + entry.score);

        /* Get a reference to the leadership board, for this game mode, from our leadership board database*/
        holder.userDBRef = FirebaseDatabase.getInstance().getReference("Users/"+entry.id);

        /* When User entry is first published to client, let display correct information */
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

        // Possible set card click to see user details

        // Set Options On Click
        holder.entryOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set Leader Clicked on
                currentLeaderSelected = new LeadershipBoardEntry();
                currentLeaderSelected.id = entry.id;

                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(LeadershipSectionAdapter.this);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.leadership_entry, popupMenu.getMenu());
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.leadershipSectionEntries == null) {
            return 0;
        }

        return this.leadershipSectionEntries.size();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.sendMessage:

                // Generate New Conversation
                FirebaseUser signedInUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference conversationsDBRef = firebaseDatabase.getReference("ConversationsGroups");
                DatabaseReference signInUserConversations = conversationsDBRef.child(signedInUser.getUid());
                signInUserConversations.runTransaction(createNewConversationTransactionHandler(currentLeaderSelected.id, signedInUser.getUid()));

                return true;
            default:
                return false;
        }
    }

    /**
     * Generates a Transaction Handler that creates a new Conversation with supplied user Id
     * @return Transaction.Handler
     */
    private Transaction.Handler createNewConversationTransactionHandler(String messageRecipient, String signedInUser) {
        return new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                HashMap<String, String> signInUserConvoRecipients = (HashMap<String, String>)currentData.getValue();

                if (signInUserConvoRecipients == null) {
                    signInUserConvoRecipients = new HashMap<String, String>();
                }

                signInUserConvoRecipients.put(messageRecipient, messageRecipient);

                currentData.setValue(signInUserConvoRecipients);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error == null) {

                    // Generate New Reversed Conversation
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference conversationsDBRef = firebaseDatabase.getReference("ConversationsGroups");
                    DatabaseReference recipientUserConversations = conversationsDBRef.child(messageRecipient);
                    recipientUserConversations.runTransaction(createNewReversedCopiedConversationTransactionHandler(signedInUser, messageRecipient));

                    /*
                    int uidComparison = signInUserId.toUpperCase().trim().compareTo(currentLeaderSelected.id.toUpperCase().trim());

                    // Generate New Conversation
                    FirebaseUser signedInUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference conversationsDBRef = firebaseDatabase.getReference("ConversationsChatMessages");

                    if (uidComparison > 0) {
                        DatabaseReference chatMessagesInConversation = conversationsDBRef.child(currentSelectLeaderId.trim()+"-"+signInUserId.trim());
                        chatMessagesInConversation.runTransaction(createNewConversationChatMessagesEntryTransactionHandler());
                    } else if (uidComparison < 0) {
                        DatabaseReference chatMessagesInConversation = conversationsDBRef.child(signInUserId.trim()+"-"+currentSelectLeaderId.trim());
                        chatMessagesInConversation.runTransaction(createNewConversationChatMessagesEntryTransactionHandler());
                    }
                    */
                }
            }
        };
    }

    /**
     * Generates a Transaction Handler that creates a reverse copied Conversation with supplied user Id
     * @return Transaction.Handler
     */
    private Transaction.Handler createNewReversedCopiedConversationTransactionHandler(String userId, String messageRecipient) {
        return new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                HashMap<String, String> signInUserConvoRecipients = (HashMap<String, String>)currentData.getValue();

                if (signInUserConvoRecipients == null) {
                    signInUserConvoRecipients = new HashMap<String, String>();
                }

                signInUserConvoRecipients.put(userId, userId);

                currentData.setValue(signInUserConvoRecipients);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error == null) {

                    Intent chatMessagesIntent = new Intent(context, ChatConversationDetails.class);
                    chatMessagesIntent.putExtra("messageRecipient", messageRecipient);
                    context.startActivity(chatMessagesIntent);
                }
            }
        };
    }


    /**
     * Generates a Transaction Handler that creates a new entry of chat messages for the Conversation between signed in user and selected leader board user
     * @return Transaction.Handler
     */
    private Transaction.Handler createNewConversationChatMessagesEntryTransactionHandler() {
        return new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        };
    }

    /**
     * Listen for Leadership Board Section Changes
     * @param databaseReference Database Table to Reference
     */
    private void listenForLeadershipBoardSectionChanges(DatabaseReference databaseReference) {
        databaseReference.addChildEventListener(getChildEventListenerForLeadershipBoardSection());
    }

    /* Listen for Leadership Board CRUD Operations and update View */
    private ChildEventListener getChildEventListenerForLeadershipBoardSection() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Get User's Id
                String userId = dataSnapshot.getKey();

                // Get Leadership Board Entry
                HashMap<String, Object> leadershipBoardEntry = (HashMap<String, Object>)dataSnapshot.getValue();

                if (leadershipBoardEntry != null) {

                    // Map to Strong Model for a Leadership Board Entry
                    LeadershipBoardEntry newEntry = new LeadershipBoardEntry();
                    newEntry.id = userId;
                    newEntry.score = (String)leadershipBoardEntry.get("Score");

                    // Add Entry Collection of entries
                    leadershipSectionEntries.add(newEntry);

                    Collections.sort(leadershipSectionEntries, new Comparator<LeadershipBoardEntry>() {
                        @Override
                        public int compare(LeadershipBoardEntry m1, LeadershipBoardEntry m2) {
                            return Integer.parseInt(m1.score) - Integer.parseInt(m2.score);
                        }
                    });

                    LeadershipSectionAdapter.this.notifyDataSetChanged(); // Trigger adapter to reprocess all entries in leadership board
                    LeadershipSectionAdapter.this.recyclerView.scrollToPosition(leadershipSectionEntries.size()-1); // Tell adapter to scroll down to the last entry
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
    public static class LeaderShipSectionItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView userProfilePhoto;
        public ImageView entryOptions;
        public TextView userUID;
        public TextView userDisplayName;
        public TextView userScore;

        public DatabaseReference userDBRef; // Holds a reference to a specific user in the users database table
        public ValueEventListener userValueEventListener; // Holds a reference to listener to invoke when this entry is changed in the users database table


        public LeaderShipSectionItemViewHolder(View v){
            super(v);

            // Bind Layout UI Elements to properties in View Holder Instance
            this.userUID = v.findViewById(R.id.userUID);
            this.userDisplayName = v.findViewById(R.id.userDisplayName);
            this.userScore = v.findViewById(R.id.userScore);
            this.userProfilePhoto = v.findViewById(R.id.userProfilePhoto);
            this.entryOptions = v.findViewById(R.id.leadershipBoardEntryOptions);
        }
    }
}
