package d.ward.blockmatch.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import d.ward.blockmatch.R;
import d.ward.blockmatch.services.adapters.ConversationsAdapter;

public class Conversations extends Fragment {
    private String signedInUser;

    public Conversations(String signInUser) {
        this.signedInUser = signInUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chatMessages = inflater.inflate(R.layout.conversations, container, false);

        RecyclerView recyclerView = chatMessages.findViewById(R.id.chatMessages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(chatMessages.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Create Adapter
        ConversationsAdapter conversationsAdapter = new ConversationsAdapter(recyclerView, this.signedInUser);

        // Set Adapter
        recyclerView.setAdapter(conversationsAdapter);

        return chatMessages;
    }
}
