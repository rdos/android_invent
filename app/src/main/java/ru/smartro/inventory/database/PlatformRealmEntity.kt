package ru.smartro.inventory.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import ru.smartro.inventory.*

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import ru.smartro.worknote.extensions.simulateClick


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
    @SerializedName("update_at")
    var update_at: String = Snull,
    @SerializedName("synchro_at")
    var synchro_at: String = Snull,
    var is_allow_synchro: Boolean = false,
) : RealmObject() {

    fun afterSync(db: RealmRepo) {
        this.is_allow_synchro = false
        this.status_id = 1
        this.status_name = "Просто статус_RtextS"
        this.synchro_at = getDeviceTime()
        //            1. сколько всего сделанных карточек
        val cntSyncConfig = db.loadConfigL("cnt_platform__sync")
        cntSyncConfig.cntPlusOne()
        db.saveConfig(cntSyncConfig)
    }

    fun afterCreate(db: RealmRepo) {
        this.is_allow_synchro = true
        this.update_at = getDeviceTime()
        val cnt = db.loadConfigL("cnt_platform__create")
        cnt.cntPlusOne()
        db.saveConfig(cnt)
    }

    fun beforeCreate() {
        this.is_allow_synchro = false
    }


    fun convertFromServData() {
        this.coordinateLat = this.coordinates?.lat!!
        this.coordinateLng = this.coordinates?.lng!!
        this.coordinates = null
    }

    fun convertToServData() {
        this.coordinates?.let {
            Log.e("ТОДО", "this.coordinates?")
        }
        this.coordinates = CoordinatesRealmEntity(this.coordinateLat, this.coordinateLng)
    }

    fun getIconDrawableResId(): Int {
    val result = when (this.status_id) {
        1 ->  R.drawable.ic_map_fragment__bunker_green
        2 ->  R.drawable.ic_map_fragment__bunker_blue
        3 -> R.drawable.ic_map_fragment__bunker_red
        else -> {R.drawable.ic_map_fragment__bunker_green}
    }
        return result
    }

    fun base64ToImage(encodedImage: String?): Bitmap {
        val decodedString: ByteArray =
            Base64.decode(encodedImage?.replace("data:image/png;base64,", ""), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    //Xiaomi.huawei
    //ToD LiveEntity
    private var tmp__cnt_enabled: Int = Inull
    fun isEnableSave(apcDep__GUI: AppCompatButton): Boolean {
    //    abstract class AbstractEntity  {
        if (tmp__cnt_enabled >= 0) {
            return true
        }

        if (this.containers.size <= 0) {
            val image: Drawable? = ResourcesCompat.getDrawable(apcDep__GUI.resources, R.drawable.ic_exclamation_mark, null)
//            apcDep__GUI.setLeftIcon(image)
            this.tmp__cnt_enabled = 0
            val dependenceButton = apcDep__GUI.getSubButton()
            dependenceButton?.setCompoundDrawablesWithIntrinsicBounds(null, null,image,null)
            dependenceButton?.simulateClick(500)
//            dependenceButton?.simulateClick()
            return false
        }
        // TODO: 14.12.2021 wt f?!;
        return true
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
) : RealmObject() {

}

open class CardStatusTypeRealm(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("name")
    var name: String = Snull,
    @SerializedName("organisation_id")
    var organisation_id: Int = Inull
) : RealmObject() {

}
