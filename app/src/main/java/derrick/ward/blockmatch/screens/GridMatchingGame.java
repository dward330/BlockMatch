package derrick.ward.blockmatch.screens;

import androidx.appcompat.app.AppCompatActivity;

import derrick.ward.blockmatch.models.Settings;
import derrick.ward.blockmatch.screens.fragments.GameModeChooser;
import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.models.Block;
import derrick.ward.blockmatch.services.GameActions;
import derrick.ward.blockmatch.services.SettingsDBHelper;
import derrick.ward.blockmatch.services.adapters.GameBlocksEngine;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridView;

import java.util.ArrayList;

public class GridMatchingGame extends AppCompatActivity implements GameActions {
    // private final String LOGTAG = "BlockMatch";
    private int uncoveredBlock1Location = -1;
    private int uncoveredBlock2Location = -1;
    private int score = 0;
    private ArrayList<Block> gameBlocks = new ArrayList<>();
    private GameModeChooser.GameMode gameMode;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_matching_game);

        GridView gameGrid = findViewById(R.id.blockGrid);

        // Get Game Mode
        Intent intent = getIntent();
        this.gameMode = (GameModeChooser.GameMode) intent.getSerializableExtra(String.valueOf(R.string.gameMode));

        // Launch Game
        GameBlocksEngine gameBlocksEngine = new GameBlocksEngine(this, this.gameMode, this, this);
        gameGrid.setGravity(Gravity.CENTER);
        gameGrid.setAdapter(gameBlocksEngine);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent currentIntent = getIntent();

        // Get latest Game Settings
        Settings gameSettings = new SettingsDBHelper(this).getSettings();

        // Does the game settings specify not to play music
        if (gameSettings.playMusic != 1) {
            return;
        }

        // Start Music
        if (currentIntent != null) {
            int musicPosition = currentIntent.getIntExtra("musicPosition", -1);

            if (musicPosition == -1) {
                this.startMusic();
            } else {
                this.startMusic(musicPosition);
            }
        } else {
            this.startMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (this.mediaPlayer != null) {
            this.mediaPlayer.pause();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {

        if (this.mediaPlayer != null) {
            int musicPosition = this.mediaPlayer.getCurrentPosition();

            Intent currentIntent = getIntent();
            currentIntent.putExtra("musicPosition", musicPosition);
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * Ends Game
     * @param score latest score the gamer has
     */
    public void gameFinished(int score) {
        this.score = score;
        this.proceedToEndOfGameScreen();
    }

    /**
     * Empty to adhere to interface
     */
    public void startGame() {

    }

    /**
     * Empty to adhere to interface
     */
    public void openMenu() {

    }

    /*
    * Starts a new Game
    * */
    private void proceedToEndOfGameScreen() {
        // Build Intent for End Of Game Screen
        Intent intentForEndOfGame = new Intent(this, EndOfGame.class);
        intentForEndOfGame.putExtra(String.valueOf(R.string.score), score);
        intentForEndOfGame.putExtra(String.valueOf(R.string.gameMode), this.gameMode);

        // Start End of Game Activity
        startActivity(intentForEndOfGame);
        finish();
    }

    /**
     * Starts the correct music based on the game mode
     */
    private void startMusic() {
        switch (this.gameMode) {
            case EASY:
                this.mediaPlayer = MediaPlayer.create(this, R.raw.oniku);
                break;
            case MEDIUM:
                this.mediaPlayer = MediaPlayer.create(this, R.raw.motivated);
                break;
            case DIFFICULT:
                this.mediaPlayer = MediaPlayer.create(this, R.raw.lounge1);
                break;
        }

        if (this.mediaPlayer != null) {
            this.mediaPlayer.setLooping(true);
            this.mediaPlayer.start();
        }
    }

    /**
     * Resumes the correct music based on the game mode
     */
    private void startMusic(int position) {
        switch (this.gameMode) {
            case EASY:
                this.mediaPlayer = MediaPlayer.create(this, R.raw.oniku);
                break;
            case MEDIUM:
                this.mediaPlayer = MediaPlayer.create(this, R.raw.motivated);
                break;
            case DIFFICULT:
                this.mediaPlayer = MediaPlayer.create(this, R.raw.lounge1);
                break;
        }

        if (this.mediaPlayer != null) {
            this.mediaPlayer.seekTo(position);
            this.mediaPlayer.setLooping(true);
            this.mediaPlayer.start();
        }
    }

    @Override
    public void onBackPressed() {
        // Build Intent to See Game Chooser Fragment
        Intent intentToGameChooserFragment = new Intent(this, LandingScreen.class);
        intentToGameChooserFragment.putExtra("fragmentToLoad", "GameModeChooser");

        // Go to Game Mode Chooser
        startActivity(intentToGameChooserFragment);
        finish();
    }
}