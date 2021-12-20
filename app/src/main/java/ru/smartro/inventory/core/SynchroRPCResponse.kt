package ru.smartro.inventory.core

import com.google.gson.annotations.SerializedName
import ru.smartro.inventory.Snull
import ru.smartro.inventory.database.PlatformEntityRealm

data class SynchroRPCResponse(
    @SerializedName("type")
    var type: String = Snull,
    @SerializedName("error")
    var error: List<String> = emptyList()


) {
//        companion object {
//                fun from(responseBody: String?, classs: Class<*>): ResponseO<T> {
//                        val body = responseBody ?: Snull
//                        Log.i("ResponseO.from body", "=${body}")
//                        return Gson().fromJson(body, classs::class.java)
//                }
//        }
}