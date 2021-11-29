package ru.smartro.inventory.core

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import ru.smartro.inventory.Snull
import ru.smartro.inventory.database.*
import java.io.Serializable

//data class Responsel(
data class ResponseI(
        @SerializedName("type")
        var type: String = Snull,
        @SerializedName("payload")
        val payload: PayloadCatalog = PayloadCatalog(),
        @SerializedName("error")
        val error: RealmList<ResponseRPCError> = RealmList()

) {

//        companion object {
//                fun from(responseBody: String?, classs: Class<*>): ResponseO<T> {
//                        val body = responseBody ?: Snull
//                        Log.i("ResponseO.from body", "=${body}")
//                        return Gson().fromJson(body, classs::class.java)
//                }
//        }
}

open class PayloadCatalog(
        @SerializedName("container_type")
        var container_type: RealmList<ContainerTypeRealm> = RealmList(),
        @SerializedName("container_platform_type")
        var container_platform_type : RealmList<PlatformTypeRealm> = RealmList(),
        @SerializedName("card_status")
        var card_status : RealmList<CardStatusTypeRealm> = RealmList(),
        @SerializedName("container_status")
        var container_status : RealmList<ContainerStatusRealm> = RealmList(),
)
