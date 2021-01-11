package d.ward.blockmatch.services.adapters;

import android.content.Context;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import d.ward.blockmatch.screens.fragments.GameModeChooser;
import d.ward.blockmatch.screens.fragments.LeadershipSection;
import d.ward.blockmatch.services.GameActions;

public class LeadershipBoardPager extends FragmentStatePagerAdapter {
    private Context context;
    private GameActions gameActions;
    private ArrayList<String> leadershipBoardSections = new ArrayList<>();

    public LeadershipBoardPager(Context context, FragmentManager fragmentManager, GameActions gameActions) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.context = context;
        this.gameActions = gameActions;

        this.leadershipBoardSections.add("EASY MODE");
        this.leadershipBoardSections.add("MEDIUM MODE");
        this.leadershipBoardSections.add("DIFFICULT MODE");
    }

    @Override
    public Fragment getItem(@NonNull int i) {
        Fragment leadershipSection = null;

        switch (i) {
            case 0:
                leadershipSection = new LeadershipSection(context, GameModeChooser.GameMode.EASY, gameActions);
                break;
            case 1:
                leadershipSection = new LeadershipSection(context, GameModeChooser.GameMode.MEDIUM, gameActions);
                break;
            case 2:
                leadershipSection = new LeadershipSection(context, GameModeChooser.GameMode.DIFFICULT, gameActions);
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
