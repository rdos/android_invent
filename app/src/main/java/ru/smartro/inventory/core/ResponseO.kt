package ru.smartro.inventory.core

import com.google.gson.annotations.SerializedName
import ru.smartro.inventory.Snull
import ru.smartro.inventory.database.PlatformEntityRealm

//data class ResponseO(
data class ResponseO(
        @SerializedName("type")
        val type: String = Snull,
        @SerializedName("payload")
        val payload: List<PlatformEntityRealm>,
        @SerializedName("error")
        val error: List<ResponseRPCError>,


) {
//        companion object {
//                fun from(responseBody: String?, classs: Class<*>): ResponseO<T> {
//                        val body = responseBody ?: Snull
//                        Log.i("ResponseO.from body", "=${body}")
//                        return Gson().fromJson(body, classs::class.java)
//                }
//        }
}
