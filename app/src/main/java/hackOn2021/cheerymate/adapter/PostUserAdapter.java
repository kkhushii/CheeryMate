package hackOn2021.cheerymate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import hackOn2021.cheerymate.R;

public class PostUserAdapter extends RecyclerView.Adapter<PostUserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<String> uidList;

    public PostUserAdapter(Context context, List<String> idList){
        mContext = context;
        this.uidList = idList;
    }

    @NonNull
    @Override
    public PostUserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_like_show_user, parent, false);
        return new PostUserAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostUserAdapter.ImageViewHolder holder, final int position) {

        PostAdapter.publisherInfo( holder.imageViewProfilePhoto,holder.textViewfullName, uidList.get(position), mContext);

    }

    @Override
    public int getItemCount() {
        return uidList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewfullName;
        public ImageView imageViewProfilePhoto;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewfullName = itemView.findViewById(R.id.postLikeShowUserFullName);
            imageViewProfilePhoto = itemView.findViewById(R.id.postLikeShowUserProfilePhoto);
        }
    }


}