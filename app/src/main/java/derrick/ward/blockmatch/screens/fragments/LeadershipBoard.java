package derrick.ward.blockmatch.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.services.GameActions;
import derrick.ward.blockmatch.services.LeadershipBoardPager;

public class LeadershipBoard extends Fragment {
    private GameActions gameActions;

    public LeadershipBoard(GameActions gameActions) {
        this.gameActions = gameActions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View leadershipBoardFragment = inflater.inflate(R.layout.leadership_board, container, false);
        TabLayout gameModes = leadershipBoardFragment.findViewById(R.id.gameModes);

        LeadershipBoardPager leadershipBoardPager = new LeadershipBoardPager(leadershipBoardFragment.getContext(), getFragmentManager(), gameActions);
        ViewPager leadershipBoardSectionPager = leadershipBoardFragment.findViewById(R.id.leadershipBoard);
        leadershipBoardSectionPager.setAdapter(leadershipBoardPager);
        gameModes.setupWithViewPager(leadershipBoardSectionPager);

        return leadershipBoardFragment;
    }
}
