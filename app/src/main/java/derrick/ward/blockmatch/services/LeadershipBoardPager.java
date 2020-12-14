package derrick.ward.blockmatch.services;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import derrick.ward.blockmatch.screens.GameModeChooser;
import derrick.ward.blockmatch.screens.fragments.LeadershipSection;

public class LeadershipBoardPager extends FragmentStatePagerAdapter {
    private Context context;
    private ArrayList<String> leadershipBoardSections = new ArrayList<>();

    public LeadershipBoardPager(Context context, FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.context = context;

        this.leadershipBoardSections.add("EASY MODE");
        this.leadershipBoardSections.add("MEDIUM MODE");
        this.leadershipBoardSections.add("DIFFICULT MODE");
    }

    @Override
    public Fragment getItem(@NonNull int i) {
        Fragment leadershipSection = null;

        switch (i) {
            case 0:
                leadershipSection = new LeadershipSection(context, GameModeChooser.GameMode.EASY);
                break;
            case 1:
                leadershipSection = new LeadershipSection(context, GameModeChooser.GameMode.MEDIUM);
                break;
            case 2:
                leadershipSection = new LeadershipSection(context, GameModeChooser.GameMode.DIFFICULT);
                break;
        }

        return leadershipSection;
    }

    @Override
    public int getCount() {
        return this.leadershipBoardSections.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = this.leadershipBoardSections.get(position);

        return title;
    }
}
