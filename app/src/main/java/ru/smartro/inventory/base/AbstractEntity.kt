package ru.smartro.inventory.base

import com.google.gson.Gson
import io.realm.RealmObject
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
//Xiaomi.huawei
//ToD LiveEntity
abstract class AbstractEntity  {
//    protected val log: Logger = LoggerFactory.getLogger("${this::class.simpleName}")

    fun log(): Logger {
        return LoggerFactory.getLogger("${this::class.simpleName}")
    }

    fun toRequestBody(): RequestBody {
        log().debug("toRequestBody.before")
        var json = Gson().toJson(this)
//        json = "{\"payload\":{\"lat\":54.881347,\"lng\":55.44919, \"organisation_id\":1},\"type\":\"inventory_mobile_get_platforms\"}"
        log().info("toRequestBody. json=${json}")
        return json.toRequestBody()
    }


//    fun AbstractEntity?.from(response: Response) :  {
//
//    }
}
