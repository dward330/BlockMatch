package derrick.ward.blockmatch.screens.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.screens.GameModeChooser;
import derrick.ward.blockmatch.screens.LandingScreen;
import derrick.ward.blockmatch.screens.LoginSignUp;

/**
 * Loads Login Screen
 * - Handles Login Operations
 */
public class Login extends Fragment {
    private Context context;
    private EditText email;
    private EditText password;

    public Login (Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loginFragment = inflater.inflate(R.layout.login, container, false);

        // Register UI Elements
        this.email = loginFragment.findViewById(R.id.loginEmail);
        this.password = loginFragment.findViewById(R.id.loginPassword);
        Button resetPassword = loginFragment.findViewById(R.id.loginResetPassword);
        resetPassword.setOnClickListener(resetPasswordOnClickListener());
        Button login = loginFragment.findViewById(R.id.login);
        login.setOnClickListener(loginOnClickListener());
        Button resendEmailVerification = loginFragment.findViewById(R.id.resendEmailVerification);
        resendEmailVerification.setOnClickListener(resendEmailVerificationOnClickListener());
        Button signUp = loginFragment.findViewById(R.id.loginSignUp);
        signUp.setOnClickListener(navigateToRegistrationOnClickListener());

        return loginFragment;
    }

    /**
     * Generates an On Click Listener that open the LoginSignUp Activity with some intent info
     * @return OnClickListener
     */
    private View.OnClickListener navigateToRegistrationOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoginSignUp.class);
                intent.putExtra(LoginSignUp.ScreenToLoad, LoginSignUp.UserRegistrationScreenName);
                startActivity(intent);
            }
        };
    }

    /**
     * Generates an On Click Listener for when Reset Password Action is invoked
     * @return OnClickListener
     */
    private View.OnClickListener resetPasswordOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword(view);
            }
        };
    }

    /*
     * Resets the password of the email entered
     */
    public void resetPassword(View view) {
        String emailEntered = this.email.getText().toString();

        if (emailEntered.trim().equals("")) {
            Toast.makeText(context, "Please enter email you want password reset for!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(emailEntered)
                .addOnSuccessListener((Activity)context, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "An email to reset password has been sent!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener((Activity)context, e -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Generates an On Click Listener for when Login Action is invoked
     * @return OnClickListener
     */
    private View.OnClickListener loginOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        };
    }

    /*
     * Tries to login the user
     */
    public void login(View view) {
        String emailEntered = this.email.getText().toString();
        String passwordEntered = this.password.getText().toString();

        if (emailEntered.trim().equals("")) {
            Toast.makeText(context, "Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordEntered.trim().equals("")) {
            Toast.makeText(context, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEntered, passwordEntered)
                .addOnSuccessListener((Activity) context, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser signedInUser = authResult.getUser();

                        if (signedInUser.isEmailVerified()) {
                            Toast.makeText(context, "Login Successful.", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent((Activity)context, LandingScreen.class));
                            ((Activity) context).finish();
                        } else {
                            Toast.makeText(context, "Please verify your email first. Check your email for a sent copy of the process.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener((Activity)context, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Generates an On Click Listener for when Resend Email Verification Action is invoked
     * @return OnClickListener
     */
    private View.OnClickListener resendEmailVerificationOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendEmailVerification(view);
            }
        };
    }

    /*
     * Resend email verification for the email entered
     */
    private void resendEmailVerification(View view) {
        String emailEntered = this.email.getText().toString();

        if (emailEntered.trim().equals("")) {
            Toast.makeText(context, "Please enter email you want the password reset for!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(context, "Please login first to re-send verification email.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseUser.sendEmailVerification()
                .addOnSuccessListener((Activity)context, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "An email with verification steps has been sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener((Activity)context, e -> {
                    Toast.makeText(context, "Email Verification failed to be sent: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
