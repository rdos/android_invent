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
        /**
         * Set the api key before calling initialize on MapKitFactory.
         * It is recommended to set api key in the Application.onCreate method,
         * but here we do it in each activity to make examples isolated.
         */
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
    }
}

const val AUTH_STAGE = "https://auth.stage.smartro.ru/api"
const val Snull = "rNull"
const val Inull = -111

fun AbstractFragment.toast(text: String? = "") {
    try {
        Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {

    }

}