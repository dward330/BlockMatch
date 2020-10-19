package derrick.ward.blockmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class EndOfGame extends AppCompatActivity {
    private final String LOGTAG = "BlockMatch";
    private MainActivity.GameMode gameMode = MainActivity.GameMode.EASY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_of_game);

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
        Intent intentToNewGame = new Intent(this, MainActivity.class);

        // Start New Game Activity
        startActivity(intentToNewGame);
    }
}