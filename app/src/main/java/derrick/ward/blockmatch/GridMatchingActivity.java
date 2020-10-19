package derrick.ward.blockmatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GridMatchingActivity extends AppCompatActivity {
    private final String LOGTAG = "BlockMatch";
    private int uncoveredBlock1Location = -1;
    private int uncoveredBlock2Location = -1;
    private int score = 0;
    private ArrayList<Block> gameBlocks = new ArrayList<Block>();
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_matching);

        GridLayout gameGrid = findViewById(R.id.blockGrid);

        // Get Game Mode
        Intent intent = getIntent();
        MainActivity.GameMode gameMode = (MainActivity.GameMode) intent.getSerializableExtra(String.valueOf(R.string.gameMode));

        // Launch Game Mode
        this.loadGameMode(gameMode, gameGrid);
    }

    /*
    * Loads Game Mode
    * */
    private void loadGameMode(MainActivity.GameMode gameMode, GridLayout gameGrid) {
        switch (gameMode) {
            case EASY:
                this.generateBlocks(gameGrid, 16);
                break;
            case MEDIUM:
                this.generateBlocks(gameGrid, 25);
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
    private void generateBlocks(GridLayout gameGrid, int numOfBlocks) {

        // Remove Existing Blocks
        if (gameGrid.getChildCount() != 0 ) {
            gameGrid.removeAllViewsInLayout();
            this.gameBlocks.clear();
        }

        // Specify new alignment for block grid
        int dimensionNum = (int) Math.sqrt(numOfBlocks);
        gameGrid.setRowCount(dimensionNum);
        gameGrid.setColumnCount(dimensionNum);

        for(int index = 0; index < numOfBlocks; index++) {
            TextView block = new TextView(this);
            int size = 180;
            block.setId(index);
            block.setWidth(size);
            block.setHeight(size);
            block.setBackgroundResource(R.drawable.grid_box_border);
            block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isCoveredGameBlock = ((TextView) view).getText().toString().isEmpty();

                    if (isCoveredGameBlock) {
                        // Update Score
                        score++;

                        // Find Game Block to Unhide
                        Block gameBlock = getBlockByView(view, gameBlocks);

                        // Uncover Game Block
                        gameBlock.getGameBlock().setText(String.valueOf(gameBlock.getHiddenNumber()));

                        if (uncoveredBlock1Location == -1) {
                            uncoveredBlock1Location = gameBlock.getBlockLocation();
                        } else {
                            uncoveredBlock2Location = gameBlock.getBlockLocation();

                            // Wait a little bit, so user can see the blocks uncovered
                            timer = new CountDownTimer(2000, 1000) {
                                @Override
                                public void onTick(long l) {
                                    // Just Wait -> Leave the current blocks select still showing
                                }

                                @Override
                                public void onFinish() {
                                    // Check if Game is over (Announce Score and start new game)
                                    updateAndCheckGameProgress(uncoveredBlock1Location, uncoveredBlock2Location, gameBlocks);
                                }
                            };

                            timer.start();
                        }
                    }
                }
            });

            // Specify Row and Col Span
            GridLayout.Spec blockGridRowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.Spec blockGridColSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.LayoutParams blockGridLayoutParams = new GridLayout.LayoutParams(blockGridRowSpan, blockGridColSpan);

            // Add Block to Block Grid
            gameGrid.addView(block, blockGridLayoutParams);

            // Add Block to Collection of Game Blocks
            Block gameBlock = new Block(block, index);
            this.gameBlocks.add(gameBlock);

            Log.d(LOGTAG, "Add Block Number "+(index+1)+" to the Game Grid");
        }
    }

    /*
    * Check if blocks are a match, and if the game is over (launches finished game experience)
    * */
    private void updateAndCheckGameProgress(int uncoveredBlock1Location, int uncoveredBlock2Location, List<Block> gameBlocks) {
        if (uncoveredBlock1Location != -1 && uncoveredBlock2Location != -1) {
            Block gameBlock1 = this.getBlockByLocation(uncoveredBlock1Location, gameBlocks);
            Block gameBlock2 = this.getBlockByLocation(uncoveredBlock2Location, gameBlocks);

            // Reset Uncovered Locations
            this.uncoveredBlock1Location = -1;
            this.uncoveredBlock2Location = -1;

            if (gameBlock1.getHiddenNumber() != gameBlock2.getHiddenNumber()) {
                // Reset Uncovered Block
                gameBlock1.getGameBlock().setText("");
                gameBlock2.getGameBlock().setText("");
            } else {
                // If Game is Finished
                if (this.areAllGameBlocksUncovered(gameBlocks)) {
                    // Proceed to Congrats Screen
                    this.proceedToEndOfGameScreen();
                }
            }
        } else {
            timer.cancel();
        }
    }

    /*
    * Indicated if all Game Blocks are uncovered
    * */
    private boolean areAllGameBlocksUncovered(List<Block> gameBlocks) {
        boolean allUncovered = true;

        for (Block gameBlock : gameBlocks) {
            if (gameBlock.getGameBlock().getText().toString().isEmpty()) {
                allUncovered = false;
            }
        }

        return allUncovered;
    }

    /*
    * Generate Hidden Block Numbers
    * */
    private void generateHiddenBlockNumbers(List<Block> gameBlocks) {
        int numOfBlocks = gameBlocks.size();
        int numOfSetsOf2 = numOfBlocks / 2;
        ArrayList<Integer> usedBlockLocations = new ArrayList<Integer>();

        for(int index = 1; index <= numOfSetsOf2; index++) {
            // Find Hiding Location for first number
            int hidingLocation1 = this.getRandomNumber(0, gameBlocks.size() -1, usedBlockLocations);

            // Set Hidden Value in Block
            Block blockAtLocation1 = this.getBlockByLocation(hidingLocation1, gameBlocks);
            blockAtLocation1.setHiddenNumber(index);

            usedBlockLocations.add(Integer.valueOf(hidingLocation1));

            // Find Hiding Location for second number
            int hidingLocation2 = this.getRandomNumber(0, gameBlocks.size() -1, usedBlockLocations);

            // Set Hidden Value in Block
            Block blockAtLocation2 = this.getBlockByLocation(hidingLocation2, gameBlocks);
            blockAtLocation2.setHiddenNumber(index);

            usedBlockLocations.add(Integer.valueOf(hidingLocation2));
        }
    }

    /*
    * Get Block by location
    * */
    private Block getBlockByLocation(int location, List<Block> gameBlocks) {
        Block gameBlock = null;

        for (Block block : gameBlocks) {
            if (block.blockLocation == location) {
                gameBlock = block;
                break;
            }
        }

        return gameBlock;
    }

    /*
    * Get Game Block by View
    * */
    private Block getBlockByView(View view, List<Block> gameBlocks) {
        Block gameBlock = null;

        for (Block block : gameBlocks) {
            if (block.getGameBlock().getId() == view.getId()) {
                gameBlock = block;
                break;
            }
        }

        return gameBlock;
    }

    /**
     * Generates and Returns a Random number between min and max, and not in numbers to exclude
     */
    private int getRandomNumber(int min, int max, List<Integer> numbersToExclude) {
        boolean randomUniqueNumberFound = false;
        int randomNumber = -1;

        do {
            int number = (int) (Math.random() * (max - min + 1) + min);

            if (!numbersToExclude.contains(Integer.valueOf(number))) {
                randomNumber = number;
                randomUniqueNumberFound = true;
            }

        } while(randomUniqueNumberFound != true);

        return randomNumber;
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

    /*
    * Block Data Structure
    * - Contains the TextView (Game Block Cover)
    * - Contains the hidden number
    * */
    private class Block {
        private int hiddenNumber = -1;
        private int blockLocation = -1;
        private TextView gameBlock;

        public Block(TextView gameBlock, int blockLocation) {
            this.gameBlock = gameBlock;
            this.blockLocation = blockLocation;
        }

        public void setHiddenNumber(int hiddenNumber) {
            this.hiddenNumber = hiddenNumber;
        }

        public int getHiddenNumber() {
            return this.hiddenNumber;
        }

        public int getBlockLocation() {
            return blockLocation;
        }

        public TextView getGameBlock() {
            return gameBlock;
        }
    }
}