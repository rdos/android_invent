package ru.smartro.inventory

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
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
import java.io.File

class Activity : AbstractAct() , LocationListener {
    private lateinit var mLocationManager: LocationManager
    private lateinit var mMapKit: MapKit

    val db: RealmRepository by lazy {
        initRealm()
    }

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.LOCATION_HARDWARE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CAMERA
    )


    fun hasPermissions(vararg permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(this, it.toString()) == PackageManager.PERMISSION_GRANTED
        }

    fun checkPermission(permissions: Array<String>) {
        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 1)
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
            if (Snull == db.loadConfig("token")) {
                showFragment(LoginFragment.newInstance())

            } else {
                showFragment(MapFragment.newInstance())
            }
            checkPermission(PERMISSIONS)
        }
    }

    fun showHideActionBar(isHideMode: Boolean) {
        if (isHideMode) {
            supportActionBar?.hide()
        }
        else {
            supportActionBar?.show()
        }
    }

    fun setActionBarTitle(title: String) {
        showHideActionBar(false)
        supportActionBar?.title = title
    }

    fun setScreenOrientation(isLockOrientationMode: Boolean) {
        if (isLockOrientationMode) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        }

    }

    override fun onResume() {
        super.onResume()
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

    fun showNextFragment(fragment: AbstractFragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack();
        } else {
            super.onBackPressed()
        }
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

    /** Use external media if it is available, our app's file directory otherwise */
    fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else appContext.filesDir
    }


}