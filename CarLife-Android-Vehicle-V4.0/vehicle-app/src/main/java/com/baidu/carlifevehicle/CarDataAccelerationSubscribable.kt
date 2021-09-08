package com.baidu.carlifevehicle

import com.baidu.carlife.protobuf.CarlifeAccelerationProto
import com.baidu.carlife.sdk.CarLifeContext
import com.baidu.carlife.sdk.CarLifeSubscribable
import com.baidu.carlife.sdk.Constants
import com.baidu.carlife.sdk.internal.protocol.CarLifeMessage
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes
import com.baidu.carlife.sdk.util.TimerUtils

class CarDataAccelerationSubscribable(private val context: CarLifeContext): CarLifeSubscribable {
    override val id: Int = 3
    override var SupportFlag: Boolean = true

    override fun subscribe() {
        //开始给手机端发送Acceleration信息
        TimerUtils.schedule(subscribeAccelerationData, 30 * 1000, 2 * 1000)
    }

    override fun unsubscribe() {
        //停止给手机端发送Acceleration信息
        TimerUtils.stop(subscribeAccelerationData)
    }

    private val subscribeAccelerationData = Runnable {
        //开始给手机端发送当前汽车加速度信息
        var message = CarLifeMessage.obtain(Constants.MSG_CHANNEL_CMD, ServiceTypes.MSG_CMD_CAR_ACCELERATION)
        message.payload(
            CarlifeAccelerationProto.CarlifeAcceleration.newBuilder()
                .setAccX(1.0)
                .setAccY(2.0)
                .setAccZ(3.0)
                .setTimeStamp(System.currentTimeMillis())
                .build())
        context.postMessage(message)
    }
}