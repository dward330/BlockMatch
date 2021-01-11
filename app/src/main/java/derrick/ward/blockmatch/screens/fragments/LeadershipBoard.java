package derrick.ward.blockmatch.screens.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.services.GameActions;
import derrick.ward.blockmatch.services.adapters.LeadershipBoardPager;

public class LeadershipBoard extends Fragment {
    private GameActions gameActions;

    public LeadershipBoard(GameActions gameActions) {
        this.gameActions = gameActions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View leadershipBoardFragment = inflater.inflate(R.layout.leadership_board, container, false);
        TabLayout gameModes = leadershipBoardFragment.findViewById(R.id.gameModes);

        ImageView menuIconImageView = leadershipBoardFragment.findViewById(R.id.menuIconImageView);
        int color = Color.parseColor("#fc72a1");
        menuIconImageView.setColorFilter(color);
        menuIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameActions.openMenu();
            }
        });

        LeadershipBoardPager leadershipBoardPager = new LeadershipBoardPager(leadershipBoardFragment.getContext(), getFragmentManager(), gameActions);
        ViewPager leadershipBoardSectionPager = leadershipBoardFragment.findViewById(R.id.leadershipBoard);
        leadershipBoardSectionPager.setAdapter(leadershipBoardPager);
        gameModes.setupWithViewPager(leadershipBoardSectionPager);

        return leadershipBoardFragment;
    }
}
