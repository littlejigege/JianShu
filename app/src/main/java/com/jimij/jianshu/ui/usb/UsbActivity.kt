package com.jimij.jianshu.ui.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.jimij.jianshu.App
import com.jimij.jianshu.R
import com.jimij.jianshu.utils.getDefaultSavePath
import com.jimij.jianshu.utils.writeTo
import com.mobile.utils.smartDelete
import com.mobile.utils.usbManager
import com.weechan.httpserver.httpserver.reslover.body.BinaryInputStream
import com.weechan.httpserver.httpserver.uitls.writeTo
import kotlinx.android.synthetic.main.activity_usb.*
import java.io.*
import java.net.ServerSocket
import java.util.TreeSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread


private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

class UsbActivity : AppCompatActivity() {

//    private val usbReceiver: BroadcastReceiver = UsbReceiver()

    var time : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usb)

        thread {
            val socket = ServerSocket(10086)
            while (true) {
                val ss = socket.accept();
                thread {
                    if(time ==0L) time =  System.currentTimeMillis()
                    Log.e("UsbActivity", "ACCEPT")
                    val ins = DataInputStream(ss.getInputStream())
                    val fileName = ins.readLine()
                    val length = ins.readLine()
                    val fromTo = ins.readLine()

                    Log.e("UsbActivity", fromTo)

                    val (from, to) = fromTo.split(' ').map { it.toLong() }

//                    val output = File(getDefaultSavePath() + "/${fileName}.A").outputStream().buffered()
//
//                    Log.e("UsbActivity", "USB DONE")
//                    addAndcombine(fileName)
                    with(File(getDefaultSavePath() + "/${fileName}")) {
                        if (!exists()) createNewFile()
                        val output = RandomAccessFile(this,"rw")
                        output.seek(from)

                        val bi = BinaryInputStream(ins, (to - from + 1).toLong())
                        val BUF_SIZE = 1024 * 1024 * 4
                        val buf = ByteArray(BUF_SIZE)

                        var readLength = bi.read(buf)
                        while (readLength != -1) {
                            output.write(buf, 0, readLength)
                            readLength = bi.read(buf)
                        }
                        output.close()

                    }

                    Log.e("UsbActivity", "usb ${System.currentTimeMillis() - time}")
                    ss.getOutputStream().write("SUCCESS@!!!".toByteArray())
                    ss.close()
                }

            }
        }

        thread {
            val socket = ServerSocket(10087)
            while (true) {

                val ss = socket.accept();
                thread {
                    if(time ==0L) time =  System.currentTimeMillis()
                    Log.e("UsbActivity", "ACCEPT")
                    val ins = DataInputStream(ss.getInputStream())
                    val fileName = ins.readLine()
                    val length = ins.readLine()
                    val fromTo = ins.readLine()

                    Log.e("UsbActivity", fromTo)

                    val (from, to) = fromTo.split(' ').map { it.toLong() }

                    with(File(getDefaultSavePath() + "/${fileName}")) {
                        if (!exists()) createNewFile()
                        val output = RandomAccessFile(this,"rw")
                        output.seek(from)

                        val bi = BinaryInputStream(ins, (to - from + 1).toLong())
                        val BUF_SIZE = 1024 * 1024 * 4
                        val buf = ByteArray(BUF_SIZE)

                        var readLength = bi.read(buf)
                        while (readLength != -1) {
                            output.write(buf, 0, readLength)
                            readLength = bi.read(buf)
                        }
                        output.close()

                    }
                    Log.e("UsbActivity", "total ${System.currentTimeMillis() - time}")
                    ss.getOutputStream().write("SUCCESS@!!!".toByteArray())
                    ss.close()
                }

            }
        }


    }

}
