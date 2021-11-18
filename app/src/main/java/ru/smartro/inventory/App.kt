package ru.smartro.inventory

import android.app.Application
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yandex.mapkit.MapKitFactory
import ru.smartro.inventory.base.AbstractFragment

private val MAPKIT_API_KEY = "948e55bc-da44-452d-9629-00898d438ca9"

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
    }
}

const val AUTH_STAGE = "https://auth.stage.smartro.ru/api"
const val Snull = "rNull"
const val Inull = -111
const val Lnull = -111222333 as Long
const val Fnull = -111.01
const val Dnull = -110.1

fun AbstractFragment.toast(text: String? = "") {
    try {
        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {

    }

}


/**HELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIRE
 * HELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIRE
 * HELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIRE
 *
 *

 **/


//public <E extends RealmModel> E createObject(Class<E> clazz) {
//https://ru.wikipedia.org/wiki/TensorFlow