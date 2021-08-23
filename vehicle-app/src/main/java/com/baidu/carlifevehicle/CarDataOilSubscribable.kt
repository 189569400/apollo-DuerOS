package com.baidu.carlifevehicle

import com.baidu.carlife.protobuf.CarlifeGearInfoProto
import com.baidu.carlife.protobuf.CarlifeOilProto
import com.baidu.carlife.sdk.CarLifeContext
import com.baidu.carlife.sdk.CarLifeSubscribable
import com.baidu.carlife.sdk.Constants
import com.baidu.carlife.sdk.internal.protocol.CarLifeMessage
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes
import com.baidu.carlife.sdk.util.TimerUtils

class CarDataOilSubscribable(private val context: CarLifeContext): CarLifeSubscribable {
    override val id: Int = 5
    override var SupportFlag: Boolean = false

    override fun subscribe() {
        //开始给手机端发送Oil信息
        TimerUtils.schedule(subscribeOilData, 50 * 1000, 2 * 1000)
    }

    override fun unsubscribe() {
        //停止给手机端发送Oil信息
        TimerUtils.stop(subscribeOilData)
    }

    private val subscribeOilData = Runnable {
        //开始给手机端发送当前汽车油耗信息
        var message = CarLifeMessage.obtain(Constants.MSG_CHANNEL_CMD, ServiceTypes.MSG_CMD_CAR_OIL)
        message.payload(
            CarlifeOilProto.CarlifeOil.newBuilder()
                .setLevel(1)
                .setRange(100)
                .setLowFuleWarning(true)
                .build())
        context.postMessage(message)
    }
}