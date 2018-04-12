package com.jimij.jianshu.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.provider.MediaStore;

import com.jimij.jianshu.db.dao.ThumbnailDao;
import com.jimij.jianshu.db.entity.Thumbnail;

/**
 * Created by 铖哥 on 2018/4/10.
 */

@Database(entities = Thumbnail.class,version = 1,exportSchema = false)
public abstract class ThumbnailDatabase extends RoomDatabase {
    public abstract ThumbnailDao thumbnailDao();
}
