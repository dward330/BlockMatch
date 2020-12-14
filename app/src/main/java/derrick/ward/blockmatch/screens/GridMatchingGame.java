package derrick.ward.blockmatch.screens;

import androidx.appcompat.app.AppCompatActivity;
import derrick.ward.blockmatch.screens.EndOfGame;
import derrick.ward.blockmatch.screens.GameModeChooser;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.models.Block;
import derrick.ward.blockmatch.services.GameActions;
import derrick.ward.blockmatch.services.adapters.GameBlocksEngine;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.GridView;

import java.util.ArrayList;

public class GridMatchingGame extends AppCompatActivity implements GameActions {
    private final String LOGTAG = "BlockMatch";
    private int uncoveredBlock1Location = -1;
    private int uncoveredBlock2Location = -1;
    private int score = 0;
    private ArrayList<Block> gameBlocks = new ArrayList<>();
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_matching_game);

        GridView gameGrid = findViewById(R.id.blockGrid);

        // Get Game Mode
        Intent intent = getIntent();
        GameModeChooser.GameMode gameMode = (GameModeChooser.GameMode) intent.getSerializableExtra(String.valueOf(R.string.gameMode));

        // Launch Game
        GameBlocksEngine gameBlocksEngine = new GameBlocksEngine(this, gameMode, this, this);
        gameGrid.setGravity(Gravity.CENTER);
        gameGrid.setAdapter(gameBlocksEngine);
    }

    /**
     * Ends Game
     * @param score latest score the gamer has
     */
    public void GameFinished(int score) {
        this.score = score;
        this.proceedToEndOfGameScreen();
    }

    /*
    * Starts a new Game
    * */
    private void proceedToEndOfGameScreen() {
        // Build Intent for End Of Game Screen
        Intent intentForEndOfGame = new Intent(this, EndOfGame.class);
        intentForEndOfGame.putExtra(String.valueOf(R.string.score), score);

        // Start End of Game Activity
        startActivity(intentForEndOfGame);
        finish();
    }
}