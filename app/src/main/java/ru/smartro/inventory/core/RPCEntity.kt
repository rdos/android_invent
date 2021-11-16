package ru.smartro.inventory.core

import android.util.Log
import com.google.gson.Gson
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import io.realm.RealmObject
import okhttp3.Response
import ru.smartro.inventory.base.AbstractEntity
import ru.smartro.inventory.database.OwnerResponse

//TOdo LiveEntity)) )vWSs{
class RPCProvider(
    val type: String,
    val position: Point
) : AbstractEntity() {
    fun getRPCEntity(): RPCEntity{
        val rpcEntity = RPCEntity(type, PayLoadEntity(position.latitude, position.longitude, 1))
        Log.w("RPCProvider", rpcEntity.toString() )
        return rpcEntity
    }
}

data class RPCEntity(
    var type: String,
    var payload: PayLoadEntity
)  : AbstractEntity() {
    companion object {
        fun from(response: Response): RPCEntity {
            return Gson().fromJson(response.body?.string(), RPCEntity::class.java)
        }
    }
}


data class PayLoadEntity (
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