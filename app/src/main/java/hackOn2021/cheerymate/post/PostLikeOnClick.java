package hackOn2021.cheerymate.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import hackOn2021.cheerymate.R;
import hackOn2021.cheerymate.adapter.PostUserAdapter;

public class PostLikeOnClick extends AppCompatActivity {


    private String id; private int loadLimit = 50;

    private List<String> idList; private Stack<String> stringStack;
    private ImageButton backButton;
    private RecyclerView recyclerView;
    private Toolbar likeCountTextView;
    private PostUserAdapter userAdapter;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_like_on_click);

        idList = new ArrayList<>();
        stringStack = new Stack<>();
        progressBar = findViewById(R.id.progress_bar_load_city_members_show);
        likeCountTextView = findViewById(R.id.cityMemberShowToolbar);
        nestedScrollView = findViewById(R.id.cityMemberShowNestedScrollView);
        backButton = findViewById(R.id.cityMemberShowButtonBack);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        recyclerView = findViewById(R.id.cityMemberShowRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PostLikeOnClick.this);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        userAdapter = new PostUserAdapter(PostLikeOnClick.this, idList);
        recyclerView.setAdapter(userAdapter);
        setSupportActionBar(likeCountTextView);
        likeCountTextView.setTitle("Likes");


        reference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("postLikes").child(id);

        //  getLikes();
        getLike();
        getLikeCount();
        setNestedScrollView();
        setBackButton();
    }

    private void setBackButton()
    {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getLike(){

        reference.limitToLast(loadLimit).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    idList.clear();
                    stringStack.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        stringStack.push(snapshot.getKey());
                    }
                    addToListFromStack();
                    userAdapter.notifyDataSetChanged();
                    //       displayPost(postList);
                    progressBar.setVisibility(View.GONE);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }

        });

    }

    private void setNestedScrollView()
    {
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView
                        .getScrollY()));

                if (diff == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    loadMoreLike();
                }
            }
        });
    }

    private void loadMoreLike()
    {
        if(idList.size()<1)
            return;

        stringStack.clear();

        reference.orderByKey()//.startAfter(projectClassList.get(0).getProjectId())
                .endBefore(idList.get(idList.size() - 1))
                .limitToLast(loadLimit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        stringStack.push(snapshot.getKey());
                    }
                    addToListFromStack();
                    userAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }

        });

    }

    private void addToListFromStack()
    {
        while (!stringStack.isEmpty())
        {
            idList.add(stringStack.pop());
        }
    }

    private void getLikeCount()
    {
        FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("CountRecord").child("post").child(id).child("likeCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    String count = snapshot.getValue().toString();
                    likeCountTextView.setTitle(count + " Likes");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}