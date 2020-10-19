package derrick.ward.blockmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private final String LOGTAG = "BlockMatch";
    private GameMode gameMode = GameMode.EASY;

    public static enum GameMode {
        EASY,
        MEDIUM,
        DIFFICULT
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Intent intentToLoadGame = new Intent(this, GridMatchingActivity.class);
        intentToLoadGame.putExtra(String.valueOf(R.string.gameMode), this.gameMode);

        // Start Game Activity
        startActivity(intentToLoadGame);
    }
}