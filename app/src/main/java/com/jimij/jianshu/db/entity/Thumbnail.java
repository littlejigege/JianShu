package com.jimij.jianshu.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by 铖哥 on 2018/4/10.
 */

@Entity(tableName = "thumbnails")
public class Thumbnail {

    public Thumbnail(@NonNull String path, String bitmap) {
        this.path = path;
        this.bitmap = bitmap;
    }

    @PrimaryKey
    @NonNull
    String path;
    String bitmap;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }
}
