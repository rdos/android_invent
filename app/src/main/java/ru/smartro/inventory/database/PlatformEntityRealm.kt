package ru.smartro.inventory.database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
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
    var coordinates: CoordinatesRealm? = null,
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

    var imageBase64: RealmList<ImageRealm> = RealmList(),
    var orig_json: OrigJsonRealm? = null
) : RealmObject(), Serializable {



//    companion object {
//        fun from(responseBody: Array<Any>?, java: Class<PlatformEntityRealm>): PlatformEntityRealm {
//            val body = responseBody?: Snull
//            Log.i("ResponseO.from body", "=${body}")
//            return Gson().fromJson(body, java)
//        }
    fun getIconDrawableResId(): Int {
        return R.drawable.ic_map_fragment_bunker_green
//        if (this.beginnedAt.isNotNull() && this.status == StatusEnum.NEW) {
//            return R.drawable.ic_serving
//        }
//        return when (this.icon) {
//            "bunker" ->
//                when (this.status) {
//                    StatusEnum.NEW -> R.drawable.ic_bunker_blue
//                    StatusEnum.SUCCESS -> R.drawable.ic_bunker_green
//                    StatusEnum.ERROR -> R.drawable.ic_bunker_red
//                    else -> R.drawable.ic_bunker_orange
//                }
//            "bag" ->
//                when (this.status) {
//                    StatusEnum.NEW -> R.drawable.ic_bag_blue
//                    StatusEnum.SUCCESS -> R.drawable.ic_bag_green
//                    StatusEnum.ERROR -> R.drawable.ic_bag_red
//                    else -> R.drawable.ic_bag_orange
//                }
//            "bulk" ->
//                when (this.status) {
//                    StatusEnum.NEW -> R.drawable.ic_bulk_blue
//                    StatusEnum.SUCCESS -> R.drawable.ic_bulk_green
//                    StatusEnum.ERROR -> R.drawable.ic_bulk_red
//                    else -> R.drawable.ic_bulk_orange
//                }
//            "euro" ->
//                when (this.status) {
//                    StatusEnum.NEW -> R.drawable.ic_euro_blue
//                    StatusEnum.SUCCESS ->  R.drawable.ic_euro_green
//                    StatusEnum.ERROR -> R.drawable.ic_euro_red
//                    else -> R.drawable.ic_euro_orange
//                }
//            "metal" ->
//                when (this.status) {
//                    StatusEnum.NEW -> R.drawable.ic_metal_blue
//                    StatusEnum.SUCCESS -> R.drawable.ic_metal_green
//                    StatusEnum.ERROR -> R.drawable.ic_metal_red
//                    else -> R.drawable.ic_metal_orange
//                }
//            else ->
//                //many
//                when (this.status) {
//                    StatusEnum.NEW -> R.drawable.ic_many_blue
//                    StatusEnum.SUCCESS -> R.drawable.ic_many_green
//                    StatusEnum.ERROR -> R.drawable.ic_many_red
//                    else -> R.drawable.ic_many_orange
//                }
//        }
    }

    fun imageToBase64(imageUri: Uri, rotationDegrees: Float, context: Context): String {
        val imageStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val selectedImage = BitmapFactory.decodeStream(imageStream)
        val matrix = Matrix()
        matrix.preRotate(rotationDegrees)
        val rotatedBitmap = Bitmap.createBitmap(selectedImage, 0, 0, selectedImage.width, selectedImage.height, matrix, true)
        val compressedBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 320, 620, false)
        val baos = ByteArrayOutputStream()
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return "data:image/png;base64,${Base64.encodeToString(b, Base64.DEFAULT)}"
    }

    fun base64ToImage(encodedImage: String?): Bitmap {
        val decodedString: ByteArray =
            Base64.decode(encodedImage?.replace("data:image/png;base64,", ""), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}

open class OrigJsonRealm(
    var date: String? = Snull,
) : Serializable, RealmObject(
) {

}

open class ImageRealm(
    var imageBase64: String = Snull,
    var date: String = Snull,
    var coordinates: CoordinatesRealm? = null,
) : Serializable, RealmObject()

open class CoordinatesRealm(
    var lat: Double = Dnull,
    var lng: Double = Dnull
): RealmObject()


open class PlatformTypeRealm(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = Inull,
    @SerializedName("name")
    var name: String = Snull,
    @SerializedName("organisation_id")
    var organisation_id: Int = Inull
) : RealmObject()

//open class CardStatusTypeRealm(
//    @SerializedName("id")
//    @PrimaryKey
//    var id: Int = Inull,
//    @SerializedName("name")
//    var name: String = Snull,
//    @SerializedName("organisation_id")
//    var organisation_id: Int = Inull
//) : RealmObject()