package ru.smartro.inventory

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.yandex.mapkit.MapKitFactory
import ru.smartro.inventory.base.AbstrActF
import ru.smartro.inventory.base.AbstractO
import ru.smartro.worknote.extensions.showImmersive

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



const val Snull = "rNull"
const val Inull = -111
const val Lnull = -111222333 as Long
const val Fnull = -111.01
const val Dnull = -110.1
const val ErrorsE = "ErrorsE"

fun getAUTHurl(dirPath: String): String {
    return when (BuildConfig.BUILD_TYPE) {
        "STAGE" -> URL_AUTH_STAGE + "/${dirPath}"
        "RC" -> URL_AUTH_RC + "/${dirPath}"
        "PROD" -> URL_AUTH_PROD + "/${dirPath}"
        else -> {
            Log.e("aA1", "Используй build type !!!")
             URL_AUTH_STAGE
        }
    }
}

fun AbstractO.getRpcUrl(): String {
    return when (BuildConfig.BUILD_TYPE) {
        "STAGE" -> URL_RPC_STAGE
        "RC" -> URL_RPC_RC
        "PROD" -> URL_RPC_PROD
        else -> {
            Log.e("Aa1", "Используй build type !!!")
            URL_RPC_STAGE
        }
    }
}

private const val URL_AUTH_STAGE = "https://auth.stage.smartro.ru/api"
private const val URL_AUTH_RC = "https://auth.rc.smartro.ru/api"
private const val URL_AUTH_PROD = "https://auth.smartro.ru/api"
private const val URL_RPC_STAGE = "https://worknote-back.stage.smartro.ru/api/rpc"
private const val URL_RPC_RC = "https://worknote-back.rc.smartro.ru/api/rpc"
private const val URL_RPC_PROD = "https://wn-api.smartro.ru/api/rpc"

fun AbstrActF.showErrorToast(text: String? = "") {
    try {
        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {

    }
}

fun AbstrActF.showErrorDialog(text: String? = "") {
    view?.let {
        AlertDialog.Builder(it.context, android.R.style.Theme_Material_Dialog)
        .setTitle(getString(R.string.delete_title))
        .setMessage(getString(R.string.delete_dialog))
        .setIcon(android.R.drawable.ic_dialog_alert)
//        .setPositiveButton(android.R.string.yes) { _, _ ->
//
//            // Delete current photo
//            mediaFile.delete()
//
//            // Send relevant broadcast to notify other apps of deletion
//            MediaScannerConnection.scanFile(
//                view.context, arrayOf(mediaFile.absolutePath), null, null)
//
//            // Notify our view pager
//            mediaList.removeAt(viewPager.currentItem)
//            viewPager.adapter?.notifyDataSetChanged()
//
//            // If all photos have been deleted, return to camera
//            if (mediaList.isEmpty()) {
//                this.exitFragment()
//            }
//        }
        .setNegativeButton(android.R.string.no, null)
        .create().showImmersive()
    }
}
