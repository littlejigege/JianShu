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
import com.mobile.utils.toBitmap
import com.mobile.utils.toBytes
import com.weechan.httpserver.httpserver.uitls.getMimeType
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Semaphore
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
    private val thumbnailsDao = Room.databaseBuilder(App.ctx, ThumbnailDatabase::class.java, "thumbnails").build().thumbnailDao()
    private val formatter = SimpleDateFormat.getDateInstance()

    var musics: MutableList<MFile>? = null

    var docs: MutableList<MFile>? = null
    var videos: MutableList<MFile>? = null
    var pictures: MutableList<MFile>? = null
    var apps: MutableList<AppInfo>? = null

    init {
        thread {
            getMusic()
            getDocument()
            getApplications()
            getVideo()
        }
    }

    @Synchronized
    fun getMusic(): List<MFile> {

        if (musics != null) return musics!!.filter { File(it.path).exists() }

        val projection = arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.SIZE)
        val cursor = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Audio.Media.DATE_MODIFIED + " desc")


        val list = mutableListOf<MFile>()

        if (cursor != null && cursor.moveToFirst() && cursor.count > 0)
            do {
                var initSize = (cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))).toLong()
                val location = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                if (initSize == 0L) initSize = File(location).length()
                val info = MFile(initSize, location, formatter.format(Date(File(location).lastModified())))
                if (File(location).exists()) {
                    list.add(info)
                }
            } while (cursor.moveToNext())

        cursor.close()

        this.musics = list
        return list

    }

    @Synchronized
    fun getDocument(): List<MFile> {

        if (docs != null) return docs!!.filter { File(it.path).exists() }

        val list = mutableListOf<MFile>()
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
                val info = MFile(initSize, location, formatter.format(Date(File(location).lastModified())))
                if (File(location).exists()) {
                    list.add(info)
                }
            } while (cursor.moveToNext())

            cursor.close()

        }

        this.docs = list
        return list
    }

    @Synchronized
    fun getVideo(): List<MFile> {

        if (videos != null) return videos!!.filter { File(it.path).exists() }


        val list = mutableListOf<MFile>()

        val cursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.TITLE,
                        MediaStore.Video.VideoColumns.SIZE, MediaStore.Video.VideoColumns._ID),
                null, null, MediaStore.Video.VideoColumns.DATE_MODIFIED + "  desc")



        if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
            do {
                val location = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA))
                val initSize = java.lang.Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE)))
                val info = MFile(initSize, location, formatter.format(Date(File(location).lastModified())))
                if (File(location).exists()) {
                    list.add(info)
                }
            } while (cursor.moveToNext())
        }
        cursor?.close()

        this.videos = list
        return list
    }

    @Synchronized
    fun getPhotosDirectory(): List<MFile> {

        if (pictures != null) return pictures!!.filter { File(it.path).exists() }

        val list = mutableListOf<MFile>()
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
            val location = it
            val folderInfo = MFile(0, location, isDirectory = true)
            list.add(folderInfo)
        }
        this.pictures = list
        return list

    }

    @Synchronized
    fun getPhotos(path: String?): List<MFile>? {
        if (path == null) return null
        val list = mutableListOf<MFile>()
        list.addAll(File(path).listFiles().filter { it.isFile && it.exists() }.map { MFile(it.length(), it.path, formatter.format(Date(it.lastModified()))) }.filter {
            it.path.endsWith(".jpg") ||
                    it.path.endsWith(".png") ||
                    it.path.endsWith(".jpeg") ||
                    it.path.endsWith(".gif") ||
                    it.path.endsWith(".bmp") ||
                    it.path.endsWith(".jpg") ||
                    it.path.endsWith(".webp") ||
                    it.path.endsWith(".tif") ||
                    it.path.endsWith(".tiff")
        })
        return list
    }
//
//    private fun cutBitmapToThumbnails(path: String, type: Int): Bitmap? {
//
//        val file = File(path)
//        if (!file.exists()) return null;
//
//        if (type == 0) return ThumbnailUtils.createVideoThumbnail(path, MICRO_KIND)
//
//        if (type == 1) {
//            val options = BitmapFactory.Options()
//            options.apply {
//                inSampleSize = 4
//            }
//
//            val bitmap = BitmapFactory.decodeFile(path)
//            return ThumbnailUtils.extractThumbnail(bitmap, 150, 100)
//        }
//
//        return null
//    }

    fun cutBitmapToThumbnails(url: String?, type: Int, width: Int = 150, height: Int = 100): Bitmap? {
        var bitmap: Bitmap? = null
        if (url == null || !File(url).exists()) return bitmap


        val file = File(url)
        if (!file.exists()) return null;
        if (type == 0) return ThumbnailUtils.createVideoThumbnail(url, MICRO_KIND)

        try {
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(url, opts)
            opts.inSampleSize = calculateSampleSize(opts, width, height)
            opts.inJustDecodeBounds = false
            opts.inPreferredConfig = Bitmap.Config.RGB_565
            opts.inPurgeable = true
            opts.inInputShareable = true
            bitmap = BitmapFactory.decodeStream(file.inputStream(), null, opts)
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }

    private fun calculateSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }


    @Synchronized
    fun getApplications(): List<AppInfo>? {

        if (apps != null) return apps

        val list = mutableListOf<AppInfo>()
        val pm = App.ctx.packageManager
        var sourceDir: String

        val infos = pm.getInstalledApplications(0)

        for (i in infos) {
            sourceDir = i.sourceDir
            val initSize = File(sourceDir).length()
            val location = sourceDir

            val app = AppInfo(initSize, location, formatter.format(Date(File(location).lastModified())), i.loadLabel(pm).toString())
            list.add(app)
            thumbnailsDao.insertIgnore(Thumbnail(location, Base64.encodeToString(pm.getApplicationIcon(i).toBitmap()?.toBytes(), Base64.DEFAULT)))
        }

        apps = list

        return apps
    }

    fun saveThumbnail(thumbnail: Thumbnail) {
        thumbnailsDao.insert(thumbnail)
    }

    fun deleteThumbnail(path: String) {
        thumbnailsDao.delteThumbnail(path);
    }

    @Synchronized
    fun deleteFromMemory(path: String) {

    }

    fun getThumbnail(path: String, type: Int): Thumbnail? {

        val thumbnail = thumbnailsDao.getThumbnail(path)
        if (type == 2) return thumbnail

        if (thumbnail == null) {
            var bitmap = cutBitmapToThumbnails(path, type)
            if (bitmap != null) {
                val bytes = bitmap.toBytes()
                bitmap.recycle()
                bitmap = null
                val t = Thumbnail(path, Base64.encodeToString(bytes, Base64.DEFAULT))
                async(CommonPool) { saveThumbnail(t) }
                return t
            }
        }
        return thumbnail
    }


}


