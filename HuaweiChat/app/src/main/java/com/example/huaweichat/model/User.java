/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 * Generated by the CloudDB ObjectType compiler.  DO NOT EDIT!
 */
package com.example.huaweichat.model;

import com.huawei.agconnect.cloud.database.CloudDBZoneObject;
import com.huawei.agconnect.cloud.database.Text;
import com.huawei.agconnect.cloud.database.annotations.DefaultValue;
import com.huawei.agconnect.cloud.database.annotations.EntireEncrypted;
import com.huawei.agconnect.cloud.database.annotations.NotNull;
import com.huawei.agconnect.cloud.database.annotations.Indexes;
import com.huawei.agconnect.cloud.database.annotations.PrimaryKeys;

import java.util.Date;

/**
 * Definition of ObjectType User.
 *
 * @since 2021-11-09
 */
@PrimaryKeys({"id"})
public final class User extends CloudDBZoneObject {
    private String id;

    private String imgage_url;

    private String user_name;

    public User() {
        super(User.class);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setImgage_url(String imgage_url) {
        this.imgage_url = imgage_url;
    }

    public String getImgage_url() {
        return imgage_url;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

}
