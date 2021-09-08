package com.baidu.carlifevehicle

import com.baidu.carlife.protobuf.CarlifeCarGpsProto
import com.baidu.carlife.protobuf.CarlifeCarSpeedProto
import com.baidu.carlife.sdk.CarLifeContext
import com.baidu.carlife.sdk.CarLifeSubscribable
import com.baidu.carlife.sdk.Constants
import com.baidu.carlife.sdk.internal.protocol.CarLifeMessage
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes
import com.baidu.carlife.sdk.util.TimerUtils

class CarDataVelocitySubscribable(private val context: CarLifeContext): CarLifeSubscribable {
    override val id: Int = 1
    override var SupportFlag: Boolean = true

    override fun subscribe() {
        //开始给手机端发送Velocity信息
        TimerUtils.schedule(subscribeVelocityData, 10 * 1000, 2 * 1000)
    }

    override fun unsubscribe() {
        //停止给手机端发送Velocity信息
        TimerUtils.stop(subscribeVelocityData)
    }

    private val subscribeVelocityData = Runnable {
        //开始给手机端发送当前汽车速度信息
        var message = CarLifeMessage.obtain(Constants.MSG_CHANNEL_CMD, ServiceTypes.MSG_CMD_CAR_VELOCITY)
        message.payload(
            CarlifeCarSpeedProto.CarlifeCarSpeed.newBuilder()
                .setSpeed(80)
                .setTimeStamp(System.currentTimeMillis())
                .build())
        context.postMessage(message)
    }
}