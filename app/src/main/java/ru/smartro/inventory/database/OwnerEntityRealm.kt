package ru.smartro.inventory.database

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.realm.RealmObject
import okhttp3.Response
import ru.smartro.inventory.Inull
import ru.smartro.inventory.Snull

typealias OwnerEntityList = List<OwnerEntityRealm>
open class OwnerEntityRealm(
    @SerializedName("hostname")
    var hostname: String = Snull,
    @SerializedName("id")
    var id: Int = Inull,
    @SerializedName("name")
    var name: String = Snull,
//    @SerializedName("region_id")
//    val regionId: Any = null,
    @SerializedName("timezone")
    var timezone: String = Snull
) : RealmObject() {
    companion object {

        fun <T> convert(arr: Array<T>): List<T> {
            val list: MutableList<T> = ArrayList()
            list.addAll(arr);
            return list
        }

        fun from(response: Response): OwnerEntityList {
            val arrayTutorialType = object : TypeToken<Array<OwnerEntityRealm>>() {}.type
            val arrayList = Gson().fromJson(response.body?.string(), arrayTutorialType::class.java)
            return convert(arrayList as Array<OwnerEntityRealm>)
        }
    }
}