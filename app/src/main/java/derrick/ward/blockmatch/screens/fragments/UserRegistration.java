package derrick.ward.blockmatch.screens.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.models.User;
import derrick.ward.blockmatch.screens.LoginSignUp;
import derrick.ward.blockmatch.services.FirebaseUtility;

/**
 * Loads User Registration Screen
 * - Handles Registration Operations
 */
public class UserRegistration extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private static final int REQUEST_FOR_CAMERA=0011;
    private static final int REQUEST_FOR_LOCATION = 123;
    public static final int RESULT_OK = -1;
    private Context context;
    private ImageView profilePhoto;
    private Uri profilePhotoUri;
    private EditText emailAddress;
    private EditText password;
    private EditText displayName;

    public UserRegistration (Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loginFragment = inflater.inflate(R.layout.user_registration, container, false);

        // Register UI Elements
        profilePhoto = loginFragment.findViewById(R.id.signUpProfilePhoto);
        profilePhoto.setOnClickListener(registerLaunchProfilePhotoMenuHandler());
        emailAddress = loginFragment.findViewById(R.id.signUpEmail);
        password = loginFragment.findViewById(R.id.signUpPassword);
        displayName = loginFragment.findViewById(R.id.signUpDisplayName);
        Button signUpButton = loginFragment.findViewById(R.id.signUp);
        signUpButton.setOnClickListener(registerCreateUserEventHandler());

        return loginFragment;
    }

    /**
     * Event Handler for registering a new User
     * @return On Click Event Handler
     */
    private View.OnClickListener registerCreateUserEventHandler() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFormValid()) {
                    User user = new User();
                    user.email = emailAddress.getText().toString().trim();
                    user.password = password.getText().toString().trim();
                    user.displayName = displayName.getText().toString().trim();

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
                                .addOnSuccessListener((Activity) context, newUserRegistrationSuccessListener(user))
                                .addOnFailureListener((Activity) context, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed to create new user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                }
            }
        };
    }

    /**
     * On Success Event Handler for a new registered authentication user/account
     * @param user new user to register
     * @return OnSuccessListner
     */
    private OnSuccessListener newUserRegistrationSuccessListener(User user) {
        return new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser firebaseUser = authResult.getUser();
                firebaseUser.sendEmailVerification().addOnSuccessListener((Activity) context, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Move this to after the user is saved in the database
                        Toast.makeText(context, "Sign up Successful! Please verify your email before trying to login. Check your email for the process.", Toast.LENGTH_SHORT).show();

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        String photoFileName = FirebaseUtility.getUniqueName()+".jpg";
                        String userPhotoFileLocation = FirebaseUtility.userPhotosFolderName +"/"+ photoFileName;

                        // Save User's Profile Photo
                        final StorageReference userPhotoFirebaseReference = storage.getReference(userPhotoFileLocation);
                        userPhotoFirebaseReference.putFile(profilePhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Update profile photo info in User Model
                                user.profilePhoto = FirebaseUtility.userPhotosFolderPath +"/"+photoFileName;

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference userDatabaseTable = database.getReference("Users");

                                // Save new User information
                                userDatabaseTable.child(firebaseUser.getUid()).runTransaction(createNewUserTransactionHandler(user));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to create new user because profile photo image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener((Activity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to Send Email Verification for your new Account. Please Click Resend Email Verification. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    /**
     * Event Handler for launch profile photo menu handler
     * @return On Click Listener
     */
    private View.OnClickListener registerLaunchProfilePhotoMenuHandler() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.setOnMenuItemClickListener(UserRegistration.this);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.profile_photo, popupMenu.getMenu());
                popupMenu.show();
            }
        };
    }

    /**
     * Generates a Transaction Handler for creating a new user
     * @param newUser New User to create
     * @return Transaction Handler
     */
    private Transaction.Handler createNewUserTransactionHandler(User newUser) {
        return new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                currentData.setValue(newUser);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                profilePhotoUri = null;

                // Return to Login Screen
                Intent intent = new Intent(context, LoginSignUp.class);
                intent.putExtra(LoginSignUp.ScreenToLoad, LoginSignUp.LoginScreenName);
                startActivity(intent);
            }
        };
    }

    /**
     * Indicates if registration Form is valid
     * @return true if registration form is valid
     */
    private boolean isFormValid() {

        if (this.profilePhotoUri == null) {
            Toast.makeText(this.context, "Please Set a Profile Photo", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (emailAddress.getText().toString().trim().equals("")) {
            Toast.makeText(this.context, "Please Enter an Email Address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.getText().toString().trim().equals("")) {
            Toast.makeText(this.context, "Please Enter a Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (displayName.getText().toString().trim().equals("")) {
            Toast.makeText(this.context, "Please Enter a Display Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.selectPhotoFromGallary:
                selectPhoto();
                return true;
            case R.id.takePhoto:
                takePhoto();
                return true;
            default:
                return false;
        }
    }

    /* Launches Photo Gallery so user make select photo */
    private void selectPhoto(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooser=Intent.createChooser(pickPhoto,"Select a Photo Gallery App.");
        if (pickPhoto.resolveActivity(this.context.getPackageManager()) != null) {
            startActivityForResult(pickPhoto, 1);}
    }

    /* Launches Camera App */
    private void takePhoto(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Movie Post Photo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        this.profilePhotoUri = this.context.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.profilePhotoUri);
        Intent chooser=Intent.createChooser(intent,"Select a Camera App.");
        if (intent.resolveActivity(this.context.getPackageManager()) != null) {
            startActivityForResult(chooser, REQUEST_FOR_CAMERA);}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_FOR_LOCATION && ((grantResults.length>0 && grantResults[0]!= PackageManager.PERMISSION_GRANTED) || (grantResults.length>1 && grantResults[1]!=PackageManager.PERMISSION_GRANTED))){
            Toast.makeText(context, "We need to access your location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_CAMERA && resultCode == RESULT_OK) {
            if(this.profilePhotoUri==null)
            {
                Toast.makeText(this.context, "Error taking photo.", Toast.LENGTH_SHORT).show();
                return;
            }

            this.profilePhoto.setImageURI(profilePhotoUri);

            return;
        }

        // User Choice a Photo from the gallery
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            this.profilePhotoUri = data.getData();

            this.profilePhoto.setImageURI(profilePhotoUri);
        }
    }
}
