package com.baidu.carlifevehicle

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.baidu.carlife.sdk.Configs.*
import com.baidu.carlife.sdk.Constants
import com.baidu.carlife.sdk.receiver.CarLife
import com.baidu.carlife.sdk.sender.display.DisplaySpec
import com.baidu.carlife.sdk.internal.audio.recorder.VoiceManager
import com.baidu.carlifevehicle.util.CarlifeConfUtil

class VehicleApplication : Application() {

    var vehicleBind: VehicleService.VehicleBind? = null

    override fun onCreate() {
        super.onCreate()
        CarlifeConfUtil.getInstance().init()
        initReceiver()
        bindVehicleService()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initReceiver() {
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val displaySpec = DisplaySpec(
            this,
            screenWidth.coerceAtLeast(screenHeight).coerceAtMost(1920),
            screenWidth.coerceAtMost(screenHeight).coerceAtMost(1080),
            30
        )

        val features = mapOf(
            FEATURE_CONFIG_USB_MTU to 16 * 1024,
            FEATURE_CONFIG_FOCUS_UI to 1,
            FEATURE_CONFIG_I_FRAME_INTERVAL to 300
//            FEATURE_CONFIG_VOICE_MIC to 1,
//            FEATURE_CONFIG_VOICE_WAKEUP to 1
        )

        val configs = mapOf(
            CONFIG_LOG_LEVEL to Log.DEBUG,
            CONFIG_SAVE_AUDIO_FILE to true,
            CONFIG_WIFI_DIRECT_NAME to "MI8",//车机端wifi直连的名称
            CONFIG_TARGET_BLUETOOTH_NAME to "1234MIX",//手机端蓝牙名称
            CONFIG_CONTENT_ENCRYPTION to true,
            CONFIG_USE_ASYNC_USB_MODE to false,
//            CONFIG_PROTOCOL_VERSION to 4,
            CONFIG_PROTOCOL_VERSION to 3,
            CONFIG_HU_BT_NAME to "hu_name",
            CONFIG_HU_BT_MAC to "xxx-xxx-xxx"
        )

        Log.d(Constants.TAG, "VehicleApplication initReceiver $screenWidth, $screenHeight $displaySpec")
        CarLife.init(
            this,
            "20029999",
            "12345678",
            features,
            CarlifeActivity::class.java,
            configs)
        VoiceManager.init(CarLife.receiver())
        CarLife.receiver().setDisplaySpec(displaySpec)

//        CarLife.receiver().addSubscriber(AssistantGuideSubscriber(CarLife.receiver()))
//        CarLife.receiver().addSubscriber(TurnByTurnSubscriber(CarLife.receiver()))
//        CarLife.receiver().addSubscribable(CarDataGPSSubscribable(CarLife.receiver()))
//        CarLife.receiver().addSubscribable(CarDataVelocitySubscribable(CarLife.receiver()))
//        CarLife.receiver().addSubscribable(CarDataGyroscopeSubscribable(CarLife.receiver()))
//        CarLife.receiver().addSubscribable(CarDataAccelerationSubscribable(CarLife.receiver()))
//        CarLife.receiver().addSubscribable(CarDataGearSubscribable(CarLife.receiver()))
//        CarLife.receiver().addSubscribable(CarDataOilSubscribable(CarLife.receiver()))
    }

    val vehicleConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            vehicleBind = service as VehicleService.VehicleBind
        }
    }

    fun bindVehicleService() {
        val intent = Intent(this, VehicleService::class.java)
        bindService(intent, vehicleConnection, Context.BIND_AUTO_CREATE)
    }

}