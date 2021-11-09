package com.example.huaweichat.model;

import android.content.Context;
import android.util.Log;

import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

public class DBConnect {
    public AGConnectCloudDB mCloudDB;
    public CloudDBZone mCloudDBZone ;

    public DBConnect(Context context){
        AGConnectCloudDB.initialize(context);
        mCloudDB = AGConnectCloudDB.getInstance ();
        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
        } catch (AGConnectCloudDBException e) {
            e.printStackTrace();
        }

        CloudDBZoneConfig mConfig = new CloudDBZoneConfig("Zone1",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig.setPersistenceEnabled(true);
        try {
            mCloudDBZone = mCloudDB.openCloudDBZone(mConfig, true);
        } catch (AGConnectCloudDBException e) {
            Log.e("TAG", "openCloudDBZone: " + e.getMessage());
        }
    }
    public void add(CloudDBZoneObject object ){
        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(object);
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer cloudDBZoneResult) {
                Log.e("TAG", "Upsert " + cloudDBZoneResult + " records");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("TAG", "Insert failed");

            }
        });
    }
    public void delete(CloudDBZoneObject object){
        com.huawei.hmf.tasks.Task<Integer> deleteTask = mCloudDBZone.executeDelete(object);
        deleteTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
            }
        });
    }
}
