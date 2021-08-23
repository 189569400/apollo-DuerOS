package com.baidu.carlifevehicle.module

import com.baidu.carlife.sdk.CarLifeContext
import com.baidu.carlife.sdk.CarLifeModule
import com.baidu.carlife.sdk.Constants
import com.baidu.carlife.sdk.receiver.OnPhoneStateChangeListener
import com.baidu.carlife.sdk.util.Logger
import com.baidu.carlifevehicle.CarlifeActivity


import com.baidu.carlifevehicle.message.MsgHandlerCenter
import com.baidu.carlifevehicle.util.CommonParams

class PhoneModule(private val context: CarLifeContext,
                  private val activity: CarlifeActivity,
                  val callback: OnPhoneStateChangeListener)
    : CarLifeModule() {
    override val id: Int = Constants.PHONE_MODULE_ID

    override fun onModuleStateChanged(newState: Int, oldState: Int) {
        Logger.d(Constants.TAG, "newState: $newState, activity.mIsConnectException: ${activity.mIsConnectException}")
        // 电话状态车机端无需任何处理
//        when(newState) {
//            Constants.PHONE_STATUS_IDLE  -> {
//                this@PhoneModule.callback.onStateChanged(false, false)
//                if (activity.mIsConnectException) {
//                    MsgHandlerCenter.dispatchMessage(CommonParams.MSG_MAIN_DISPLAY_MAIN_FRAGMENT)
//                } else {CarlifeActivity
//                    //通话结束后直接返回到投屏界面
//                    MsgHandlerCenter.dispatchMessage(CommonParams.MSG_MAIN_DISPLAY_TOUCH_FRAGMENT)
//                }
//            }
//
//            else -> {
//                this@PhoneModule.callback.onStateChanged(true, newState == Constants.PHONE_STATUS_INCOMING)
//                MsgHandlerCenter.dispatchMessage(CommonParams.MSG_MAIN_DISPLAY_EXCEPTION_FRAGMENT)
//            }
//        }
    }
}