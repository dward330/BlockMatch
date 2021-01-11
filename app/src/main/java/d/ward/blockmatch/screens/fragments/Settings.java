package d.ward.blockmatch.screens.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import d.ward.blockmatch.R;
import d.ward.blockmatch.services.SettingsDBHelper;

public class Settings extends Fragment implements AdapterView.OnItemSelectedListener {
    private Context context;
    private ImageView blockCoverImage;
    private Spinner blockCoverChoices;
    private SettingsDBHelper settingsDBHelper;

    public Settings(Context context) {
        this.context = context;
        this.settingsDBHelper = new SettingsDBHelper(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.settings, container, false);

        d.ward.blockmatch.models.Settings gameSettings = this.settingsDBHelper.getSettings();

        // Register UI Elements
        this.blockCoverImage = settingsView.findViewById(R.id.blockCover);

        Switch musicSwitch = settingsView.findViewById(R.id.musicSwitch);
        musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //Fetch Latest Settings
                d.ward.blockmatch.models.Settings latestGameSettings = settingsDBHelper.getSettings();

                // Update Settings
                latestGameSettings.playMusic = isChecked ? 1 : 0;

                // Save Latest Settings Changes
                settingsDBHelper.updateSettings(latestGameSettings.id, context, latestGameSettings);
            }
        });

        Switch publishScoreSwitch = settingsView.findViewById(R.id.publishScore);
        publishScoreSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //Fetch Latest Settings
                d.ward.blockmatch.models.Settings latestGameSettings = settingsDBHelper.getSettings();

                // Update Settings
                latestGameSettings.publishScore = isChecked ? 1 : 0;

                // Save Latest Settings Changes
                settingsDBHelper.updateSettings(latestGameSettings.id, context, latestGameSettings);
            }
        });

        blockCoverChoices = settingsView.findViewById(R.id.blockCoverChoice);
        ArrayAdapter<CharSequence> stringChoiceAdapter = ArrayAdapter.createFromResource(context, R.array.block_cover_options, R.layout.spinner_item);
        stringChoiceAdapter.setDropDownViewResource(R.layout.spinner_item);
        blockCoverChoices.setAdapter(stringChoiceAdapter);
        blockCoverChoices.setOnItemSelectedListener(this);

        // Apply the current settings to music switch
        if (gameSettings.playMusic == 1) {
            musicSwitch.setChecked(true);
        } else {
            musicSwitch.setChecked(false);
        }

        // Apply the current settings to publish score switch
        if (gameSettings.publishScore == 1) {
            publishScoreSwitch.setChecked(true);
        } else {
            publishScoreSwitch.setChecked(false);
        }

        // Apply the current settings to block cover image
        blockCoverChoices.setSelection(gameSettings.blockCoverImage, true);

        return settingsView;
    }

    /**
     * Event Handler for when a block cover image choice is made
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Drawable blockCoverDrawable;

        // Choice Block Cover Image
        switch (pos) {
            case 0:
                blockCoverDrawable = this.context.getResources().getDrawable(R.drawable.gift_box);
                blockCoverImage.setImageDrawable(blockCoverDrawable);
                break;
            case 1:
                blockCoverDrawable = this.context.getResources().getDrawable(R.drawable.santa_claus);
                blockCoverImage.setImageDrawable(blockCoverDrawable);
                break;
            case 2:
                blockCoverDrawable = this.context.getResources().getDrawable(R.drawable.reindeer);
                blockCoverImage.setImageDrawable(blockCoverDrawable);
                break;
            case 3:
                blockCoverDrawable = this.context.getResources().getDrawable(R.drawable.elf);
                blockCoverImage.setImageDrawable(blockCoverDrawable);
                break;
        }

        // Save block cover choice information to Db
        //Fetch Latest Settings
        d.ward.blockmatch.models.Settings latestGameSettings = settingsDBHelper.getSettings();

        // Update Settings
        latestGameSettings.blockCoverImage = pos;

        // Save Latest Settings Changes
        settingsDBHelper.updateSettings(latestGameSettings.id, context, latestGameSettings);
    }

    /**
     * Event Handler for when there is no block cover image choice made
     * @param parent
     */
    public void onNothingSelected(AdapterView<?> parent) {
        // Set Spinner Choice to Gift Box
        blockCoverChoices.setSelection(0, true);
    }
}
