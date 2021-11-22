package ru.smartro.inventory.core

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.database.PlatformEntityRealm
import java.io.IOException


class CatalogRequestRPC(val p_RestClient: RestClient): AbstractO(), Callback {
    private val result = MutableLiveData<List<PlatformEntityRealm>>()

    fun callAsyncRPC(catalogEntity: CatalogsRequestEntity): MutableLiveData<List<PlatformEntityRealm>> {
        log.debug("callAsyncRPC.before" )
        val requestBody = catalogEntity.toRequestBody()
        val url = "https://worknote-back.stage.smartro.ru/api/rpc"
        val request = p_RestClient.newRequest(url).post(requestBody).build()
        p_RestClient.newCall(request, this)
        return result
    }

    override fun onFailure(call: Call, e: IOException) {
//        throw IOException("Error response ${e}")
    }

    override fun onResponse(call: Call, response: Response) {
        log.debug("onResponse.before")

        val bodyString = response.body?.string()
        log.debug("onResponse. responseBody=${bodyString}")
        if (!response.isSuccessful) {
            throw IOException("Error response $response")
        }
        val responseI = Gson().fromJson(bodyString, ResponseI::class.java)
        log.info("onResponse responseO=${responseI}")
        db.saveRealmEntityList(responseI.payload.container_platform_type)
        db.saveRealmEntityList(responseI.payload.container_type)
        db.saveRealmEntityList(responseI.payload.card_status)
        db.saveRealmEntityList(responseI.payload.container_status)
//        db.save {
//            for(container_platform_type in responseI.payload.container_platform_type) {
//                db.insert(container_platform_type)
//            }
//        }
//        result.postValue(responseO.payload)
    }


//inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

}