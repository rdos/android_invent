package ru.smartro.inventory.database

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import ru.smartro.inventory.Inull
import ru.smartro.inventory.Snull

open class ImageRealmEntity(
    @SerializedName("id")
//    @PrimaryKey
    var id: Int = Inull,
    var imageBase64: String = Snull,
    var date: String = Snull,
    var coordinates: CoordinatesRealmEntity? = CoordinatesRealmEntity(),
) : RealmObject() {

}