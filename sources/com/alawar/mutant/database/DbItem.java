package com.alawar.mutant.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/* loaded from: classes.dex */
public class DbItem {
    static Random random = new Random(System.currentTimeMillis());
    private String _id;
    private String _tag;
    private String _value;

    public DbItem(String tag, String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        this._id = sdf.format((Date) new java.sql.Date(System.currentTimeMillis())) + "_" + (random.nextInt(900) + 100);
        this._value = value;
        this._tag = tag;
    }

    public DbItem(String uid, String tag, String value) {
        this._id = uid;
        this._value = value;
        this._tag = tag;
    }

    public String getId() {
        return this._id;
    }

    public String getValue() {
        return this._value;
    }

    public String getTag() {
        return this._tag;
    }

    public String toString() {
        return this._id + ":" + this._value + ":" + this._tag;
    }
}
