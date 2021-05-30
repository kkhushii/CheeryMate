package hackOn2021.cheerymate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import hackOn2021.cheerymate.ExpertChat;
import hackOn2021.cheerymate.R;

public class BottomNavExpertConnect extends Fragment {

    CardView cardViewExpertChat;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_nav_expert_connect, container, false);
        cardViewExpertChat = view.findViewById(R.id.expertConnectChatSupport);
        setButton();
        return view;
    }

    void setButton()
    {
        cardViewExpertChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ExpertChat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

}
