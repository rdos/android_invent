package ru.smartro.inventory.database

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Response

//typealias OwnerEntityList = List<OwnerEntityRealm>


data class OwnerResponse(
    @SerializedName("data")
    val data: Data1,
    @SerializedName("success")
    val success: Boolean
) {
    companion object {
        fun from(response: Response): OwnerResponse {
            return Gson().fromJson(response.body?.string(), OwnerResponse::class.java)
        }
    }
}

data class Data1(
    @SerializedName("email")
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_blocked")
    val isBlocked: Boolean,
    @SerializedName("is_confirmed")
    val isConfirmed: Boolean,
    @SerializedName("is_trashed")
    val isTrashed: Boolean,
    @SerializedName("mobile_fact")
    val mobileFact: MobileFact,
    @SerializedName("name")
    val name: String,
    @SerializedName("organisation_ids")
    val organisationIds: List<Int>,
    @SerializedName("organisations")
    val organisationRealmEntities: List<OrganisationRealmEntity>,
    @SerializedName("role_srp_ids")
    val roleSrpIds: List<Int>,
    @SerializedName("roles")
    val roles: List<Int>
)

data class MobileFact(
    @SerializedName("can")
    val can: Boolean,
    @SerializedName("missing_permissions")
    val missingPermissions: List<Any>
)

