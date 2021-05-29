package hackOn2021.cheerymate.post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import hackOn2021.cheerymate.R;
import hackOn2021.cheerymate.adapter.PostCommentAdapter;
import hackOn2021.cheerymate.module.PostComment;

public class PostCommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostCommentAdapter commentAdapter;
    private List<PostComment> commentList;
    private ProgressBar progressBar;
    private EditText addcomment;
    private ImageButton uploadComment, backButton;
    private String postid;
    private Toolbar toolbar;
    private Stack<PostComment> commentStack;
    int loadLimit = 50;
    private NestedScrollView nestedScrollView;
    private int commentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);


        addcomment = findViewById(R.id.editTextCreateComment);
        uploadComment = findViewById(R.id.buttonCommentUpload);
        backButton = findViewById(R.id.PostCommentShowBackButton);
        toolbar = findViewById(R.id.PostCommentShowToolbar);
        recyclerView = findViewById(R.id.postCommentShowRecyclerView);
        progressBar = findViewById(R.id.progress_bar_load_comment);
        nestedScrollView = findViewById(R.id.postCommentActivityNestedView);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PostCommentActivity.this);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        //  mLayoutManager.setReverseLayout(true);
        //  mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemViewCacheSize(20);
        commentList = new ArrayList<>();
        commentStack = new Stack<>();
        commentAdapter = new PostCommentAdapter(PostCommentActivity.this, commentList);
        recyclerView.setAdapter(commentAdapter);
        addcomment.setEnabled(false);

        //   mLayoutManager.setStackFromEnd(true);
        //    readComments();

        getCommentItem();
        getCommentCount();
        setNestedScrollView();
        buttonInitialized();

    }



    private void buttonInitialized()
    {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uploadComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addcomment.getText().toString().isEmpty())
                    Toast.makeText(PostCommentActivity.this, "Comment can't be empty.", Toast.LENGTH_SHORT).show();
                else
                {
                    addComment(FirebaseAuth.getInstance().getCurrentUser().getUid(), addcomment.getText().toString(), getTime());
                }
            }
        });
    }


    private void addComment(String uid, String comment, String time){

        DatabaseReference reference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("postComments").child(postid);

        String commentid = reference.push().getKey();

        PostComment postComment = new PostComment(uid, comment, time, commentid);

        reference.child(commentid).setValue(postComment);
        FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("CountRecord").child("post")
                .child(postid).child("commentCount").setValue(commentCount+1);
        commentCount = commentCount + 1;
        toolbar.setTitle(commentCount+" Comments");
        addcomment.setText("");

    }


    private String getTime()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd.MMM hh:mm aaa");
        String date = simpledateformat.format(calendar.getTime());
        return date.toUpperCase();
    }

    private void getCommentItem(){

        DatabaseReference reference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("postComments").child(postid);

        reference.limitToLast(loadLimit).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    commentList.clear();
                    commentStack.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostComment postComment = snapshot.getValue(PostComment.class);
                        commentStack.push(postComment);
                    }
                    addToListFromStack();
                    commentAdapter.notifyDataSetChanged();
                    addcomment.setEnabled(true);
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
                    loadMoreCommentItem();
                }
            }
        });
    }

    private void loadMoreCommentItem()
    {
        if(commentList.size()<1)
            return;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("postComments").child(postid);

        commentStack.clear();

        reference.orderByKey()//.startAfter(projectClassList.get(0).getProjectId())
                .endBefore(commentList.get(commentList.size() - 1).getCommentId())
                .limitToLast(loadLimit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PostComment postComment = snapshot.getValue(PostComment.class);
                        commentStack.push(postComment);
                    }
                    addToListFromStack();
                    commentAdapter.notifyDataSetChanged();
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
        while (!commentStack.isEmpty())
        {
            commentList.add(commentStack.pop());
        }
    }

    private void getCommentCount()
    {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("CountRecord")
                .child("post").child(postid).child("commentCount");

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                try{
                    String count = dataSnapshot.getValue().toString();
                    int comments = Integer.parseInt(count);
                    commentCount = comments;
                    toolbar.setTitle(commentCount + " Comments");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }


}