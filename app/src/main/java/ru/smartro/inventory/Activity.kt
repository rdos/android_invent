package ru.smartro.inventory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.location.*
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.smartro.inventory.base.AbstractAct
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.ui.main.LoginFragment
import ru.smartro.inventory.ui.main.MapFragment

class Activity : AbstractAct() , LocationListener {
    private lateinit var mLocationManager: LocationManager
    private lateinit var mMapKit: MapKit

    val db: RealmRepository by lazy {
        initRealm()
    }

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.LOCATION_HARDWARE,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    fun hasPermissions(vararg permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(this, it.toString()) == PackageManager.PERMISSION_GRANTED
        }

    fun checkPermission(vararg permissions: Array<String>) {
        if (!hasPermissions(PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
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

        // TODO: 12.11.2021 место!))
        initRealm()

        if (savedInstanceState == null) {
            actionBar?.title = "Вход в систему"
            if (Snull == db.loadConfig("token")) {
                showFragment(LoginFragment.newInstance())
            } else {
                showFragment(MapFragment.newInstance())
            }
        }
        checkPermission(PERMISSIONS)
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