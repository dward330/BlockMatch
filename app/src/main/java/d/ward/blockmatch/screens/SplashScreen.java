package d.ward.blockmatch.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import d.ward.blockmatch.R;

/**
 * Launches and runs Splash Screen
 */
public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth firebaseAuth; // Firebase Authentication Service
    private FirebaseUser signedInFirebaseUser; // Sign-In Firebase User

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // FirebaseApp.initializeApp(this); // Initialize Firebase for Entire Application
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.signedInFirebaseUser = this.firebaseAuth.getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();

        CountDownTimer countDownUntilNextScreen = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (signedInFirebaseUser == null) {
                    Toast.makeText(SplashScreen.this, "No signed in user detected", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashScreen.this, LoginSignUp.class));
                    finish();
                } else {
                    if (signedInFirebaseUser.isEmailVerified()) {
                        Toast.makeText(SplashScreen.this, "Signed in User Detected", Toast.LENGTH_SHORT).show();
                        startActivity( new Intent(SplashScreen.this, LandingScreen.class));
                        finish();
                    } else {
                        Toast.makeText(SplashScreen.this, "Please verify your email before trying to login. Check your email for process.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashScreen.this, LoginSignUp.class));
                        finish();
                    }
                }
            }
        };

        countDownUntilNextScreen.start();
    }
}
