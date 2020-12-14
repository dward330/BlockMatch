package derrick.ward.blockmatch.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import derrick.ward.blockmatch.R;

public class ChatMessages extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chatMessages = inflater.inflate(R.layout.chat_messages, container, false);

        return chatMessages;
    }
}
