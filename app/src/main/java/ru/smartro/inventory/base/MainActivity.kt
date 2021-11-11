package ru.smartro.inventory.base

import android.os.Bundle
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.location.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.smartro.inventory.AppPreferences
import ru.smartro.inventory.R
import ru.smartro.inventory.ui.main.LoginFragment

class MainActivity : AbstractAct() , LocationListener {
    private lateinit var mLocationManager: LocationManager
    private val MAPKIT_API_KEY = "948e55bc-da44-452d-9629-00898d438ca9"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        /**
         * Set the api key before calling initialize on MapKitFactory.
         * It is recommended to set api key in the Application.onCreate method,
         * but here we do it in each activity to make examples isolated.
         */
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        /**
         * Initialize the library to load required native libraries.
         * It is recommended to initialize the MapKit library in the Activity.onCreate method
         * Initializing in the Application.onCreate method may lead to extra calls and increased battery use.
         */
        MapKitFactory.initialize(this)
        AppPreferences.init(this)

        // And to show what can be done with it, we move the camera to the center of Saint Petersburg.

        val mMapKit = MapKitFactory.getInstance()
        mLocationManager = mMapKit.createLocationManager()

        mLocationManager.subscribeForLocationUpdates(0.0, 0, 0.0, true, FilteringMode.OFF, this)

        val location: Location? = LocationManagerUtils.getLastKnownLocation()
        log.warn(location?.position.toString())

        if (savedInstanceState == null) {
            showFragment(LoginFragment.newInstance())
        }
    }

    fun showFragment(fragment: AbstractFragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()
    }

    override fun onDestroy() {
        mLocationManager.unsubscribe(this)
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        // Activity onStop call must be passed to both MapView and MapKit instance.
        MapKitFactory.getInstance().onStop()
    }

    override fun onLocationUpdated(p0: Location) {
        log.debug("latitude=${p0.position.latitude},latitude=${p0.position.longitude}")
//        log.debug("heading=${p0.heading}")
    }

    override fun onLocationStatusUpdated(p0: LocationStatus) {
//        TODO("Not yet implemented")
        val log: Logger = LoggerFactory.getLogger(MainActivity::class.java)
        log.warn("p0=$p0")
    }
}