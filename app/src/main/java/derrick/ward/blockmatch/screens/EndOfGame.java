package derrick.ward.blockmatch.screens;

import androidx.appcompat.app.AppCompatActivity;
import derrick.ward.blockmatch.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndOfGame extends AppCompatActivity {
    private final String LOGTAG = "BlockMatch";
    private GameModeChooser.GameMode gameMode = GameModeChooser.GameMode.EASY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_of_game);

        Intent intent = getIntent();
        int score = intent.getIntExtra(String.valueOf(R.string.score), 0);
        String message = "Congrats on completing the puzzle!\n\nYour Score was :"+" "+score;

        // Write Score and Congrats Message
        TextView textView = findViewById(R.id.scoreTextView);
        textView.setText(message);
    }

    /*
     * Starts a new Game
     * */
    public void startNewGame(View view) {
        // Build Intent to Load Game
        Intent intentToNewGame = new Intent(this, GameModeChooser.class);

        // Start New Game Activity
        startActivity(intentToNewGame);
        finish();
    }
}