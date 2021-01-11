package derrick.ward.blockmatch.screens;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.screens.fragments.Login;
import derrick.ward.blockmatch.screens.fragments.UserRegistration;

public class LoginSignUp extends AppCompatActivity {
    public static final String ScreenToLoad = "screenToLoad";
    public static final String LoginScreenName = "Login";
    public static final String UserRegistrationScreenName = "UserRegistration";
    private enum ActiveFragment {
        Login,
        UserRegistration
    }
    private ActiveFragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signin_fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Intent intent = getIntent();
        String screenToLoad = intent.getStringExtra(LoginSignUp.ScreenToLoad);

        // Load Login or Registration View
        if (screenToLoad == null || screenToLoad.toUpperCase().equals(LoginSignUp.LoginScreenName.toUpperCase())) {
            this.activeFragment = ActiveFragment.Login;

            Login loginFragment = new Login(this);
            fragmentTransaction.replace(R.id.loginRegistrationContainer, loginFragment);
            fragmentTransaction.commit();
        } else if (screenToLoad.toUpperCase().equals(LoginSignUp.UserRegistrationScreenName.toUpperCase())) {
            this.activeFragment = ActiveFragment.UserRegistration;

            UserRegistration userRegistrationFragment = new UserRegistration(this);
            fragmentTransaction.replace(R.id.loginRegistrationContainer, userRegistrationFragment);
            fragmentTransaction.commit();
        }
    }

    /**
     * Event Handler for when Back Button is clicked
     */
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        switch (this.activeFragment) {
            case UserRegistration:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                Login loginFragment = new Login(this);
                fragmentTransaction.replace(R.id.loginRegistrationContainer, loginFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    // Needed if we are going to request permissions in a fragment (Which we will in User Registration Fragment)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
