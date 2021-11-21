package ru.smartro.inventory.core

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import ru.smartro.inventory.Snull
import ru.smartro.inventory.database.ARealmObject
import ru.smartro.inventory.database.ContainerTypeRealm
import ru.smartro.inventory.database.PlatformEntityRealm
import ru.smartro.inventory.database.PlatformTypeRealm
import java.io.Serializable

//data class Responsel(
data class ResponseI(
        @SerializedName("type")
        var type: String = Snull,
        @SerializedName("payload")
        val payload: PayloadCatalog,
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
        var container_platform_type : RealmList<PlatformTypeRealm> = RealmList()
) : RealmObject(), Serializable
