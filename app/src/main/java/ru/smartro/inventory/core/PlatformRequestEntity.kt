package ru.smartro.inventory.core

import android.util.Log
import com.google.gson.Gson
import com.yandex.mapkit.geometry.Point
import okhttp3.Response
import ru.smartro.inventory.base.AbstractEntity

//TOdo LiveEntity)) )vWSs{
class RPCProvider(
    val type: String,
    val position: Point
) : AbstractEntity() {
    fun getRPCEntity(): RPCPlatformEntity{
        val rpcEntity = RPCPlatformEntity(type, PayLoadPlatform(position.latitude, position.longitude, 1))
        Log.w("RPCProvider", rpcEntity.toString() )
        return rpcEntity
    }
}

data class RPCPlatformEntity(
    var type: String,
    var payload: PayLoadPlatform
)  : AbstractEntity() {

}

data class PayLoadPlatform (
    val lat: Double,
    val lng: Double,
    val organisation_id: Int
) : AbstractEntity()



//TOdo LiveEntity)))
//class RPCProvider(
//    val type: String,
//    val position: Point
//) {
//    fun getRPCEntity(): RPCEntity{
//        return RPCEntity(type, PayLoadEntity(position.latitude, position.longitude))
//    }
//}