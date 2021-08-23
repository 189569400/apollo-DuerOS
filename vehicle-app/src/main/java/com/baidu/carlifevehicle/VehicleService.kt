package com.baidu.carlifevehicle

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.baidu.carlife.sdk.CarLifeContext
import com.baidu.carlife.sdk.receiver.CarLife
import com.baidu.carlife.sdk.internal.transport.TransportListener
import com.baidu.carlife.sdk.util.Logger

class VehicleService : Service(), TransportListener {
    override fun onBind(intent: Intent?): IBinder? {
        Logger.d("mtg_carlife", "VehicleService onBind")
        return VehicleBind()
    }

    override fun onCreate() {
        super.onCreate()
        CarLife.receiver().registerTransportListener(this)
        Logger.d("mtg_carlife", "VehicleService onCreate")
    }

    override fun onConnectionEstablished(context: CarLifeContext) {
        super.onConnectionEstablished(context)

        Logger.d("VideoRender", "onConnectionEstablished start CarlifeActivity")
        // showFront();
    }

    fun showFront() {
        val mAm = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskList = mAm.getRunningTasks(100)

        for (rti in taskList) {
            Logger.d("VideoRender", "topActivity:", rti.topActivity!!.className)
            // 找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity!!.className.contains("CarlifeActivity")) {
                mAm.moveTaskToFront(rti.id, 0)
                Logger.d("VideoRender", "CarlifeActivity moveTaskToFront")
                return
            }
        }

        val intent = Intent(this, CarlifeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    class VehicleBind : Binder() {
        fun serviceStatus(status: Int) {

        }
    }

}