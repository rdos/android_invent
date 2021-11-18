package ru.smartro.inventory.core

import com.google.gson.annotations.SerializedName
import ru.smartro.inventory.Snull
import ru.smartro.inventory.database.ContainerTypeRealm
import ru.smartro.inventory.database.PlatformEntityRealm

//data class Responsel(
data class ResponseI(
        @SerializedName("type")
        val type: String = Snull,
        @SerializedName("payload")
        val payload: PayloadCatalog,
        @SerializedName("error")
        val error: List<ResponseRPCError>

) {

//        companion object {
//                fun from(responseBody: String?, classs: Class<*>): ResponseO<T> {
//                        val body = responseBody ?: Snull
//                        Log.i("ResponseO.from body", "=${body}")
//                        return Gson().fromJson(body, classs::class.java)
//                }
//        }
}

data class PayloadCatalog(
        @SerializedName("container_type")
        val container_type: List<ContainerTypeRealm>,
        @SerializedName("container_platform_type")
        val container_platform_type : List<PlatformEntityRealm>
