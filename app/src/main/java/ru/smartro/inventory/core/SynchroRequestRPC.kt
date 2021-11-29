package ru.smartro.inventory.core

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.realm.RealmList
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.database.CoordinatesRealmEntity
import ru.smartro.inventory.database.PlatformEntityRealm
import ru.smartro.inventory.getRpcUrl
import java.io.IOException

class SynchroRequestRPC(): AbstractO(), Callback {
    private lateinit var mPlatformUuid: String
    private val result = MutableLiveData<Boolean>()

    fun callAsyncRPC(platformEntity: PlatformEntityRealm): MutableLiveData<Boolean> {
        log.debug("callAsyncRPC.before" )
        val synchroRequestEntity = SynchroRequestEntity()
        val ownerId = db.loadConfigInt("Owner")

        synchroRequestEntity.payload.organisation_id = ownerId
        mPlatformUuid = platformEntity.uuid
        platformEntity.coordinates = CoordinatesRealmEntity(platformEntity.coordinateLat, platformEntity.coordinateLng)

        synchroRequestEntity.payload.data.add(platformEntity)
        val requestBody = synchroRequestEntity.toRequestBody()
        val restClient = RestClient()
        val request = restClient.newRequest(getRpcUrl()).post(requestBody).build()
        restClient.newCall(request, this)
        return result
    }

    override fun onFailure(call: Call, e: IOException) {
//        throw IOException("Error response ${e}")
        result.postValue(false)

    }

    override fun onResponse(call: Call, response: Response) {
        log.debug("onResponse.before")

        val bodyString = response.body?.string()
        log.debug("onResponse. responseBody=${bodyString}")
        if (!response.isSuccessful) {
            result.postValue(false)

//            throw IOException("Error response $response")
        }
        val platformEntity = db().loadPlatformEntity(mPlatformUuid)
        platformEntity.setStatusAfterSync()
        log.debug("save_-onResponse. saveRealmEntity status_id=${platformEntity.status_name}")
        log.debug("save_-onResponse. saveRealmEntity status_name=${platformEntity.status_id}")
        platformEntity.setSynchroDisable()
        db().saveRealmEntity(platformEntity)
        log.debug("save_-onResponse. saveRealmEntity.after")
        result.postValue(true)
//        result.postValue(responseO.payload)
    }
}