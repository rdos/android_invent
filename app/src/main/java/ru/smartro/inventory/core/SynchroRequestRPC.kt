package ru.smartro.inventory.core

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.smartro.inventory.base.AbstractO
import ru.smartro.inventory.base.RestClient
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

        platformEntity.beforeSync()
        db.saveRealmEntity(platformEntity)

        synchroRequestEntity.payload.organisation_id = ownerId
        mPlatformUuid = platformEntity.uuid
        platformEntity.convertToServData()
        synchroRequestEntity.payload.data.add(platformEntity)
        val requestBody = synchroRequestEntity.toRequestBody()
        val restClient = RestClient()
        val request = restClient.newRequest(getRpcUrl()).post(requestBody).build()
        restClient.newCall(request, this)
        return result
    }

    override fun onFailure(call: Call, e: IOException) {
//        throw IOException("Error response ${e}")
        log.error("onFailure", e)
        try {
        val platformEntity = db().loadPlatformEntity(mPlatformUuid)
        platformEntity.afterFailSync()
        db().saveRealmEntity(platformEntity)
        } catch (e: Exception) {

        }
        result.postValue(false)
    }

    override fun onResponse(call: Call, response: Response) {
        log.debug("onResponse.before")
        try {
            val bodyString = response.body?.string()
            log.debug("onResponse. bodyString=${bodyString}")
            if (!response.isSuccessful) {
                result.postValue(false)

                val platformEntity = db().loadPlatformEntity(mPlatformUuid)
                platformEntity.afterFailSync()
                db().saveRealmEntity(platformEntity)

                return
//            throw IOException("Error response $response")
            }

            val synchroRPCResponse = Gson().fromJson(bodyString, SynchroRPCResponse::class.java)
            if (synchroRPCResponse.error.isEmpty()) {
                val platformEntity = db().loadPlatformEntity(mPlatformUuid)
                platformEntity.afterSync(db())
                log.debug("save_-onResponse. saveRealmEntity status_id=${platformEntity.status_name}")
                log.debug("save_-onResponse. saveRealmEntity status_name=${platformEntity.status_id}")
                db().saveRealmEntity(platformEntity)
                log.debug("save_-onResponse. saveRealmEntity.after")
                result.postValue(true)
            } else {

                val platformEntity = db().loadPlatformEntity(mPlatformUuid)
                platformEntity.afterFailSync()
                db().saveRealmEntity(platformEntity)

            }

        } catch (e: Exception) {
            log.error("onResponse", e)

            val platformEntity = db().loadPlatformEntity(mPlatformUuid)
            platformEntity.afterFailSync()
            db().saveRealmEntity(platformEntity)

            result.postValue(false)
        }

//        result.postValue(responseO.payload)
    }
}