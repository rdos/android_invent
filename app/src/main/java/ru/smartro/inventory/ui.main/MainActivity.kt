package ru.smartro.inventory.ui.main

import android.os.Bundle
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.location.*
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstractAct
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.RealmRepository

class MainActivity : AbstractAct() , LocationListener {
    private lateinit var mLocationManager: LocationManager
    private lateinit var mMapKit: MapKit

    val realmDB: RealmRepository by lazy {
        initRealm()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        /**
         * Initialize the library to load required native libraries.
         * It is recommended to initialize the MapKit library in the Activity.onCreate method
         * Initializing in the Application.onCreate method may lead to extra calls and increased battery use.
         */
        MapKitFactory.initialize(this)
//        AppPreferences.init(this)

        // And to show what can be done with it, we move the camera to the center of Saint Petersburg.

        mMapKit = MapKitFactory.getInstance()
        mLocationManager = mMapKit.createLocationManager()

        mLocationManager.subscribeForLocationUpdates(0.0, 0, 0.0, true, FilteringMode.OFF, this)

        val location: Location? = LocationManagerUtils.getLastKnownLocation()
        log.info("onCreate", location?.position.toString())

        if (savedInstanceState == null) {
            actionBar?.title = "Вход в систему"
            showFragment(LoginFragment.newInstance())
        }
        // TODO: 12.11.2021 место!))
        initRealm()

    }

    private fun initRealm(): RealmRepository {
        Realm.init(applicationContext)
//        if (!AppPreferences.isHasTask) {
//            Realm.deleteRealm(Realm.getDefaultConfiguration()!!)
//        }
        val realmConfigBuilder = RealmConfiguration.Builder()
//        config.allowWritesOnUiThread(true)
        realmConfigBuilder.name("INVENTORY.realm")
        realmConfigBuilder.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(realmConfigBuilder.build())
        return RealmRepository(Realm.getDefaultInstance())
    }

    fun showFragment(fragment: AbstractFragment) {
        log.info("AaA")
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()
//TOdo            .commitNow()
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
//        log.debug("latitude=${p0.position.latitude},latitude=${p0.position.longitude}")
//        log.debug("heading=${p0.heading}")
    }

    override fun onLocationStatusUpdated(p0: LocationStatus) {
//        TODO("Not yet implemented")
//        val log: Logger = LoggerFactory.getLogger(MainActivity::class.java)
//        log.warn("p0=$p0")
    }
}