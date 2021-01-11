package d.ward.blockmatch.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import d.ward.blockmatch.R;

/*
* How To Fragment/Screen
* */
public class HowTo extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View howTo = inflater.inflate(R.layout.how_to, container, false);

        return howTo;
    }
}
