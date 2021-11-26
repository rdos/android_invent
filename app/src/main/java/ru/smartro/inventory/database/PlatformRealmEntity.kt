package ru.smartro.inventory.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import ru.smartro.inventory.Dnull
import ru.smartro.inventory.Inull
import ru.smartro.inventory.R
import ru.smartro.inventory.Snull

open class PlatformEntityRealm(
    @PrimaryKey
    @SerializedName("uuid")
    var uuid: String = Snull,
    @SerializedName("id")
    var id: Int = Inull,
    @SerializedName("length")
    var length: Int? = Inull,
    @SerializedName("width")
    var width: Int = Inull,
    @SerializedName("has_base")
    var has_base: Int = Inull,
    @SerializedName("has_fence")
    var has_fence: Int = Inull,

    @SerializedName("is_open")
    var is_open: Int = Inull,
    @SerializedName("address")
    var address: String? = null,
    //раскоментируй и посмотри!
    @SerializedName("type")
    var type: PlatformTypeRealm? = null,

    @SerializedName("coordinates")
    var coordinates: CoordinatesRealmEntity? = null,
    var coordinateLat: Double = Dnull,
    var coordinateLng: Double = Dnull,
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
    var imageList: RealmList<ImageRealmEntity> = RealmList(),
    var is_synchro_start: Boolean = false,
) : RealmObject() {



//    companion object {
//        fun from(responseBody: Array<Any>?, java: Class<PlatformEntityRealm>): PlatformEntityRealm {
//            val body = responseBody?: Snull
//            Log.i("ResponseO.from body", "=${body}")
//            return Gson().fromJson(body, java)
//        }
    fun getIconDrawableResId(): Int {
    val result = when (this.status_id) {
        1 ->  R.drawable.ic_map_fragment__bunker_green
        2 ->  R.drawable.ic_map_fragment__bunker_blue
        3 -> R.drawable.ic_map_fragment__bunker_red
        else -> {R.drawable.ic_map_fragment__bunker_orange}
    }
        return result
    }



    fun base64ToImage(encodedImage: String?): Bitmap {
        val decodedString: ByteArray =
            Base64.decode(encodedImage?.replace("data:image/png;base64,", ""), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
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