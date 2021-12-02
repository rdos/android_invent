package ru.smartro.inventory.database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import ru.smartro.inventory.Inull
import ru.smartro.inventory.Snull

open class OrganisationRealmEntity(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("hostname")
    var hostname: String = Snull,
    @SerializedName("name")
    var name: String = Snull,
//    @SerializedName("region_id")
//    val regionId: Any,
    @SerializedName("timezone")
    var timezone: String = Snull
): RealmObject()