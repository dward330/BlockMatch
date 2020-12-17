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
import derrick.ward.blockmatch.screens.fragments.Conversations;
import derrick.ward.blockmatch.screens.fragments.GameModeChooser;
import derrick.ward.blockmatch.screens.fragments.LeadershipBoard;
import derrick.ward.blockmatch.screens.fragments.Settings;
import derrick.ward.blockmatch.services.GameActions;

public class LandingScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GameActions {
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

        Intent intent = getIntent();
        if (intent != null) {
            String screenToLoad = intent.getStringExtra("fragmentToLoad");

            if (screenToLoad != null && !screenToLoad.isEmpty()) {
                this.loadFragment(screenToLoad);
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new LeadershipBoard(this));
                fragmentTransaction.commit();
            }

        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new LeadershipBoard(this));
            fragmentTransaction.commit();
        }
    }

    /**
     * Loads Fragment based on screen name supplied
     * @param screenName Name of the screen to load (as a Fragment)
     */
    public void loadFragment(String screenName) {
        String screenNameCapitalized = screenName.toUpperCase();

        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        switch (screenNameCapitalized) {
            case "ABOUTAUTHOR":
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new AboutGameAuthor());
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case "GAMEMODECHOOSER":
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new GameModeChooser(this));
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case "LEADERSHIPBOARD":
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new LeadershipBoard(this));
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case "CONVERSATIONS":
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Conversations(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case "SETTINGS":
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Settings(this));
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
        }
    }

    /**
     * Event Handler for when a menu item is chosen on the app drawer
     * @param menuItem
     * @return
     */
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
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new GameModeChooser(this));
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.leadershipBoardItem:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new LeadershipBoard(this));
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.messagesItem:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Conversations(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                fragmentTransaction.commit();
                this.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.settingsItem:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Settings(this));
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

    @Override
    public void gameFinished(int score) {

    }

    @Override
    public void startGame() {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new GameModeChooser(this));
        fragmentTransaction.commit();
        this.drawerLayout.closeDrawer(GravityCompat.START);
    }
}
