package hackOn2021.cheerymate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hackOn2021.cheerymate.adapter.MessageAdapter;
import hackOn2021.cheerymate.module.MessageChat;

public class MessageActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser; private DatabaseReference databaseReference;
    private EditText editTextMessageField; private ImageButton buttonMessageSend;
    private MessageAdapter messageAdapter; private List<MessageChat> messageChat; RecyclerView recyclerView;
    private ProgressBar messgaeProgressBar;
    private Toolbar toolbar; private SwipeRefreshLayout swipeRefreshLayout; private LinearLayoutManager linearLayout;
    private int loadLimit = 50; private List<MessageChat> tempList;
  //  private SharedPreferences sharedPreferences;
    private boolean activityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = findViewById(R.id.MessageActivityToolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout = findViewById(R.id.messageActivitySwipeRefreshLayout);
        messgaeProgressBar = findViewById(R.id.progressBarMessage);
        getSupportActionBar().setTitle("Group Chat");
        tempList = new ArrayList<>();
        recyclerView = findViewById(R.id.message_recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayout = new LinearLayoutManager(MessageActivity.this);
        messageChat = new ArrayList<>();
        messageAdapter = new MessageAdapter(getApplicationContext(),messageChat);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(linearLayout);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("chat");
        editTextMessageField = findViewById(R.id.editTextSendMessage);
        buttonMessageSend = findViewById(R.id.buttonMessageSend);
       // sharedPreferences = getSharedPreferences("MessagePreference", Context.MODE_PRIVATE);
        editTextMessageField.setEnabled(false);
      //  FeedActivity.toolbarMenu.getItem(0).setIcon(R.drawable.ic_chat);
        checkMessage();
        setButtonActivity();
        setSwipeRefreshLayout();
        activityRunning = true;
    }

    public void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMoreMessage();
            }
        });
    }


    private void loadMoreMessage()
    {
        if(messageChat.size() < 1)
        {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        tempList.clear();
        tempList.addAll(messageChat);

        databaseReference.orderByKey()
                .endBefore(messageChat.get(0).getMessageId()).limitToLast(loadLimit).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try{
                            messageChat.clear();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                MessageChat message = dataSnapshot.getValue(MessageChat.class);
                                messageChat.add(message);
                            }

                            messageChat.addAll(tempList);

                            messageAdapter.notifyDataSetChanged();

                            swipeRefreshLayout.setRefreshing(false);
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

    private void setButtonActivity()
    {
        buttonMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageSendButtonMethod();
            }
        });
    }

    private void messageSendButtonMethod()
    {
        String message = editTextMessageField.getText().toString();
        String name = "";
        if(firebaseUser.getDisplayName() != null)
            name = firebaseUser.getDisplayName();
        String messageId = databaseReference.push().getKey();
        MessageChat messageChat = new MessageChat(name, message, firebaseUser.getUid(), getTime(), messageId);
        if(!message.isEmpty())
            databaseReference.child(messageId).setValue(messageChat);
        editTextMessageField.setText(null);
    }

    String getTime()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd.MMM hh:mm aaa");
        String date = simpledateformat.format(calendar.getTime());
        Log.i("SERVER TIME", date);
        return date;
    }


    void checkMessage()
    {
        databaseReference.limitToLast(loadLimit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    messageChat.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MessageChat message = dataSnapshot.getValue(MessageChat.class);
                        messageChat.add(message);
                    }
                    messageChat.remove(messageChat.size() - 1);
                    linearLayout.scrollToPosition(messageChat.size() - 1);
                    messgaeProgressBar.setVisibility(View.GONE);
                    editTextMessageField.setEnabled(true);
                    messageAdapter.notifyDataSetChanged();
                    addNewMessageListener();
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

    void addNewMessageListener()
    {
        databaseReference.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    MessageChat message = snapshot.getValue(MessageChat.class);
                    messageChat.add(message);
                    messageAdapter.notifyDataSetChanged();
                    linearLayout.scrollToPosition(messageChat.size() - 1);
//                    if(activityRunning){
//                        sharedPreferences.edit().putString("messageKey", message.getMessageId()).apply();
//                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        activityRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityRunning = false;
    }

}