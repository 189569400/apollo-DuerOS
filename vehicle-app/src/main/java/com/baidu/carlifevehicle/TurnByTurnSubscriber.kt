package com.baidu.carlifevehicle

import android.os.Handler
import android.os.Looper
import com.baidu.carlife.protobuf.*
import com.baidu.carlife.sdk.CarLifeContext
import com.baidu.carlife.sdk.CarLifeSubscriber
import com.baidu.carlife.sdk.Constants
import com.baidu.carlife.sdk.internal.protocol.CarLifeMessage
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes
import com.baidu.carlife.sdk.util.Logger

class TurnByTurnSubscriber(private val context: CarLifeContext): CarLifeSubscriber {
    private val TAG = "TurnByTurnSubscriber"

    override val id: Int = 0

    private var handler = Handler(Looper.getMainLooper())

    override fun process(context: CarLifeContext, info: Any) {
        var carLifeInfo = info as CarlifeSubscribeMobileCarLifeInfoProto.CarlifeSubscribeMobileCarLifeInfo

        if (carLifeInfo.supportFlag) {
            snedCarlifeDataSubscribe(true)


            handler.postDelayed(Runnable {
                snedCarlifeDataSubscribe(false)
            }, 30 * 1000)
        }
    }

    override fun onReceiveMessage(context: CarLifeContext, message: CarLifeMessage): Boolean {
        when (message.serviceType) {
            ServiceTypes.MSG_CMD_NAV_NEXT_TURN_INFO -> handleCarlifeDataSubscribeInfo(context, message)
        }

        return false
    }

    private fun snedCarlifeDataSubscribe(isStart: Boolean) {
        val infoListBuilder =
            CarlifeSubscribeMobileCarLifeInfoListProto.CarlifeSubscribeMobileCarLifeInfoList.newBuilder()
        val infoBuilder = CarlifeSubscribeMobileCarLifeInfoProto.CarlifeSubscribeMobileCarLifeInfo.newBuilder()
        infoBuilder.moduleID = id
        infoBuilder.supportFlag = isStart
        infoListBuilder.addSubscribemobileCarLifeInfo(infoBuilder.build())

        infoListBuilder.cnt = infoListBuilder.subscribemobileCarLifeInfoCount

        val response = CarLifeMessage.obtain(
            Constants.MSG_CHANNEL_CMD,
            if (isStart) ServiceTypes.MSG_CMD_CARLIFE_DATA_SUBSCRIBE_START else ServiceTypes.MSG_CMD_CARLIFE_DATA_SUBSCRIBE_STOP
        )
        response.payload(infoListBuilder.build())
        context.postMessage(response)
    }


    private fun handleCarlifeDataSubscribeInfo(context: CarLifeContext, message: CarLifeMessage) {
        var response = message.protoPayload as? CarlifeNaviNextTurnInfoProto.CarlifeNaviNextTurnInfo

        //开始处理移动设备Carlife发送过来手机导航HUD信息
        Logger.d(TAG, response?.toString())
    }
}