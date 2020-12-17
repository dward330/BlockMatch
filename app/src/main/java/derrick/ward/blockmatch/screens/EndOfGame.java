package derrick.ward.blockmatch.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.models.Settings;
import derrick.ward.blockmatch.screens.fragments.GameModeChooser;
import derrick.ward.blockmatch.services.SettingsDBHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;

public class EndOfGame extends AppCompatActivity {
    private final String LOGTAG = "BlockMatch";
    private GameModeChooser.GameMode gameMode = GameModeChooser.GameMode.EASY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_of_game);

        Intent intent = getIntent();
        int score = intent.getIntExtra(String.valueOf(R.string.score), 0);
        GameModeChooser.GameMode gameMode = (GameModeChooser.GameMode) intent.getSerializableExtra(String.valueOf(R.string.gameMode));
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get latest Game Settings
        Settings gameSettings = new SettingsDBHelper(this).getSettings();

        if (gameSettings.publishScore == 1) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            String pathToUserLeadershipBoardEntry = "LeadershipBoard/" + gameMode.name() + "/" + userId;
            DatabaseReference userLeadershipBoardEntry = firebaseDatabase.getReference(pathToUserLeadershipBoardEntry);
            userLeadershipBoardEntry.runTransaction(updateUserScoreOnLeaderShipBoard(userId, score, gameMode));
        }

        String message = "Congrats on finding all block matches!\n\nYour Score:"+" "+score;

        // Write Score and Congrats Message
        TextView textView = findViewById(R.id.scoreTextView);
        textView.setText(message);
    }

    /**
     * Generates a Transaction Handler for saving user's latest score to the leadership Board
     * @param userId Current User Id
     * @param score User's latest Score
     * @param gameMode Game Mode the user is playing in
     * @return Transaction.Handler
     */
    private Transaction.Handler updateUserScoreOnLeaderShipBoard(String userId, int score, GameModeChooser.GameMode gameMode) {
        return new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                HashMap<String, String> scoreEntry = new HashMap<>();
                scoreEntry.put("Score", String.valueOf(score));

                HashMap<String, Object> hashMapOfCurrentData = (HashMap<String, Object>)currentData.getValue();
                if (hashMapOfCurrentData != null && hashMapOfCurrentData.containsKey("Score")) {
                    int currentScore = Integer.parseInt((String)hashMapOfCurrentData.get("Score"));
                    if (currentScore < score) {
                        scoreEntry.put("Score", String.valueOf(currentScore));
                    }
                }

                currentData.setValue(scoreEntry);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        };
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

    public void seeLeadershipBoard(View view) {
        // Build Intent to See Leadership Board
        Intent intentToLeadershipBoard = new Intent(this, LandingScreen.class);

        // Start Leadership Board Activity
        startActivity(intentToLeadershipBoard);
        finish();
    }
}