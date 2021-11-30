package ru.smartro.inventory

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.LocationManagerUtils
import com.yandex.mapkit.location.LocationStatus
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.smartro.inventory.base.AbstractAct
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.database.ConfigEntityRealm
import ru.smartro.inventory.ui.main.LoginFragment
import ru.smartro.inventory.ui.main.MapFragment
import java.io.File
import java.util.concurrent.TimeUnit

class Activity : AbstractAct() , LocationListener {
//    private lateinit var mYandexLocationManager: com.yandex.mapkit.location.LocationManager
    private var mLocationNETWORK: Location? = null
    private lateinit var mLocationManager: LocationManager
    private var mIsCallonBackPressed: Boolean = true
//    private lateinit var mLocationManager: LocationManager
    private lateinit var mMapKit: MapKit
    private var mLastShowFragment: AbstractFragment? = null

    val db: RealmRepo by lazy {
        //Remember to call close() on all Realm instances.
        initRealm()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
// TODO: 18.11.2021 Initializing in the Application.onCreate method may lead to extra calls and increased battery use.
        MapKitFactory.initialize(this)

        // TODO: 12.11.2021 место!))
        if (savedInstanceState == null) {
            val configEntityRealm = ConfigEntityRealm("is_allowed_inventory_get_platforms", true.toString())
            db.saveConfig(configEntityRealm)
            if (Snull == db.loadConfig("Owner")) {
                showFragment(LoginFragment.newInstance())
            } else {
                showFragment(MapFragment.newInstance())
            }
            checkPermission(PERMISSIONS)
        }

        mMapKit = MapKitFactory.getInstance()
//        mYandexLocationManager = mMapKit.createLocationManager()


        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mLocationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            10F,
            this
        ) // здесь можно указать другие более подходящие вам параметры

//        mLocationManager.requestLocationUpdates(
//            LocationManager.NETWORK_PROVIDER, 900000, 0f, MyListener());
//


//        mYandexLocationManager.subscribeForLocationUpdates(0.0, 500, 0.0, true, FilteringMode.OFF, this)

//        val location: Location? = LocationManagerUtils.getLastKnownLocation()
//        log.info("onCreate", location?.position.toString())


        startSynchronizeData()
    }

    inner class MyListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            mLocationNETWORK = location
            log.info("MyListener.onLocationChanged")
        }

    }

    private val TARGET_LOCATION = Point(-80.243123, 25.107800)

    fun getLastKnowLocation(): Point {
        val result = TARGET_LOCATION
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return result
        }
        val imHere = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//        val imHereNetwork = LocationManagerUtils.getLastKnownLocation()

        if (imHere == null) {
            return result
        }
