package hackOn2021.cheerymate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import hackOn2021.cheerymate.R;
import hackOn2021.cheerymate.adapter.PostAdapter;
import hackOn2021.cheerymate.module.Post;

public class BottomNavFeed extends Fragment {

    private ImageButton button; private RecyclerView recyclerView;

    private PostAdapter postAdapter;
    private List<Post> postList;
    private ProgressBar progress_circular; private LinearLayoutManager mLayoutManager;
    private int loadLimit = 50;
    private Stack<Post> postStack;
    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_nav_feed, container, false);
        button = view.findViewById(R.id.createNewPost);

        recyclerView = view.findViewById(R.id.recycler_view_bottom_nav_feed);
        progress_circular = view.findViewById(R.id.progressBarBottomNav);
        nestedScrollView = view.findViewById(R.id.bottom_nav_feed_nested_scroll_view);
        swipeRefreshLayout = view.findViewById(R.id.bottom_nav_feed_swipe_refresh_layout);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postStack = new Stack<>();
        postAdapter = new PostAdapter(getActivity().getApplicationContext(), postList);
        mLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setAdapter(postAdapter);
        getPostItem();
        setNestedScrollView();
        setSwipeRefreshLayout();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), PostUploadActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setSwipeRefreshLayout()
    {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPostItem();
            }
        });
    }

    private void getPostItem(){

        DatabaseReference reference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("post");

        reference.limitToLast(loadLimit).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    postList.clear();
                    postStack.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post post = snapshot.getValue(Post.class);
                        postStack.push(post);
                    }
                    addToListFromStack();
                    postAdapter.notifyDataSetChanged();
                    //       displayPost(postList);
                    progress_circular.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progress_circular.setVisibility(View.GONE);
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
                    progress_circular.setVisibility(View.VISIBLE);
                    loadMorePostItem();
                }
            }
        });
    }

    private void loadMorePostItem()
    {
        if(postList.size()<1)
            return;

        DatabaseReference reference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("post");

        postStack.clear();

        reference.orderByKey()//.startAfter(projectClassList.get(0).getProjectId())
                .endBefore(postList.get(postList.size() - 1).getPostId())
                .limitToLast(loadLimit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post post = snapshot.getValue(Post.class);
                        postStack.push(post);
                    }
                    addToListFromStack();
                    postAdapter.notifyDataSetChanged();
                    progress_circular.setVisibility(View.GONE);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progress_circular.setVisibility(View.GONE);
            }

        });

    }

    private void addToListFromStack()
    {
        while (!postStack.isEmpty())
        {
            postList.add(postStack.pop());
        }
    }


}
