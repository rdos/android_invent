package ru.smartro.inventory.core

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.realm.RealmList
import io.realm.RealmObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.Inull
import ru.smartro.inventory.RealmRepo
import ru.smartro.inventory.base.AbstractEntity
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.getRpcUrl
import java.io.IOException


class CatalogRequestRPC(): AbstractO(), Callback {
    private val result = MutableLiveData<String>()

    fun callAsyncRPC(ownerId: Int): MutableLiveData<String> {
        log.debug("callAsyncRPC.before" )
//  it`s errorS?      val ownerI = db.loadConfig("Owner").toInt()
        val rpcGetCatalogs = CatalogsRequestEntity(PayLoadCatalogRequest())
        rpcGetCatalogs.payload.organisation_id = ownerId

        val requestBody = rpcGetCatalogs.toRequestBody()
        val restClient = RestClient()
        val request = restClient.newRequest(getRpcUrl()).post(requestBody).build()
        restClient.newCall(request, this)
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
//            db.clearData()


            val payLoad_IdEA = responseI.payload
            mapFromServData_TO_SpinnerAData(payLoad_IdEA.container_platform_type, db)
            db.saveRealmEntity(payLoad_IdEA.__TO_SpinnerAData())
            mapFromServData_TO_SpinnerAData(payLoad_IdEA.container_type, db)
            db.saveRealmEntity(payLoad_IdEA.mapTOCardStatus_AData())
            // TODO: 14.12.2021 вариантЫ
            fromTOservSPINNER_AData(payLoad_IdEA.container_status, db)
            // TODO: 14.12.2021 поиск
            db.saveFromRealmEntityList(payLoad_IdEA.card_status)
            db.saveRealmEntity(payLoad_IdEA.mapTOCardStatus_AData())



        } catch (e: Exception) {
            bodyString?.let {
                result.postValue(it)
            }
            result.postValue(e.message)
        }



//        db.save {
//            for(container_platform_type in responseI.payload.container_platform_type) {
//                db.insert(container_platform_type)
//            }
//        }
//
    }

    /**? //entityRealmS vs entitySRealm **/
    //map ПОРА УЖЕ!!! convert TO convert FROM
    fun mapFromServData_TO_SpinnerAData(entityRealmS: RealmList<out RealmObject>, dbRea: RealmRepo) {
        dbRea.saveFromRealmEntityList(entityRealmS)
    }

    private fun fromTOservSPINNER_AData(payload: SpinnerADataRealmL, dbRea: RealmRepo) { /**, dbRea: RealmRepo===; **/
        val entityRealmSpinner= payload.containerStatusRealm_ADataO()
        dbRea.saveRealmEntity(entityRealmSpinner)
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