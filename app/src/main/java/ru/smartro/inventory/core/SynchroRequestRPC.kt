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

class SynchroRequestRPC: AbstractO() {

    fun callExecuteRPC(platformEntity: PlatformEntityRealm) {
        log.debug("callAsyncRPC.before" )
        val synchroRequestEntity = SynchroRequestEntity()
        val ownerId = db.loadConfigInt("Owner")

        synchroRequestEntity.payload.organisation_id = ownerId
        platformEntity.convertToServData()
        val platformImageS = db.getImages(platformEntity.uuid)
        platformEntity.imageList.addAll(platformImageS)
        platformEntity.containers.forEach { container ->
            val containerImageS = db.getImages(container.uuid)
            container.imageList.addAll(containerImageS)
        }
        synchroRequestEntity.payload.data.add(platformEntity)
        val requestBody = synchroRequestEntity.toRequestBody()
        val restClient = RestClient()
        val request = restClient.newRequest(getRpcUrl()).post(requestBody).build()
        try {
            val response = restClient.newExecute(request)
            val bodyString = response.body?.string()
            log.debug("onResponse. bodyString=${bodyString}")
            if (!response.isSuccessful) {

                return
//            throw IOException("Error response $response")
            }

            val synchroRPCResponse = Gson().fromJson(bodyString, SynchroRPCResponse::class.java)
            if (synchroRPCResponse.error.isEmpty()) {
                platformEntity.afterSync(db)
                platformEntity.convertFromServData()
                log.debug("save_-onResponse. saveRealmEntity status_id=${platformEntity.status_name}")
                log.debug("save_-onResponse. saveRealmEntity status_name=${platformEntity.status_id}")
                db.saveRealmEntity(platformEntity)
                log.debug("save_-onResponse. saveRealmEntity.after")
            } else {
                log.error("onResponse synchroRPCResponse.error.isEmpty()")
            }

        } catch (e: Exception) {
            log.error("onResponse", e)
        }
    }




}