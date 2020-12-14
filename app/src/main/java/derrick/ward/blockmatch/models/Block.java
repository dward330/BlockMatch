package derrick.ward.blockmatch.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Block Data Structure
 * - Contains Game Block Information
 * */
public class Block {
    private int hiddenNumber = -1;
    private int blockLocation;
    private TextView textBlock;
    private ImageView imageBlock;
    private boolean covered = true;

    public Block(TextView textBlock, ImageView imageBlock, int blockLocation) {
        this.textBlock = textBlock;
        this.imageBlock = imageBlock;
        this.blockLocation = blockLocation;
    }

    /**
     * Sets Hidden Number
     * @param hiddenNumber number to hide
     */
    public void setHiddenNumber(int hiddenNumber) {
        this.hiddenNumber = hiddenNumber;
        this.textBlock.setText(String.valueOf(hiddenNumber));
    }

    /**
     * Gets Hidden Number
     * @return number that is hidden
     */
    public int getHiddenNumber() {
        return this.hiddenNumber;
    }

    /**
     * Gets Block Location
     * @return Block's Location
     */
    public int getBlockLocation() {
        return blockLocation;
    }

    /**
     * Gets Blocks UI View
     * @return UI View Representation of block
     */
    public View getBlock() {
        if (this.covered) {
            return imageBlock;
        } else {
            return textBlock;
        }
    }

    /**
     * Hides Block
     */
    public void hideBlock() {
        this.covered = true;
    }

    /**
     * Uncovers what block is covering
     */
    public void uncoverBlock() {
        this.covered = false;
    }

    /**
     * Checks if a block is covered
     * @return true if block is uncovered
     */
    public boolean isHidden() {
        return this.covered;
    }
}
