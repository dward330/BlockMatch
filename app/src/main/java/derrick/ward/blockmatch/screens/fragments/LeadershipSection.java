package derrick.ward.blockmatch.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.screens.GameModeChooser;
import derrick.ward.blockmatch.services.adapters.LeadershipSectionAdapter;

public class LeadershipSection extends Fragment {
    private GameModeChooser.GameMode gameMode;

    public LeadershipSection (GameModeChooser.GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View leadershipSection = inflater.inflate(R.layout.leadership_section, container, false);

        RecyclerView recyclerView = leadershipSection.findViewById(R.id.leadershipSection_Recylcer_View);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(leadershipSection.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Create Adapter
        LeadershipSectionAdapter leadershipSectionAdapter = new LeadershipSectionAdapter(recyclerView, this.gameMode);

        // Set Adapter
        recyclerView.setAdapter(leadershipSectionAdapter);

        return leadershipSection;
    }
}
