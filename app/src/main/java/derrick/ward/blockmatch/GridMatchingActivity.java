package derrick.ward.blockmatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import derrick.ward.blockmatch.models.Block;
import derrick.ward.blockmatch.services.GameActions;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GridMatchingActivity extends AppCompatActivity implements GameActions {
    private final String LOGTAG = "BlockMatch";
    private int uncoveredBlock1Location = -1;
    private int uncoveredBlock2Location = -1;
    private int score = 0;
    private ArrayList<Block> gameBlocks = new ArrayList<>();
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_matching);

        GridView gameGrid = findViewById(R.id.blockGrid);

        // Get Game Mode
        Intent intent = getIntent();
        MainActivity.GameMode gameMode = (MainActivity.GameMode) intent.getSerializableExtra(String.valueOf(R.string.gameMode));

        // Launch Game
        GameBlocksEngine gameBlocksEngine = new GameBlocksEngine(this, gameMode, this, this);
        gameGrid.setGravity(Gravity.CENTER);
        gameGrid.setAdapter(gameBlocksEngine);
    }

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
    }
}