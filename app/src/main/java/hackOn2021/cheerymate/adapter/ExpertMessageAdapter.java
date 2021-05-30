package hackOn2021.cheerymate.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

import hackOn2021.cheerymate.module.MessageChat;

public class ExpertMessageAdapter extends MessageAdapter{
    public ExpertMessageAdapter(Context mContext, List<MessageChat> mChat) {
        super(mContext, mChat);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageChat chat = super.mChat.get(position);

        holder.show_message.setText(chat.getMessage());
        holder.time.setText(chat.getTimeStamp().toLowerCase());

    }
}
