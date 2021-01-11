package d.ward.blockmatch.screens.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import d.ward.blockmatch.R;
import d.ward.blockmatch.services.GameActions;
import d.ward.blockmatch.services.adapters.LeadershipSectionAdapter;

public class LeadershipSection extends Fragment {
    private GameModeChooser.GameMode gameMode;
    private Context context;
    private GameActions gameActions;

    public LeadershipSection (Context context, GameModeChooser.GameMode gameMode, GameActions gameActions) {
        this.context = context;
        this.gameMode = gameMode;
        this.gameActions = gameActions;
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

        // Set Up Join Competition Button
        Button button = leadershipSection.findViewById(R.id.joinCompetition);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameActions.startGame();
            }
        });

        return leadershipSection;
    }
}
