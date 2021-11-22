package ru.smartro.inventory

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.yandex.mapkit.MapKitFactory
import ru.smartro.inventory.base.AbstractFragment

/**HELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIRE
РАЗ * //gotoAdd = Add -> create->ПОТОМ->Save->ПОТОМ Show
 * HELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIREHELP HIRE
 **/

//public <E extends RealmModel> E createObject(clazz: Class<PlatformEntityRealm>) {
//
/** data}{base = base дЛЯ данных **/ /** интересно а **/ //а core?
                // private fun gotoAddPlatform() {
                //        val platformEntity = db().createPlatformEntity()

//kotlin: Unit Nothing
//Nothing — класс наследник любого класса в Kotlin
//все классы в Kotlin являются наследниками Any.
//Null В Kotlin null может формировать nullable-типы (произносится как «ну́ллабл»). Они обозначаются добавлением знака ? в конце типа. Например, String? — это тип String + null. То есть значение может быть строковым, а может быть null. В Java такие дополнения не нужны — там любой объект может быть null, и это приводит нас к одному из преимуществ

//https://ru.wikipedia.org/wiki/TensorFlow

private val MAPKIT_API_KEY = "948e55bc-da44-452d-9629-00898d438ca9"
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        Log.e(ErrorsE, "classs = Classs()")
    }
}

const val AUTH_STAGE = "https://auth.stage.smartro.ru/api"
const val URL_RPC_STAGE = "https://worknote-back.stage.smartro.ru/api/rpc"
const val Snull = "rNull"
const val Inull = -111
const val Lnull = -111222333 as Long
const val Fnull = -111.01
const val Dnull = -110.1
const val ErrorsE = "ErrorsE"
fun AbstractFragment.toast(text: String? = "") {
    try {
        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {

    }
}
