package ru.smartro.inventory.database

import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import ru.smartro.inventory.Inull
import ru.smartro.inventory.Snull

open class ContainerEntityRealm(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("number")
    var number: String = Snull,
    @SerializedName("container_status_id")
    var container_status_id: Int = Inull,
    @SerializedName("container_status_name")
    var container_status_name: String = Snull,
    @SerializedName("has_pedal")
    var has_pedal: Int = Inull,
    @SerializedName("type")
    var type: ContainerTypeRealm? = null,
    @SerializedName("comment")
    var comment: String? = null,
    var imageBase64Entity: RealmList<ImageRealmEntity> = RealmList(),
) : RealmObject()


open class ContainerTypeRealm(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("name")
    var name: String = Snull,
    @SerializedName("organisation_id")
    var organisation_id: Int = Inull
) : RealmObject()

open class ContainerStatusRealm(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("name")
    var name: String = Snull,
    @SerializedName("organisation_id")
    var organisation_id: Int = Inull
) : RealmObject()
