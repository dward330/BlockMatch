package derrick.ward.blockmatch.services;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import derrick.ward.blockmatch.screens.GameModeChooser;
import derrick.ward.blockmatch.screens.fragments.LeadershipSection;

public class LeadershipBoardPager extends FragmentStatePagerAdapter {
    private ArrayList<String> leadershipBoardSections = new ArrayList<>();

    public LeadershipBoardPager(FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.leadershipBoardSections.add("EASY MODE");
        this.leadershipBoardSections.add("MEDIUM MODE");
        this.leadershipBoardSections.add("DIFFICULT MODE");
    }

    @Override
    public Fragment getItem(@NonNull int i) {
        Fragment leadershipSection = null;

        switch (i) {
            case 1:
                leadershipSection = new LeadershipSection(GameModeChooser.GameMode.EASY);
                break;
            case 2:
                leadershipSection = new LeadershipSection(GameModeChooser.GameMode.MEDIUM);
                break;
            case 3:
                leadershipSection = new LeadershipSection(GameModeChooser.GameMode.DIFFICULT);
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
