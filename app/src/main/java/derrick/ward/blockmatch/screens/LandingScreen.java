package derrick.ward.blockmatch.screens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.screens.fragments.LeadershipBoard;
import derrick.ward.blockmatch.services.LeadershipBoardPager;

public class LandingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_screen);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new LeadershipBoard());
        fragmentTransaction.commit();
    }
}
