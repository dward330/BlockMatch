package derrick.ward.blockmatch.screens.fragments;

import androidx.fragment.app.Fragment;

import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.screens.GridMatchingGame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GameModeChooser extends Fragment {
    private final String LOGTAG = "BlockMatch";
    private Context context;
    private GameMode gameMode = GameMode.EASY;

    public static enum GameMode {
        EASY,
        MEDIUM,
        DIFFICULT
    }

    public GameModeChooser(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View gameModeChooser = inflater.inflate(R.layout.game_mode_chooser, container, false);

        Button easyButton = gameModeChooser.findViewById(R.id.easyModeButton);
        easyButton.setOnClickListener(this::setGameMode);

        Button mediumButton = gameModeChooser.findViewById(R.id.mediumModeButton);
        mediumButton.setOnClickListener(this::setGameMode);

        Button difficultButton = gameModeChooser.findViewById(R.id.difficultModeButton);
        difficultButton.setOnClickListener(this::setGameMode);

        return  gameModeChooser;
    }

    /*
    * Event Handler for Game Mode is Chosen
    * */
    public void setGameMode(View view) {
        int gameModeId = view.getId();

        switch (gameModeId) {
            case R.id.easyModeButton:
                this.gameMode = GameMode.EASY;
                Log.d(LOGTAG, "Easy Game Mode Selected.");
                break;
            case R.id.mediumModeButton:
                this.gameMode = GameMode.MEDIUM;
                Log.d(LOGTAG, "Medium Game Mode Selected.");
                break;
            case R.id.difficultModeButton:
                this.gameMode = GameMode.DIFFICULT;
                Log.d(LOGTAG, "Difficult Game Mode Selected.");
                break;
            default:
                Log.e(LOGTAG, "Unknown Game Mode Detected: "+gameModeId);
        }

        // Build Intent to Load Game
        Intent intentToLoadGame = new Intent(context, GridMatchingGame.class);
        intentToLoadGame.putExtra(String.valueOf(R.string.gameMode), this.gameMode);

        // Start Game Activity
        startActivity(intentToLoadGame);
    }
}