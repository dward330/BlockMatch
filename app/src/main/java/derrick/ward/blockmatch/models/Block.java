package derrick.ward.blockmatch.models;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Block Data Structure
 * - Contains the TextView (Game Block Cover)
 * - Contains the hidden number
 * */
public class Block {
    private int hiddenNumber = -1;
    private int blockLocation;
    private TextView textBlock;
    private Context context;
    private ImageView imageBlock;
    private boolean covered = true;

    public Block(TextView textBlock, ImageView imageBlock, int blockLocation) {
        this.textBlock = textBlock;
        this.imageBlock = imageBlock;
        this.blockLocation = blockLocation;
    }

    public void setHiddenNumber(int hiddenNumber) {
        this.hiddenNumber = hiddenNumber;
        this.textBlock.setText(String.valueOf(hiddenNumber));
    }

    public int getHiddenNumber() {
        return this.hiddenNumber;
    }

    public int getBlockLocation() {
        return blockLocation;
    }

    public View getBlock() {
        if (this.covered) {
            return imageBlock;
        } else {
            return textBlock;
        }
    }

    public void setText(String text) {
        this.textBlock.setText(text);
    }

    public String getText() {
        return this.textBlock.getText().toString();
    }

    public void hideBlock() {
        this.covered = true;
    }

    public void uncoverBlock() {
        this.covered = false;
    }

    public boolean isHidden() {
        return this.covered;
    }
}
