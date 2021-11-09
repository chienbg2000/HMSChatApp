package com.example.huaweichat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.huaweichat.R;
import com.example.huaweichat.model.DBConnect;
import com.example.huaweichat.model.Message;
import com.example.huaweichat.model.User;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {
    ArrayList<Message> listMessage;
    Context mContext;

    public MessageAdapter(ArrayList<Message> listMessage, Context context) {
        this.listMessage = listMessage;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return listMessage.size();
    }

    @Override
    public Object getItem(int position) {
        return listMessage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewMessage;
        if (convertView == null) {
            viewMessage = View.inflate(parent.getContext(), R.layout.message, null);
        } else viewMessage = convertView;

        Message message = (Message) getItem(position);
        TextView textViewLeft = viewMessage.findViewById(R.id.text_left);
        ImageView imageView = viewMessage.findViewById(R.id.profile_image);
        TextView textViewRight = viewMessage.findViewById(R.id.text_right);
        TextView txtUserName = viewMessage.findViewById(R.id.username);
        LinearLayout linearLayoutLeft = viewMessage.findViewById(R.id.boderLeft);
        LinearLayout linearLayoutRight = viewMessage.findViewById(R.id.boderRight);
        LinearLayout linearLayoutInfo = viewMessage.findViewById(R.id.info);

        if (! message.getUser_id().equals(AGConnectAuth.getInstance().getCurrentUser().getUid())){
            linearLayoutRight.setVisibility(View.INVISIBLE);
            linearLayoutLeft.setVisibility(View.VISIBLE);
            linearLayoutInfo.setVisibility(View.VISIBLE);

            imageView.setVisibility(View.VISIBLE);
            textViewLeft.setText(message.getText());

            DBConnect cloudDBConnect = new DBConnect(mContext);

            Task<CloudDBZoneSnapshot<User>> queryTask = cloudDBConnect.mCloudDBZone.executeQuery(
                    CloudDBZoneQuery.where(User.class).equalTo("id", message.getUser_id()),
                    CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
            queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<User>>() {
                @Override
                public void onSuccess(CloudDBZoneSnapshot<User> userCloudDBZoneSnapshot) {
                    CloudDBZoneObjectList<User> usrCursor = userCloudDBZoneSnapshot.getSnapshotObjects();
                    try {
                        if (usrCursor.hasNext()) {
                            User user = usrCursor.next();
                            if (user.getImgage_url() != null){
                                txtUserName.setText(user.getUser_name());
                                Glide.with(viewMessage.getContext()).load(user.getImgage_url()).into(imageView);
                            }
                        }
                    } catch (AGConnectCloudDBException e) {
                        Log.e("TAG", "processQueryResult: " + e.getMessage());
                    } finally {
                        userCloudDBZoneSnapshot.release();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e("Error",e.toString());
                }
            });
        }
        else {
            imageView.setVisibility(View.INVISIBLE);
            linearLayoutLeft.setVisibility(View.INVISIBLE);
            linearLayoutInfo.setVisibility(View.INVISIBLE);
            linearLayoutRight.setVisibility(View.VISIBLE);
            textViewRight.setText(message.getText());
        }

        return viewMessage;
    }
}
