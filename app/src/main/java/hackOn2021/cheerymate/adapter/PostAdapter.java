package hackOn2021.cheerymate.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.List;

import hackOn2021.cheerymate.R;
import hackOn2021.cheerymate.module.Post;
import hackOn2021.cheerymate.post.PostCommentActivity;
import hackOn2021.cheerymate.post.PostImageOnClickZoom;
import hackOn2021.cheerymate.post.PostLikeOnClick;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPosts.get(position);
        if (post.getPostImageUri().equals("")) {
            holder.post_image.setVisibility(View.GONE);
        } else {
            holder.post_image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(post.getPostImageUri())
                    // .apply(new RequestOptions().override(-36,350))
                    // .fitCenter()
                    .into(holder.post_image);

        }
        if (post.getPostText().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getPostText());
        }

        holder.uploadTime.setText(post.getTime());

        publisherInfo(holder.image_profile, holder.username, post.getUid(), mContext);

        isLiked(post.getPostId(), holder.like);
        nrLikes(holder.likes, post.getPostId());

        getComments(post.getPostId(), holder.comments);

        holder.like.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app")
                        .getReference().child("CountRecord")
                        .child("post").child(post.getPostId())
                        .child("likeCount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try{
                            String count = snapshot.getValue().toString();
                            int likeCount = Integer.parseInt(count);
                            setLikeOrDislike(holder.like, post.getPostId(), likeCount);
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
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PostLikeOnClick.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", post.getPostId());
                mContext.startActivity(intent);
            }
        });


        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCommentIntent(post.getPostId());
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCommentIntent(post.getPostId());
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PostImageOnClickZoom.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("imageUrl", post.getPostImageUri());
                mContext.startActivity(intent);
            }
        });

    }

    private void startCommentIntent(String postId) {
        Intent intent = new Intent(mContext, PostCommentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("postid", postId);
        mContext.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image, like, comment, save, more;
        public TextView username, likes, uploadTime, description, comments;

        public ImageViewHolder(View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.postUserId);
            username = itemView.findViewById(R.id.postUsername);
            post_image = itemView.findViewById(R.id.postImage);
            like = itemView.findViewById(R.id.postLike);
            comment = itemView.findViewById(R.id.postComment);
            likes = itemView.findViewById(R.id.postLikesCount);
            description = itemView.findViewById(R.id.postDescription);
            comments = itemView.findViewById(R.id.postCommentsCount);
            uploadTime = itemView.findViewById(R.id.postUploadTime);

        }
    }


    public static void publisherInfo(ImageView image_profile, TextView username, String userId, Context mContext) {
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
                        String imageUrlSmall = snapshot.child("imageUrlSmall").getValue().toString();
                        if (!name.isEmpty())
                            username.setText(name);

                        Glide.with(mContext).load(imageUrlSmall).into(image_profile);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getComments(String postId, final TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("CountRecord")
                .child("post").child(postId).child("commentCount");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                try{
                    comments.setText("View All " + dataSnapshot.getValue() + " Comments");
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

    private void isLiked(String postid, ImageView imageView) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("postLikes").child(postid).child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue().toString().equals("true")) {
                        //  imageView.setImageResource(R.drawable.ic_baseline_thumb_uped_24);
                        imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_thumb_uped_24));
                        imageView.setTag("liked");
                    } /*else {
                        //imageView.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                        imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_thumb_up_24));
                        imageView.setTag("like");
                    }*/
                } catch (Exception e) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_thumb_up_24));
                    imageView.setTag("like");
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    /*
     */
    private void nrLikes(final TextView likes, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("CountRecord")
                .child("post").child(postId).child("likeCount");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                try{
                    likes.setText(dataSnapshot.getValue() + " likes");
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

    private void setLikeOrDislike(ImageView imageView, String postId, int likeCount) {
        if (imageView.getTag().equals("like")) {

            FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference().child("CountRecord")
                    .child("post").child(postId).child("likeCount").setValue(likeCount+1);
            FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference().child("postLikes").child(postId)
                    .child(firebaseUser.getUid()).setValue(true);


            //  addNotification(post.getPublisher(), post.getPostid());
        } else {
            FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference().child("CountRecord")
                    .child("post").child(postId).child("likeCount").setValue(likeCount-1);
            FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference().child("postLikes").child(postId)
                    .child(firebaseUser.getUid()).removeValue();
        }

    }

}
