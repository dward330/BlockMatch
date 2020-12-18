package derrick.ward.blockmatch.services.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import derrick.ward.blockmatch.R;
import derrick.ward.blockmatch.models.Settings;
import derrick.ward.blockmatch.screens.fragments.GameModeChooser;
import derrick.ward.blockmatch.models.Block;
import derrick.ward.blockmatch.services.GameActions;
import derrick.ward.blockmatch.services.SettingsDBHelper;

public class GameBlocksEngine extends BaseAdapter {
    // private final String LOGTAG = "GameBlocksEngine";
    private GameModeChooser.GameMode gameMode;
    private GridView gameGrid;
    private int uncoveredBlock1Location = -1;
    private int uncoveredBlock2Location = -1;
    private HashMap<Integer, Block> gameBlocks = new HashMap<Integer, Block>();
    private Context context;
    private CountDownTimer timer;
    private int score = 0;
    private int matchesFound = 0;
    private int totalBlocks = 0;
    private Activity activity;
    private GameActions gameActions;

    public GameBlocksEngine(Context context, GameModeChooser.GameMode gameMode, Activity activity, GameActions gameActions) {
        this.context = context;
        this.gameGrid = ((Activity)context).findViewById(R.id.blockGrid);
        this.gameMode = gameMode;
        this.activity = activity;
        this.gameActions = gameActions;
        this.loadGameMode(gameMode, this.gameGrid);
    }

    @Override
    public int getCount() {
        return this.gameBlocks.size();
    }

    @Override
    public Object getItem(int i) {

        Block gameBlock = null;

        gameBlock = this.gameBlocks.get(new Integer(i));

        return gameBlock;
    }

    @Override
    public long getItemId(int i) {
        Block gameBlock = null;

        gameBlock = this.gameBlocks.get(new Integer(i));

        return (gameBlock != null) ? gameBlock.getBlockLocation() : 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Block gameBlock = null;

        if (this.gameBlocks != null) {
            gameBlock = this.gameBlocks.get(new Integer(i));
        }

        return (gameBlock != null) ? gameBlock.getBlock() : null;
    }

    /*
     * Loads Game Mode
     * */
    private void loadGameMode(GameModeChooser.GameMode gameMode, GridView gameGrid) {
        switch (gameMode) {
            case EASY:
                this.generateBlocks(gameGrid, 4);
                break;
            case MEDIUM:
                this.generateBlocks(gameGrid, 16);
                break;
            case DIFFICULT:
                this.generateBlocks(gameGrid, 36);
                break;
        }

        this.generateHiddenBlockNumbers(this.gameBlocks);
    }

    /*
     * Generates Blocks and adds them to the Grid
     * */
    private void generateBlocks(GridView gameGrid, int numOfBlocks) {

        // Remove Existing Blocks
        if (gameGrid.getChildCount() != 0 ) {
            gameGrid.removeAllViewsInLayout();
            this.gameBlocks.clear();
        }

        // Specify new alignment for block grid
        int dimensionNum = (int) Math.sqrt(numOfBlocks);
        gameGrid.setNumColumns(dimensionNum);

        // Retrieve Game Settings
        Settings gameSettings = new SettingsDBHelper(context).getSettings();

        for(int index = 0; index < numOfBlocks; index++) {
            this.totalBlocks++;
            int size = 180;

            // Generate Text Block
            TextView block = new TextView(this.context);
            block.setId(index);
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(size,size);
            textLayoutParams.gravity = Gravity.CENTER;
            block.setLayoutParams(textLayoutParams);
            block.setBackgroundResource(R.drawable.grid_box_border);

            // Generate Image Block
            ImageView blockImage = new ImageView(this.context);
            blockImage.setId(index);
            Drawable blockCoverDrawable = null;

            // Retrieve the correct block image cover
            switch (gameSettings.blockCoverImage) {
                case 0:
                    blockCoverDrawable = this.context.getResources().getDrawable(R.drawable.gift_box);
                break;
                case 1:
                    blockCoverDrawable = this.context.getResources().getDrawable(R.drawable.santa_claus);
                break;
                case 2:
                    blockCoverDrawable = this.context.getResources().getDrawable(R.drawable.reindeer);
                break;
                case 3:
                    blockCoverDrawable = this.context.getResources().getDrawable(R.drawable.elf);
                break;
            }

            blockImage.setImageDrawable(blockCoverDrawable);
            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(size,size);
            blockImage.setLayoutParams(imageLayoutParams);
            blockImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (uncoveredBlock1Location != -1 && uncoveredBlock2Location != -1) {
                        return;
                    }

                    // Log.d(LOGTAG, "block "+view.getId()+"clicked");

                    // Find Game Block to Unhide
                    Block gameBlock = gameBlocks.get(new Integer(view.getId()));

                    if (gameBlock.isHidden()) {
                        // Update Score
                        score++;

                        if (uncoveredBlock1Location == -1) {
                            uncoveredBlock1Location = gameBlock.getBlockLocation();
                            // Here we need to notify of data change to the adapter so it can swap the view  and show the number
                            gameBlock.uncoverBlock();

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            });

                        } else if (uncoveredBlock2Location == -1){
                            uncoveredBlock2Location = gameBlock.getBlockLocation();
                            // Here we need to notify the data change to the adapter so it can swap the view and show the number
                            gameBlock.uncoverBlock();

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            });

