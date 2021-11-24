package ru.smartro.inventory.core

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.Inull
import ru.smartro.inventory.base.AbstractEntity
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.database.PlatformEntityRealm
import java.io.IOException


class CatalogRequestRPC(val p_RestClient: RestClient, val p_context: Context?): AbstractO(), Callback {
    private val result = MutableLiveData<String>()

    fun callAsyncRPC(): MutableLiveData<String> {
        log.debug("callAsyncRPC.before" )
        val ownerId = db.loadConfig("Owner")
        val rpcGetCatalogs = CatalogsRequestEntity(PayLoadCatalogRequest())
        rpcGetCatalogs.payload.organisation_id = ownerId.toInt()

        val requestBody = rpcGetCatalogs.toRequestBody()
        val url = "https://worknote-back.stage.smartro.ru/api/rpc"
        val request = p_RestClient.newRequest(url).post(requestBody).build()
        p_RestClient.newCall(request, this)
        return result
    }


    override fun onFailure(call: Call, e: IOException) {
//        throw IOException("Error response ${e}")
//        result.postValue()
    }

    override fun onResponse(call: Call, response: Response) {
        log.debug("onResponse.before")

        val bodyString = response.body?.string()
        log.debug("onResponse. responseBody=${bodyString}")
        if (!response.isSuccessful) {
            // TODO: 24.11.2021
//            throw IOException("Error response $response")
        }
        var responseI = ResponseI()
        try {
            responseI = Gson().fromJson(bodyString, ResponseI::class.java)
            log.info("onResponse responseO=${responseI}")
            db.saveRealmEntityList(responseI.payload.container_platform_type)
            db.saveRealmEntityList(responseI.payload.container_type)
            db.saveRealmEntityList(responseI.payload.card_status)
            db.saveRealmEntityList(responseI.payload.container_status)
        } catch (e: Exception) {
            bodyString?.let {
                result.postValue(bodyString!!)
            }

        }


//        db.save {
//            for(container_platform_type in responseI.payload.container_platform_type) {
//                db.insert(container_platform_type)
//            }
//        }
//
    }

    data class CatalogsRequestEntity(
        var payload: PayLoadCatalogRequest,
        var type: String = "get_catalogs",
    )  : AbstractEntity() {

    }

    data class PayLoadCatalogRequest (
        var organisation_id: Int = Inull,
        val catalogs: MutableList<String> = mutableListOf("container_type", "container_platform_type", "card_status", "container_status")
    ) : AbstractEntity()


//inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

}