package ru.smartro.inventory

import android.content.Context
import android.content.SharedPreferences
import com.yandex.mapkit.geometry.Point


object AppPreferences {
    private const val NAME = "AppPreferences"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var mPreferences: SharedPreferences

    fun init(context: Context) {
        mPreferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var accessToken: String?
        get() = mPreferences.getString("accessToken", "rNull")
        set(value) = mPreferences.edit {
            it.putString("accessToken", value)
        }

//    var currentCoordinate: String
//        get() = mPreferences.getString("currentCoordinate", " ")!!
//        set(value) = mPreferences.edit {
//            it.putString("currentCoordinate", value)
//        }

    var userLogin: String
        get() = mPreferences.getString("userLogin", "rNull")!!
        set(value) = mPreferences.edit {
            it.putString("userLogin", value)
        }


//    fun clear() {
//        isLogined = false
//        accessToken = ""
//        vehicleId = 0
//        organisationId = 0
//        wayBillId = 0
//        //TODO: rNull!!
//        wayBillNumber = "rNull"
//        wayTaskId = 0
//        isHasTask = false
//        workerStatus = false
//    }

//    fun getCurrentLocation(): Point {
//        val lat = currentCoordinate.substringBefore("#").toDouble()
//        val long = currentCoordinate.substringAfter("#").toDouble()
//        return Point(lat, long)
//    }

}