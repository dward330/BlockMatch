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
import derrick.ward.blockmatch.models.LeadershipBoardEntry;
import derrick.ward.blockmatch.screens.GameModeChooser;

/**
 * Leadership Section Adapter
 */
public class LeadershipSectionAdapter extends RecyclerView.Adapter<LeadershipSectionAdapter.LeaderShipSectionItemViewHolder> implements PopupMenu.OnMenuItemClickListener {
    private Context context;
    private List<LeadershipBoardEntry> leadershipSectionEntries = new ArrayList<LeadershipBoardEntry>();
    private RecyclerView recyclerView;
    private GameModeChooser.GameMode gameMode;

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

        // If this there is already a leadership board database table reference && If there is a leadership board item change event listener
        if(holder.userDBRef !=null && holder.userValueEventListener !=null)
        {
            // Remove current item change event listener from the leadership board database table reference
            holder.userDBRef.removeEventListener(holder.userValueEventListener);
        }

        holder.userScore.setText("Score: " + entry.score);

        /* Get a reference to the leadership board, for this game mode, from our leadership board database*/
        holder.userDBRef = FirebaseDatabase.getInstance().getReference("Users/"+entry.id);

        /* When leadership board entry is first published to client, let display correct information */
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

                // Run Logic to send a message

                return true;
            default:
                return false;
        }
    }

    // Class used to communicate with UI Elements used for each instance of an entry
    public static class LeaderShipSectionItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView userProfilePhoto;
        public ImageView entryOptions;
        public TextView userDisplayName;
        public TextView userScore;

        public DatabaseReference userDBRef; // Holds a reference to a specific user in the users database table
        public ValueEventListener userValueEventListener; // Holds a reference to listener to invoke when this entry is changed in the users database table


        public LeaderShipSectionItemViewHolder(View v){
            super(v);

            // Bind Layout UI Elements to properties in View Holder Instance
            this.userDisplayName = v.findViewById(R.id.userDisplayName);
            this.userScore = v.findViewById(R.id.userScore);
            this.userProfilePhoto = v.findViewById(R.id.userProfilePhoto);
            this.entryOptions = v.findViewById(R.id.leadershipBoardEntryOptions);
        }
    }

    private void listenForLeadershipBoardSectionChanges(DatabaseReference databaseReference) {
        databaseReference.addChildEventListener(getChildEventListenerForLeadershipBoardSection());
    }

    /* Listen for Movies CRUD Operations and updates View */
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
}
