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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
        if(holder.leadershipBoardDbRef !=null && holder.leadershipBoardInfoChangeListener !=null)
        {
            // Remove current item change event listener from the leadership board database table reference
            holder.leadershipBoardDbRef.removeEventListener(holder.leadershipBoardInfoChangeListener);
        }

        holder.userDisplayName.setText(entry.displayName);
        holder.userScore.setText(entry.score);

        // Download User Profile Image Image
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(entry.profilePhotoLocation);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(holder.userProfilePhoto); // Load image into supplied ImageView Element
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Error Downloading User Profile Photo! "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        /* Get a reference to the leadership board, for this game mode, from our leadership board database*/

        /* When leadership board entry is first published to client, let display correct information */

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

        public DatabaseReference leadershipBoardDbRef; // Holds a reference to a specific entry in leadership board database table
        public ValueEventListener leadershipBoardInfoChangeListener; // Holds a reference to listener to invoke when this entry is changed in leadership board database table


        public LeaderShipSectionItemViewHolder(View v){
            super(v);

            // Bind Layout UI Elements to properties in View Holder Instance
            this.userDisplayName = v.findViewById(R.id.userDisplayName);
            this.userScore = v.findViewById(R.id.userScore);
            this.userProfilePhoto = v.findViewById(R.id.userProfilePhoto);
            this.entryOptions = v.findViewById(R.id.leadershipBoardEntryOptions);
        }
    }
}