                            // Wait a little bit, so user can see the blocks uncovered
                            timer = new CountDownTimer(200, 200) {
                                @Override
                                public void onTick(long l) {
                                    // Just Wait -> Leave the current blocks select still showing
                                }

                                @Override
                                public void onFinish() {
                                    // Check if Game is over (Announce Score and start new game)
                                    updateAndCheckGameProgress(uncoveredBlock1Location, uncoveredBlock2Location, gameBlocks);
                                    timer.cancel();
                                }
                            };

                            timer.start();
                        }
                    }
                }
            });

            // Generate Game Block
            Block gameBlock = new Block(block, blockImage, index);

            // Add Block to Collection of Game Blocks
            this.gameBlocks.put(new Integer(index), gameBlock);

            // Log.d(LOGTAG, "Added Block Number "+(index+1)+" to the Game Grid");
        }
    }

    /*
     * Generate Hidden Block Numbers
     * */
    private void generateHiddenBlockNumbers(HashMap<Integer, Block> gameBlocks) {
        int numOfBlocks = gameBlocks.size();
        int numOfSetsOf2 = numOfBlocks / 2;
        ArrayList<Integer> usedBlockLocations = new ArrayList<>();

        for(int index = 1; index <= numOfSetsOf2; index++) {
            // Find Hiding Location for first number
            int hidingLocation1 = this.getRandomNumber(0, gameBlocks.size() -1, usedBlockLocations);

            // Set Hidden Value in Block
            Block blockAtLocation1 = gameBlocks.get(new Integer(hidingLocation1));
            blockAtLocation1.setHiddenNumber(index);

            usedBlockLocations.add(hidingLocation1);

            // Find Hiding Location for second number
            int hidingLocation2 = this.getRandomNumber(0, gameBlocks.size() -1, usedBlockLocations);

            // Set Hidden Value in Block
            Block blockAtLocation2 = gameBlocks.get(new Integer(hidingLocation2));
            blockAtLocation2.setHiddenNumber(index);

            usedBlockLocations.add(hidingLocation2);
        }
    }

    /**
     * Generates and Returns a Random number between min and max, and not in numbers to exclude
     */
    private int getRandomNumber(int min, int max, List<Integer> numbersToExclude) {
        boolean randomUniqueNumberFound = false;
        int randomNumber = -1;

        do {
            int number = (int) (Math.random() * (max - min + 1) + min);

            if (!numbersToExclude.contains(number)) {
                randomNumber = number;
                randomUniqueNumberFound = true;
            }

        } while(!randomUniqueNumberFound);

        return randomNumber;
    }

    /*
     * Check if blocks are a match, and if the game is over (launches finished game experience)
     * */
    private void updateAndCheckGameProgress(int uncoveredBlock1Location, int uncoveredBlock2Location, HashMap<Integer, Block> gameBlocks) {
        if (uncoveredBlock1Location != -1 && uncoveredBlock2Location != -1) {
            Block gameBlock1 =  gameBlocks.get(new Integer(uncoveredBlock1Location));
            Block gameBlock2 = gameBlocks.get(new Integer(uncoveredBlock2Location));

            // Reset Uncovered Locations
            // this.uncoveredBlock1Location = -1;
            // this.uncoveredBlock2Location = -1;

            if (gameBlock1.getHiddenNumber() != gameBlock2.getHiddenNumber()) {

                // Reset Uncovered Locations
                this.uncoveredBlock1Location = -1;
                this.uncoveredBlock2Location = -1;

                // Reset Uncovered Block
                gameBlock1.hideBlock();
                gameBlock2.hideBlock();

                // Notify the adapter that the Data changed (hide blocks again)
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            } else {
                this.matchesFound++;

                // If Game is Finished

                if (this.areAllGameBlocksUncovered()) {
                    // Proceed to Congrats Screen
                    this.gameActions.gameFinished(score);
                } else {
                    // Reset Uncovered Locations
                    this.uncoveredBlock1Location = -1;
                    this.uncoveredBlock2Location = -1;
                }

            }
        }
    }

    /*
     * Indicated if all Game Blocks are uncovered
     * */
    private boolean areAllGameBlocksUncovered() {
        boolean allUncovered = true;

        allUncovered = this.matchesFound == (this.totalBlocks/2);

        return allUncovered;
    }

}
