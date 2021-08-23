package com.baidu.carlife.sdk

import com.baidu.carlife.protobuf.*
import com.baidu.carlife.sdk.internal.protocol.CarLifeMessage
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes
import com.baidu.carlife.sdk.internal.transport.TransportListener
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes.MSG_CMD_VEHICLE_CONTROL
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes.MSG_VEHICLE_CONTROL_TYPE_GET
import com.baidu.carlife.sdk.internal.protocol.ServiceTypes.MSG_VEHICLE_CONTROL_TYPE_POST
import com.baidu.carlife.sdk.util.Logger

class VehicleControlHandler: TransportListener {
    companion object {
        const val TAG = "VehicleControlHandler"
    }
    override fun onReceiveMessage(context: CarLifeContext, message: CarLifeMessage): Boolean {
        when(message.serviceType) {
            MSG_CMD_VEHICLE_CONTROL -> handleVehicleControl(context, message)
        }

        return false
    }

    private fun handleVehicleControl(context: CarLifeContext, message: CarLifeMessage) {
        val info = message.protoPayload as?
                CarlifeVehicleControlProto.CarlifeVehicleControl

        Logger.d(TAG, "收到手机端发来的消息, type: " + info?.type
                    + ", id: " + info?.id
                    + ", areaId: " + info?.areaId
                    + ", valueType: " + info?.valueType + ", msg: " + info?.toString()
        )

        when(info?.type) {
            MSG_VEHICLE_CONTROL_TYPE_GET -> sendVehicleDataInfo(context, message)
            MSG_VEHICLE_CONTROL_TYPE_POST -> setVehicleControl(context, message)
        }
    }

    private fun setVehicleControl(context: CarLifeContext, message: CarLifeMessage) {
        Logger.d(TAG, "根据消息执行相关命令操作");
    }

    private fun sendVehicleDataInfo(context: CarLifeContext, message: CarLifeMessage) {
        Logger.d(TAG, "给手机端发送消息");
        context.postDelayed(100) {
            val message = CarLifeMessage.obtain(Constants.MSG_CHANNEL_CMD,
                ServiceTypes.MSG_CMD_VEHICLE_CONTROL_INFO, 0)

            message.payload(CarlifeVehicleDataListProto.CarlifeVehicleDataList.newBuilder()
                .setSupport(true)
                .setTokenString(TAG)
                .addVehicleData(CarlifeVehicleDataProto.CarlifeVehicleData.newBuilder()
                    .setId(100)
                    .addAreaValue(1)
                    .setValueType(2).
                    setSupport(true)
                    .build())
                .build())
            context.postMessage(message)
        }
    }
}