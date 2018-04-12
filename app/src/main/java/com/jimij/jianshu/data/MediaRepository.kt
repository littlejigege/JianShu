package com.jimij.jianshu.data

import android.arch.persistence.room.Room
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.provider.MediaStore.Video.Thumbnails.MICRO_KIND
import android.provider.MediaStore.Video.Thumbnails.MINI_KIND
import android.util.Base64
import android.util.Log
import com.jimij.jianshu.App
import com.jimij.jianshu.db.ThumbnailDatabase
import com.jimij.jianshu.db.entity.Thumbnail
import com.mobile.utils.toBytes
import com.weechan.httpserver.httpserver.uitls.getMimeType
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

import java.io.File
import java.util.stream.Stream
import kotlin.concurrent.thread

/**
 * Created by weechan on 18-3-24.
 */

/**
 * Created by steve on 17-11-22.
 */

object MediaRepository {


    private val mContentResolver: ContentResolver = App.ctx.contentResolver
    private val thumbnailsDao = Room.databaseBuilder(App.ctx, ThumbnailDatabase::class.java,"thumbnails").build().thumbnailDao()

    var musics: List<FileInfo>? = null
    var docs: List<FileInfo>? = null
    var videos: List<FileInfo>? = null
    var pictures: List<FileInfo>? = null
    var apps: List<FileInfo>? = null

    init {
        thread {
            getMusic()
            getDocument()
            getApplications()
            getVideo()
        }
    }

    @Synchronized
    fun getMusic(): List<FileInfo> {

        if (musics != null) return musics!!

        val projection = arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.SIZE)
        val cursor = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Audio.Media.DATE_MODIFIED + " desc")


        val list = mutableListOf<FileInfo>()

        if (cursor != null && cursor.moveToFirst() && cursor.count > 0)
            do {
                var initSize = (cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))).toLong()
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val location = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                if (initSize == 0L) initSize = File(location).length()
                val info = FileInfo(name, location, initSize)
                list.add(info)
            } while (cursor.moveToNext())

        cursor.close()



        this.musics = list
        return list

    }

    @Synchronized
    fun getDocument(): List<FileInfo> {

        if (docs != null) return docs!!

        val list = mutableListOf<FileInfo>()
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.TITLE)

        val selection = (MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ")

        val selectionArgs = arrayOf("application/msword", "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.ms-excel")

        val cursor = mContentResolver.query(MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, MediaStore.Files.FileColumns.DATE_MODIFIED + " desc")



        if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
            do {
                val location = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                val initSize = java.lang.Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)))
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE))
                val info = FileInfo(name, location, initSize)
                list.add(info)
            } while (cursor.moveToNext())

            cursor.close()

        }

        this.docs = list
        return list
    }

    @Synchronized
    fun getVideo(): List<FileInfo> {

        if (videos != null) return videos!!


        val list = mutableListOf<FileInfo>()

        val cursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.TITLE,
                        MediaStore.Video.VideoColumns.SIZE, MediaStore.Video.VideoColumns._ID),
                null, null, MediaStore.Video.VideoColumns.DATE_MODIFIED + "  desc")



        if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
            do {
                val location = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA))
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE))
                val initSize = java.lang.Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE)))
                val info = FileInfo(name, location, initSize)
                list.add(info)
            } while (cursor.moveToNext())
        }
        cursor?.close()

        this.videos = list
        return list
    }

    @Synchronized
    fun getPhotosDirectory(): List<FileInfo> {

        if (pictures != null) return pictures!!

        val list = mutableListOf<FileInfo>()
        val addPath = HashSet<String>()

        val cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, MediaStore.Images.ImageColumns.DATA + "  desc")



        if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
            do {

                val location = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                val parentPath = File(location).parent
                addPath.add(parentPath)
            } while (cursor.moveToNext())
        }
        cursor.close()

        addPath.forEach {

            val name = it.substring(it.lastIndexOf('/') + 1, it.length)
            val location = it
            val folderInfo = FileInfo(name, location)
            list.add(folderInfo)
        }
        this.pictures = list
        return list

    }

    @Synchronized
    fun getPhotos(path: String?): List<FileInfo>? {
        if (path == null) return null
        val list = mutableListOf<FileInfo>()
        list.addAll(File(path).listFiles().filter { it.isFile }.map { FileInfo(it.name, it.path, it.length()) })
        return list
    }

    private fun cutBitmapToThumbnails(path : String , type : Int): Bitmap? {

        val file = File(path)
        if(!file.exists()) return null;

        if(type == 0 ) return ThumbnailUtils.createVideoThumbnail(path, MICRO_KIND)

        if(type == 1 ){
            val bitmap = BitmapFactory.decodeFile(path)
            return ThumbnailUtils.extractThumbnail(bitmap ,150 ,100)
        }

        return null
    }

    @Synchronized
    fun getApplications(): List<FileInfo>? {

        if (apps != null) return apps

        val list = mutableListOf<FileInfo>()
        val pm = App.ctx.packageManager
        var sourceDir: String

        val infos = pm.getInstalledApplications(0)

        for (i in infos) {
            sourceDir = i.sourceDir
            val initSize = File(sourceDir).length()
            val location = sourceDir
            val name = i.loadLabel(pm).toString()
            val app = FileInfo(name, location, initSize)
            list.add(app)
        }

        apps = list

        return apps
    }

    fun saveThumbnail( thumbnail : Thumbnail){
        thumbnailsDao.insert(thumbnail)
    }

    fun getThumbnail( path : String , type: Int): Thumbnail? {
        val thumbnail = thumbnailsDao.getThumbnail(path)
        if(thumbnail == null){
            val bitmap = cutBitmapToThumbnails(path,type)
            if(bitmap != null){
                val bytes = bitmap.toBytes()
                val t = Thumbnail(path, Base64.encodeToString(bytes,Base64.DEFAULT))

                async(CommonPool) { saveThumbnail(t) }

                return t
            }

        }
        return thumbnail
    }


}