//        if (imHere.time <= )
//        if (imHere == null) {
//            if (imHereNetwork == null) {
//                return result
//            } else {
//                return Point(imHereNetwork.position.latitude, imHereNetwork.position.longitude)
//            }
//
//        }
//        imHereNetwork?.let{
//            if (imHere.time + 20000 <= imHereNetwork.absoluteTimestamp) {
//                return Point(imHereNetwork.position.latitude, imHereNetwork.position.longitude)
//            }
//            else return Point(imHere.latitude, imHere.longitude)
//        }

        return Point(imHere.latitude, imHere.longitude)
    }

    @SuppressLint("MissingPermission")
    fun isLastLocation(): Boolean {
        val imHere = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (imHere == null) {
            return false
        }
        val diff = System.currentTimeMillis() - imHere.time
        log.warn("diff=${diff}")
        return diff <= 33840
    }

    private fun stopSynchronizeData() {
        WorkManager.getInstance(this).
        cancelUniqueWork(Synchro_Worker_Work_Name)
    }

    val Synchro_Worker_Work_Name = "SynchroWorkerWorkName"
    private fun startSynchronizeData() {
        Log.w("TAG", "SynchroWorkerWorkName.before thread_id=${Thread.currentThread().id}")
//        AppPreferences.workerStatus = true
        val workManager = PeriodicWorkRequestBuilder<SynchroWorker>(16, TimeUnit.MINUTES).build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(Synchro_Worker_Work_Name, ExistingPeriodicWorkPolicy.REPLACE, workManager)
        Log.d("TAG", "SynchroWorkerWorkName.after")
    }


    val PERMISSIONS = arrayOf(
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

    override fun onStop() {
        super.onStop()
        // Activity onStop call must be passed to both MapView and MapKit instance.
        MapKitFactory.getInstance().onStop()
    }

    private fun initRealm(): RealmRepo {
        Realm.init(applicationContext)
//        if (!AppPreferences.isHasTask) {
//            Realm.deleteRealm(Realm.getDefaultConfiguration()!!)
//        }
        Realm.setDefaultConfiguration(getRealmConfig())
        return RealmRepo(Realm.getDefaultInstance())
    }

    private fun getRealmConfig(): RealmConfiguration {
        val realmConfigBuilder = RealmConfiguration.Builder()
        realmConfigBuilder.allowWritesOnUiThread(true) //!!
        realmConfigBuilder.name("INVENTORY.realm")
        realmConfigBuilder.deleteRealmIfMigrationNeeded()
        return realmConfigBuilder.build()
    }

    fun showFragment(fragment: AbstractFragment) {
        log.debug("showFragment.before")
         showFragment(R.id.fl_activity, fragment)
    }

    fun showFragment(container: Int, fragment: AbstractFragment) {
        log.info("showFragment.before")
        mLastShowFragment?.onCloseFragment()
        fragment.lastFragmentClazz = mLastShowFragment?.javaClass?.simpleName
        mLastShowFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(container, fragment)
            .commitNow()
//TOdo            .commitNow()
    }
    fun showNextFragment(fragment: AbstractFragment) {
        fragment.lastFragmentClazz = mLastShowFragment?.javaClass?.simpleName
        mLastShowFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_activity, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }

    open fun onBackPressed(isCallOnBackPressed: Boolean) {
        mIsCallonBackPressed = isCallOnBackPressed
        onBackPressed()
    }

    override fun onBackPressed() {
        if (mIsCallonBackPressed) {
            mLastShowFragment?.onBackPressed()
        }
        mIsCallonBackPressed = true
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            mLastShowFragment?.lastFragmentClazz?.let{
                val fragment = supportFragmentManager.findFragmentByTag(mLastShowFragment?.lastFragmentClazz)
                if (fragment == null) {
                    mLastShowFragment = null
                } else {
                    mLastShowFragment = fragment as AbstractFragment
                }
            }

        } else {
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
//        mLocationManager.unsubscribe(this)
        stopSynchronizeData()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }



//    override fun onLocationUpdated(p0: Location) {
//        log.warn("onLocationUpdated")
////        log.debug("latitude=${p0.position.latitude},latitude=${p0.position.longitude}")
////        log.debug("heading=${p0.heading}")
//    }
//
//    override fun onLocationStatusUpdated(p0: LocationStatus) {
////        TODO("Not yet implemented")
////        val log: Logger = LoggerFactory.getLogger(MainActivity::class.java)
////        log.warn("p0=$p0")
//
//    }

    fun deleteOutputDirectory(platformUuid: String, containerUuid: String?) {
        try {
            val file = getOutputDirectory(platformUuid, containerUuid)
            file.deleteRecursively()
        } catch (e: Exception) {
            log.error("deleteOutputDirectory", e)
        }
    }

    fun getOutputDirectory(platformUuid: String, containerUuid: String?): File {
        var dirPath = filesDir.absolutePath
        if(containerUuid == null) {
            dirPath = dirPath + File.separator + platformUuid
        } else {
            dirPath = dirPath + File.separator + platformUuid + File.separator + containerUuid
        }

        val file = File(dirPath)
        if (!file.exists()) file.mkdirs()
        return file
    }

    fun getOutputFileCount(platformUuid: String, containerUuid: String?): Int {
        val file = getOutputDirectory(platformUuid, containerUuid)
        val files = file.listFiles()
        var result = files.size
        for(f in  files) {
            if (f.isDirectory) {
                result = result - 1
            }
        }
        return result
    }

    override fun onLocationChanged(location: Location) {
        mLastShowFragment?.onLocationUpdate()
    }

}