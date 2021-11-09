package com.example.huaweichat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.huaweichat.adapter.MessageAdapter;
import com.example.huaweichat.model.DBConnect;
import com.example.huaweichat.model.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.ListenerHandler;
import com.huawei.agconnect.cloud.database.OnSnapshotListener;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;

import java.util.ArrayList;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ListenerHandler mRegister;
    MessageAdapter messageAdapter;
    ArrayList<Message> messagesArrayList;
    ListView listView;
    FloatingActionButton btnSend;
    EditText txtSend;
    ImageView imageViewLogout;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listView = findViewById(R.id.listview);
        btnSend = findViewById(R.id.btn_send);
        txtSend = findViewById(R.id.text_send);
        mHandler = new Handler(Looper.getMainLooper());
        messagesArrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messagesArrayList,ChatActivity.this);
        listView.setAdapter(messageAdapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        DBConnect dbConnect = new DBConnect(ChatActivity.this);
        try {
            CloudDBZoneQuery<Message> snapshotQuery = CloudDBZoneQuery.where(Message.class)
                    .equalTo("status",true);
            mRegister = dbConnect.mCloudDBZone.subscribeSnapshot(snapshotQuery,
                    CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY, mSnapshotListener);
        } catch (AGConnectCloudDBException e) {
            Log.w("AGConnectCloudDBException", "subscribeSnapshot: " + e.getMessage());
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtSend.getText().toString().trim().equals("")){
                    DBConnect mDbConnect = new DBConnect(ChatActivity.this);
                    Message message = new Message();
                    message.setId(UUID.randomUUID().toString());
                    message.setStatus(true);
                    message.setText(txtSend.getText().toString());
                    message.setUser_id(AGConnectAuth.getInstance().getCurrentUser().getUid());
                    mDbConnect.add(message);
                    txtSend.setText("");
                }
            }
        });

        imageViewLogout = findViewById(R.id.logoutImg);
        imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AGConnectAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private OnSnapshotListener<Message> mSnapshotListener = new OnSnapshotListener<Message>() {
        @Override
        public void onSnapshot(CloudDBZoneSnapshot<Message> cloudDBZoneSnapshot, AGConnectCloudDBException e) {
            if (e != null) {
                return;
            }
            CloudDBZoneObjectList<Message> snapshotObjects = cloudDBZoneSnapshot.getUpsertedObjects();
            try {
                if (snapshotObjects != null) {
                    while (snapshotObjects.hasNext()) {
                        Message message = snapshotObjects.next();
                        messagesArrayList.add(message);
                    }
                    mHandler.post(()->{messageAdapter.notifyDataSetChanged();});
                }
            } catch (AGConnectCloudDBException snapshotException) {
                Log.w("SnapshotException", "onSnapshot:(getObject) " + snapshotException.getMessage());
            } finally {
                cloudDBZoneSnapshot.release();
            }
        }
    };

}