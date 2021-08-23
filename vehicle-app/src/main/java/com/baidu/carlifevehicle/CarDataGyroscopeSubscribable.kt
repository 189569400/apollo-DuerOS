package com.baidu.carlifevehicle

import com.baidu.carlife.protobuf.CarlifeGyroscopeProto
import com.baidu.carlife.sdk.CarLifeContext
import com.baidu.carlife.sdk.CarLifeSubscribable
import com.baidu.carlife.sdk.Constants
import com.baidu.carlife.sdk.internal.protocol.CarLifeMessage
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes
import com.baidu.carlife.sdk.util.TimerUtils

class CarDataGyroscopeSubscribable(private val context: CarLifeContext): CarLifeSubscribable {
    override val id: Int = 2
    override var SupportFlag: Boolean = true

    override fun subscribe() {
        //开始给手机端发送Gyroscope信息
        TimerUtils.schedule(subscribeGyroscopeData, 20 * 1000, 2 * 1000)
    }

    override fun unsubscribe() {
        //停止给手机端发送Gyroscope信息
        TimerUtils.stop(subscribeGyroscopeData)
    }

    private val subscribeGyroscopeData = Runnable {
        //开始给手机端发送当前汽车陀螺仪信息
        var message = CarLifeMessage.obtain(Constants.MSG_CHANNEL_CMD, ServiceTypes.MSG_CMD_CAR_GYROSCOPE)
        message.payload(
            CarlifeGyroscopeProto.CarlifeGyroscope.newBuilder()
                .setGyroType(1)
                .setGyroX(1.0)
                .setGyroy(2.0)
                .setGyroZ(3.0)
                .setTimeStamp(System.currentTimeMillis())
                .build())
        context.postMessage(message)
    }
}