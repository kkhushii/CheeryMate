package hackOn2021.cheerymate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

import hackOn2021.cheerymate.R;
import hackOn2021.cheerymate.module.MessageChat;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<MessageChat> mChat;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<MessageChat> mChat){
        this.mChat = mChat;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_right_item, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_left_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        MessageChat chat = mChat.get(position);

        int positionOfMessage = getItemViewType(position);

        holder.show_message.setText(chat.getMessage());

        holder.time.setText(chat.getTimeStamp().toLowerCase());

        if(positionOfMessage==MSG_TYPE_LEFT)
        {
            if(!chat.getUserName().isEmpty()){
                String UserFullName = chat.getUserName();

                holder.senderName.setText(UserFullName);
            }

        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public TextView time;
        public TextView senderName;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            time = itemView.findViewById(R.id.timeStamp);
            senderName = itemView.findViewById(R.id.senderName);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getUid().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }


}