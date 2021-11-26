package ru.smartro.inventory.database

import com.google.gson.annotations.SerializedName
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import ru.smartro.inventory.Inull
import ru.smartro.inventory.Snull
import java.util.*

open class ContainerEntityRealm(
    @PrimaryKey
    @SerializedName("uuid")
    var uuid: String = Snull,
    @SerializedName("id")
    var id: Int = Inull,
    @SerializedName("number")
    var number: String? = null,
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
    var imageList: RealmList<ImageRealmEntity> = RealmList(),
) : RealmObject()


open class ContainerTypeRealm(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("name")
    var name: String = Snull
) : RealmObject()

open class ContainerStatusRealm(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("name")
    var name: String = Snull
) : RealmObject()
