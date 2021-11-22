package ru.smartro.inventory.database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import ru.smartro.inventory.Dnull
import ru.smartro.inventory.Inull
import ru.smartro.inventory.R
import ru.smartro.inventory.Snull
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.Serializable

open class PlatformEntityRealm(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("length")
    var length: Int? = Inull,
    @SerializedName("width")
    var width: Int = Inull,
    @SerializedName("containers_count")
    var containers_count: Int? = null,
    @SerializedName("has_base")
    var has_base: Int? = null,
    @SerializedName("has_fence")
    var has_fence: Int? = null,

    @SerializedName("is_open")
    var is_open: Int? = null,
    @SerializedName("address")
    var address: String? = null,
    //раскоментируй и посмотри!
    @SerializedName("type")
    var type: PlatformTypeRealm? = null,

    @SerializedName("coordinates")
    var coordinates: CoordinatesRealmEntity? = CoordinatesRealmEntity(),
    @SerializedName("datetime")
    var datetime: String? = null,
    @SerializedName("comment")
    var comment: String? = null,
    @SerializedName("status_id")
    var status_id: Int = Inull,
    @SerializedName("status_name")
    var status_name: String = Snull,
    @SerializedName("containers")
    var containers: RealmList<ContainerEntityRealm> = RealmList(),
    var imageBase64Entity: RealmList<ImageRealmEntity> = RealmList(),
    var orig_jsonEntity: OrigJsonRealmEntity? = OrigJsonRealmEntity()
) : RealmObject() {



//    companion object {
//        fun from(responseBody: Array<Any>?, java: Class<PlatformEntityRealm>): PlatformEntityRealm {
//            val body = responseBody?: Snull
//            Log.i("ResponseO.from body", "=${body}")
//            return Gson().fromJson(body, java)
//        }
    fun getIconDrawableResId(): Int {
        return R.drawable.ic_map_fragment_bunker_green
    }



    fun base64ToImage(encodedImage: String?): Bitmap {
        val decodedString: ByteArray =
            Base64.decode(encodedImage?.replace("data:image/png;base64,", ""), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}

open class OrigJsonRealmEntity(
    var date: String? = Snull,
) : RealmObject(
) {

}


open class CoordinatesRealmEntity(
    var lat: Double = Dnull,
    var lng: Double = Dnull
): RealmObject()
{
}

open class PlatformTypeRealm(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("name")
    var name: String = Snull,
    @SerializedName("organisation_id")
    var organisation_id: Int = Inull
) : RealmObject()

open class CardStatusTypeRealm(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("name")
    var name: String = Snull,
    @SerializedName("organisation_id")
    var organisation_id: Int = Inull
) : RealmObject()