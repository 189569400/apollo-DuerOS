package com.baidu.carlifevehicle

import com.baidu.carlife.protobuf.CarlifeCarGpsProto
import com.baidu.carlife.protobuf.CarlifeNaviAssitantGuideInfoProto
import com.baidu.carlife.sdk.CarLifeContext
import com.baidu.carlife.sdk.CarLifeSubscribable
import com.baidu.carlife.sdk.Constants
import com.baidu.carlife.sdk.internal.protocol.CarLifeMessage
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes
import com.baidu.carlife.sdk.sender.CarLife
import com.baidu.carlife.sdk.util.TimerUtils

class CarDataGPSSubscribable(private val context: CarLifeContext): CarLifeSubscribable {
    override val id: Int = 0
    override var SupportFlag: Boolean = true

    override fun subscribe() {
        //开始给手机端发送GPS信息
        TimerUtils.schedule(subscribeGpsData, 1 * 1000, 2 * 1000)
    }

    override fun unsubscribe() {
        //停止给手机端发送GPS信息
        TimerUtils.stop(subscribeGpsData)
    }

    private val subscribeGpsData = Runnable {
        //开始给手机端发送当前汽车位置信息
        var message = CarLifeMessage.obtain(Constants.MSG_CHANNEL_CMD, ServiceTypes.MSG_CMD_CAR_GPS)
        message.payload(
            CarlifeCarGpsProto.CarlifeCarGps.newBuilder()
                .setAntennaState(1)
                .setSignalQuality(2)
                .setLatitude(20)
                .setLongitude(10)
                .setHeight(1000)
                .setSpeed(80)
                .setHeading(90)
                .setYear(2021)
                .setMonth(7)
                .setDay(2)
                .setHrs(18)
                .setMin(30)
                .setSec(1)
                .setFix(1)
                .setHdop(2)
                .setPdop(1)
                .setVdop(3)
                .setSatsUsed(2)
                .setSatsVisible(2)
                .setHorPosError(4)
                .setVertPosError(5)
                .setNorthSpeed(6)
                .setEastSpeed(7)
                .setVertSpeed(8)
                .setTimeStamp(System.currentTimeMillis())
                .build())
        context.postMessage(message)
    }
}