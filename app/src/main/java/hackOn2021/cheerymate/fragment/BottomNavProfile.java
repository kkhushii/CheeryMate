package hackOn2021.cheerymate.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import hackOn2021.cheerymate.MainActivity;
import hackOn2021.cheerymate.R;

public class BottomNavProfile extends Fragment {

    private CardView cardViewLogOut;
    private ImageView imageViewProfileImage;
    private TextView textViewUserName;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_nav_profile, container, false);
        cardViewLogOut = view.findViewById(R.id.profile_logout);
        imageViewProfileImage = view.findViewById(R.id.userInfoProfilePicture);
        textViewUserName = view.findViewById(R.id.userInfoUserName);
        progressBar = view.findViewById(R.id.userInfoProgressBarSpinner);

       getUserDetails(imageViewProfileImage, textViewUserName,
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getActivity(), progressBar);
        setButton();
        return view;
    }

    void setButton()
    {
        cardViewLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutMethod();
            }
        });
    }

    public void signOutMethod()
    {
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Toast.makeText(getActivity(), "Signout Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finishAffinity();
                    }
                });


    }

    public static void getUserDetails(ImageView image_profile, TextView username,
                                      String userId, Context mContext, ProgressBar progressBar)
    {
        if(userId.equals(""))
        {
            username.setText("Anonymous");
            image_profile.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            DatabaseReference reference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference().child("userDatabase").child("users").child(userId);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String name = snapshot.child("fullName").getValue().toString();
                        String imageUrlSmall = snapshot.child("imageUrl").getValue().toString();
                        if (!name.isEmpty())
                            username.setText(name);

                        Glide.with(mContext).load(imageUrlSmall).into(image_profile);
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


}
