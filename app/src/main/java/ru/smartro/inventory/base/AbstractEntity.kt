package ru.smartro.inventory.base

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

abstract class AbstractEntity {

    fun toRequestBody(): RequestBody {
        return Gson().toJson(this).toRequestBody()
    }

//    fun AbstractEntity?.from(response: Response) :  {
//
//    }
}
