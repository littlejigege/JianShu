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
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.jimij.jianshu.App
import com.jimij.jianshu.R
import com.mobile.utils.usbManager
import kotlinx.android.synthetic.main.activity_usb.*
import java.net.ServerSocket
import kotlin.concurrent.thread


private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

class UsbActivity : AppCompatActivity() {

//    private val usbReceiver: BroadcastReceiver = UsbReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usb)

        button.setOnClickListener { 
            sendBroadcast(Intent("TTT"))
        }

//        val mPermissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
//        val filter = IntentFilter(ACTION_USB_PERMISSION)
//        filter.addAction("ACTION_USB_ACCESSORY_DETACHED")
//        registerReceiver(usbReceiver, filter)

        println(usbManager.accessoryList == null)

        thread{
            val socket = ServerSocket(10086)
            while(true){
                val ss = socket.accept();
                Log.e("UsbActivity", "ACCEPT")
                ss.getOutputStream().write("DFSADASDASDASDASDASDASD".toByteArray())
                ss.close()
            }
        }

    }


    class UsbReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            
            if(action == "TTT") {
                Log.e("UsbReceiver", "TTTTT")
            }
            
            if (ACTION_USB_PERMISSION == action) {

                Log.e("UsbReceiver", "ACTION_USB_PERMISSION")
                synchronized(this) {
                    val accessory = intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_ACCESSORY) as UsbAccessory
//                usbManager.requestPermission(accessory, mPermissionIntent);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(App.ctx, "Allow USB Permission", Toast.LENGTH_SHORT).show()
                        openAccessory(accessory)

                    } else {
                        Toast.makeText(App.ctx, "Deny USB Permission", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED == action) {
                Log.e("UsbReceiver", "ACTION_USB_ACCESSORY_DETACHED")
            }
        }

        private fun openAccessory(accessory: UsbAccessory){
            Log.e("UsbReceiver", accessory.description)
        }
    }


}
