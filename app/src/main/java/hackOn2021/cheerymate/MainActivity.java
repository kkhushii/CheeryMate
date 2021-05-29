package hackOn2021.cheerymate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import hackOn2021.cheerymate.module.UserData;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonGoogleSignIn;
    private final int RC_SIGN_IN_MENTOR = 1001;
    private final int RC_SIGN_IN_USER = 2001;
    private TextView textViewMentorSignIn;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMentorSignIn = findViewById(R.id.textViewMentorSignIn);
        buttonGoogleSignIn = findViewById(R.id.gsignin);

        databaseReference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("userDatabase");
        Log.i("Database", databaseReference.toString());
        initialiseButton();

        Log.i("Database uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            checkCurrentUser(FirebaseAuth.getInstance().getCurrentUser(), 0);


    }

    private void checkCurrentUser(FirebaseUser user, int code)
    {
        //i = 0 then user; i = 1 then mentors

        DatabaseReference reference;

       if(code==0)
           reference = databaseReference.child("users").child(user.getUid());
       else
           reference = databaseReference.child("mentors").child(user.getUid());

           reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                           Log.i("UserData", snapshot.child("uid").getValue().toString());
                            if(snapshot.child("uid").getValue().toString().equals(user.getUid()))
                            {
                                startIntent(code);
                            }
                        }catch (Exception e)
                        {
                            if(code==0)
                                checkCurrentUser(user, 1);
                            Log.i("UserData", "failure");
                            e.printStackTrace();
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }


    private void initialiseButton()
    {
        buttonGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(RC_SIGN_IN_USER);
            }
        });

        textViewMentorSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(RC_SIGN_IN_MENTOR);
            }
        });
    }

    protected void signIn(int RC_SIGN_IN)
    {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

           startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private void pushUserToDataBase(String userStatus)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserData userData = new UserData(user.getUid(), userStatus,
                user.getDisplayName(), user.getPhotoUrl().toString(), getSmallUserImageUrl(user));
        databaseReference.child(userStatus).child(user.getUid()).setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i("Database", "Success");
            }
        });
        Log.i("Database", "CodeScopeEnd");
    }

    private String getSmallUserImageUrl(FirebaseUser user)
    {
                for (UserInfo profile : user.getProviderData()) {
                if (GoogleAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                        if (user.getPhotoUrl() != null) {
                            Uri photoUrl = user.getPhotoUrl();
                            String originalPieceOfUrl = "s96-c";
                            String newPieceOfUrlToAdd = "s400-c";
                            String photoPath = photoUrl.toString();
                            String newString = photoPath.replace(originalPieceOfUrl, newPieceOfUrlToAdd);
                            return newString;
                        }
                    }

                }

                return null;
    }

    private void startIntent(int i)
    {
        // i = 0 then User; i = 1 then mentors
        Intent intent;
        if(i==0)
            intent = new Intent(this, FeedActivity.class);
        else
            intent = new Intent(this, MentorFeed.class);

          startActivity(intent);
          finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_MENTOR || requestCode == RC_SIGN_IN_USER) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Sign in success!", Toast.LENGTH_SHORT).show();

                if(requestCode == RC_SIGN_IN_USER) {
                    pushUserToDataBase("users");
                    startIntent(0);
                }
                else {
                    pushUserToDataBase("mentors");
                    startIntent(1);
                }

            } else {
                Toast.makeText(this, "Sign in failure, try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}