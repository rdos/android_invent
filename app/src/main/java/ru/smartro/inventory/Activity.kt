package ru.smartro.inventory

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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
import java.util.concurrent.TimeUnit

class Activity : AbstractAct() , LocationListener {
    private var mIsCallonBackPressed: Boolean = true
    private lateinit var mLocationManager: LocationManager
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

        mMapKit = MapKitFactory.getInstance()
        mLocationManager = mMapKit.createLocationManager()

        mLocationManager.subscribeForLocationUpdates(0.0, 500, 0.0, true, FilteringMode.OFF, this)

        val location: Location? = LocationManagerUtils.getLastKnownLocation()
        log.info("onCreate", location?.position.toString())

        // TODO: 12.11.2021 место!))

        if (savedInstanceState == null) {
            if (Snull == db.loadConfig("Owner")) {
                showFragment(LoginFragment.newInstance())
            } else {
                showFragment(MapFragment.newInstance())
            }
            checkPermission(PERMISSIONS)
        }
        startSynchronizeData()
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

    private fun initRealm(): RealmRepo {
        Realm.init(applicationContext)
//        if (!AppPreferences.isHasTask) {
//            Realm.deleteRealm(Realm.getDefaultConfiguration()!!)
//        }
        val realmConfigBuilder = RealmConfiguration.Builder()
        realmConfigBuilder.allowWritesOnUiThread(true) //!!
        realmConfigBuilder.name("INVENTORY.realm")
        realmConfigBuilder.deleteRealmIfMigrationNeeded()
        Realm.setDefaultConfiguration(realmConfigBuilder.build())
        return RealmRepo(Realm.getDefaultInstance())
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
        stopSynchronizeData()
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

}