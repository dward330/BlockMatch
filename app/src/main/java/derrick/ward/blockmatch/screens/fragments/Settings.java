package derrick.ward.blockmatch.screens.fragments;

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

import com.squareup.picasso.Picasso;

import androidx.fragment.app.Fragment;
import derrick.ward.blockmatch.R;

public class Settings extends Fragment implements AdapterView.OnItemSelectedListener {
    private Context context;
    private ImageView blockCoverImage;
    private Spinner blockCoverChoices;

    public Settings(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.settings, container, false);

        // Register UI Elements
        this.blockCoverImage = settingsView.findViewById(R.id.blockCover);

        Switch musicSwitch = settingsView.findViewById(R.id.musicSwitch);
        musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            }
        });

        Switch publishScoreSwitch = settingsView.findViewById(R.id.publishScore);
        publishScoreSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            }
        });

        blockCoverChoices = settingsView.findViewById(R.id.blockCoverChoice);
        ArrayAdapter<CharSequence> stringChoiceAdapter = ArrayAdapter.createFromResource(context, R.array.block_cover_options, R.layout.spinner_item);
        stringChoiceAdapter.setDropDownViewResource(R.layout.spinner_item);
        blockCoverChoices.setAdapter(stringChoiceAdapter);
        blockCoverChoices.setOnItemSelectedListener(this);

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

        // Save cover choice information to Db

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
