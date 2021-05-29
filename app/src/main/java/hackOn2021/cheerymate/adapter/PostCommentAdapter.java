package hackOn2021.cheerymate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import hackOn2021.cheerymate.R;
import hackOn2021.cheerymate.module.PostComment;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.ImageViewHolder> {

    private Context mContext;
    private List<PostComment> mComment;

    public PostCommentAdapter(Context context, List<PostComment> comments){
        mContext = context;
        mComment = comments;
    }

    @NonNull
    @Override
    public PostCommentAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_comment, parent, false);
        return new PostCommentAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostCommentAdapter.ImageViewHolder holder, final int position) {

        PostComment comment = mComment.get(position);

        holder.textViewComment.setText(comment.getComment());
        holder.textViewTime.setText(comment.getTime());

        PostAdapter.publisherInfo(holder.image_profile, holder.textViewUsername, comment.getUid(), mContext);

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile;
        public TextView textViewUsername, textViewTime, textViewComment;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.postCommentProfilePic);
            textViewTime = itemView.findViewById(R.id.postCommentUploadTime);
            textViewUsername = itemView.findViewById(R.id.postCommentUsername);
            textViewComment = itemView.findViewById(R.id.postComment);
        }
    }


}