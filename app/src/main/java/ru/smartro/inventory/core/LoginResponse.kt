package ru.smartro.inventory.database

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Response
import ru.smartro.inventory.base.AbstractEntity

data class LoginResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("success")
    val success: Boolean
) : AbstractEntity() {

    companion object {
        fun from(response: Response): LoginResponse {
            return Gson().fromJson(response.body?.string(), LoginResponse::class.java)
        }
    }
}

data class Data(
    @SerializedName("token")
    val token: String
)

