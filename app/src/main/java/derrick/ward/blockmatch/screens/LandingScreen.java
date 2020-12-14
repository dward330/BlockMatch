package derrick.ward.blockmatch.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.screens.fragments.AboutGameAuthor;
import derrick.ward.blockmatch.screens.fragments.ChatMessages;
import derrick.ward.blockmatch.screens.fragments.LeadershipBoard;

public class LandingScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CircleImageView profilePhoto;
    private TextView profileDisplayName;
    private TextView profileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);

        // Register UI Elements
        this.drawerLayout = findViewById(R.id.drawer);
        this.navigationView = findViewById(R.id.navigation_view);
        this.profilePhoto = this.navigationView.getHeaderView(0).findViewById(R.id.navViewSelfPortrait);
        this.profileDisplayName = this.navigationView.getHeaderView(0).findViewById(R.id.navViewDisplayName);
        this.profileEmail = this.navigationView.getHeaderView(0).findViewById(R.id.navViewEmail);
        this.navigationView.setNavigationItemSelectedListener(this);

        this.loadProfileInformation();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new LeadershipBoard());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        int menuId = menuItem.getItemId();

        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        switch (menuId) {
            case R.id.aboutAuthorItem:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new AboutGameAuthor());
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.playGameItem:
                startActivity( new Intent(this, GameModeChooser.class));
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.leadershipBoardItem:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new LeadershipBoard());
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.messagesItem:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new ChatMessages(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
        }

        return true;
    }

    /**
     * Downloads and Loads Profile Information
     */
    private void loadProfileInformation() {
        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseUser signedInUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userInfo = userDatabase.getReference("Users/"+signedInUser.getUid());

        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> profileInfo = (HashMap<String, Object>) snapshot.getValue();

                if (profileInfo != null) {
                    profileDisplayName.setText((String)profileInfo.get("displayName"));
                    profileEmail.setText((String)profileInfo.get("email"));

                    String profilePhotoLocation = (String)profileInfo.get("profilePhoto");

                    if (profilePhotoLocation != null) {
                        // Download User Profile Image Image
                        if (profilePhotoLocation != null) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageReference = storage.getReferenceFromUrl(profilePhotoLocation);
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri).into(profilePhoto); // Load image into supplied ImageView Element
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(LandingScreen.this, "Error Downloading User Profile Photo! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
