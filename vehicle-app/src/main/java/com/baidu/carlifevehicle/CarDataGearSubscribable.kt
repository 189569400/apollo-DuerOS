package com.baidu.carlifevehicle

import com.baidu.carlife.protobuf.CarlifeGearInfoProto
import com.baidu.carlife.sdk.CarLifeContext
import com.baidu.carlife.sdk.CarLifeSubscribable
import com.baidu.carlife.sdk.Constants
import com.baidu.carlife.sdk.internal.protocol.CarLifeMessage
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes
import com.baidu.carlife.sdk.util.TimerUtils

class CarDataGearSubscribable(private val context: CarLifeContext): CarLifeSubscribable {
    override val id: Int = 4
    override var SupportFlag: Boolean = true

    override fun subscribe() {
        //开始给手机端发送Gear信息
        TimerUtils.schedule(subscribeGearData, 40 * 1000, 2 * 1000)
    }

    override fun unsubscribe() {
        //停止给手机端发送Gear信息
        TimerUtils.stop(subscribeGearData)
    }

    private val subscribeGearData = Runnable {
        //开始给手机端发送当前汽车档位信息，通过档位信息标示倒车信号
        var message = CarLifeMessage.obtain(Constants.MSG_CHANNEL_CMD, ServiceTypes.MSG_CMD_CAR_GEAR)
        message.payload(
            CarlifeGearInfoProto.CarlifeGearInfo.newBuilder()
                .setGear(ServiceTypes.GEAR_REVERSE)
                .build())
        context.postMessage(message)
    }
}