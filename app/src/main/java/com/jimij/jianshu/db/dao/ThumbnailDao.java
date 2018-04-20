package com.jimij.jianshu.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.jimij.jianshu.db.entity.Thumbnail;

/**
 * Created by 铖哥 on 2018/4/10.
 */

@Dao
public interface ThumbnailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert( Thumbnail thumbnail );

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertIgnore( Thumbnail thumbnail );

    @Query("DELETE FROM thumbnails WHERE path=:path")
    void delteThumbnail(String path);

    @Query("SELECT * FROM thumbnails WHERE path=:path LIMIT 1")
    Thumbnail getThumbnail(String path);
}
