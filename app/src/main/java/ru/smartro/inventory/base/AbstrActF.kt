package ru.smartro.inventory.base

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationManagerUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.smartro.inventory.*
import ru.smartro.inventory.RealmRepo
import ru.smartro.inventory.database.PlatformEntityRealm
import ru.smartro.inventory.database.PlatformTypeRealm
import ru.smartro.inventory.ui.main.LoginFragment
import java.io.File
import kotlin.reflect.KProperty1

const val ARGUMENT_NAME__PLATFORM_UUID = "ARGUMENT_NAME__PLATFORM_UUID"
const val ARGUMENT_NAME__CONTAINER_UUID = "ARGUMENT_NAME__CONTAINER_UUID"

abstract class AbstrActF : Fragment() {
    private var mLastLocationGPS: android.location.Location? = null
    private var mLastLocationYandex: Location? = null
    protected val p_platform_uuid: String by lazy {
        getArgumentPlatformUUID()
    }
    protected val p_container_uuid: String? by lazy {
        getArgumentContainerUUID()
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as Activity).mLastShowFragment = this
    }

    fun getCurrdateTime(): String {
       return this.getDeviceTime()
    }

    //    private lateinit var mLocation: android.location.Location
    private val TARGET_LOCATION = Point(55.756025, 37.617664)

    var lastFragmentClazz: String? = null
    private val mActivity: Activity by lazy {
        activity as Activity
    }

    //reСОЗДАТЕЛЬ_реШИ_лТАК sUPER uSER
    //isEnableSave((1par,2para)

    protected fun isNotEnableSave(saveButt: AppCompatButton, entity: PlatformEntityRealm): Boolean {
        return !isEnableSave(saveButt, entity)
    }

    private fun isEnableSave(saveButt: AppCompatButton, entity: PlatformEntityRealm): Boolean {
        val resu = entity.isEnableSave(saveButt)
        Log.e("acbSave.setOnClickListener", "да будет так. r")
      return resu
    }

    protected fun isNotCheckedData(tiet: TextInputEditText, til: TextInputLayout? = null): Boolean {
        return !isCheckedData(tiet, til)
    }

    private fun isCheckedData(tiet: TextInputEditText, til: TextInputLayout?): Boolean {
        val errorText = "Поле обязательно для заполнения"
        if (tiet.text.isNullOrBlank()) {
            if (til == null) {
                tiet.error = errorText
            } else {
                til.error = errorText
            }

            return false
        }
        return true
    }

    fun hideKeyboard() {
        val imm: InputMethodManager =
            mActivity.getSystemService(android.app.Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = mActivity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getDeviceName(): String? {
        fun capitalize(s: String?): String? {
            if (s == null || s.isEmpty()) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                Character.toUpperCase(first).toString() + s.substring(1)
            }
        }

        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.lowercase().startsWith(manufacturer.lowercase())) {
            capitalize(model)
        } else {
            capitalize(manufacturer).toString() + " " + model
        }
    }

//    fun calculateDistance(
//        currentLocation: Point,
//        finishLocation: Point
//    ): Int {
//        val userLocation = android.location.Location(LocationManager.GPS_PROVIDER)
//        userLocation.latitude = currentLocation.latitude
//        userLocation.longitude = currentLocation.longitude
//
//        val checkPointLocation = android.location.Location(LocationManager.GPS_PROVIDER)
//        checkPointLocation.latitude = finishLocation.latitude
//        checkPointLocation.longitude = finishLocation.longitude
//        return userLocation.distanceTo(checkPointLocation).toInt()
//    }


    private fun isLastLocation(): Boolean {
        val isYandexLocationService = db().loadConfigBool("is_yandex_location_service")
        var lastTime: Long = 0
        if (isYandexLocationService) {
            if (mLastLocationYandex == null) {
                return false
            }
            lastTime = mLastLocationYandex!!.absoluteTimestamp

        } else {
            if (mLastLocationGPS == null) {
                return false
            }
            lastTime = mLastLocationGPS!!.time
        }

        val diff = System.currentTimeMillis() - lastTime
        log.warn("diff=${diff}")
        return diff <= 33840
    }


    protected fun isNotActualLocation(): Boolean {
        return !isLastLocation()
    }


    fun getCurrentTimeSEC(): Long {
//        return System.currentTimeMillis() / 1000L
        return System.currentTimeMillis() /1000L
    }


    fun getLastPoint(): Point {
        val isYandexLocationService = db().loadConfigBool("is_yandex_location_service")
        var postion = TARGET_LOCATION
        if (isYandexLocationService) {
            mLastLocationGPS = null
            mLastLocationYandex = LocationManagerUtils.getLastKnownLocation()
            postion = mLastLocationYandex?.position?: TARGET_LOCATION
        } else {
            mLastLocationYandex = null
            mLastLocationGPS = mActivity.getLastKnowLocation()
            mLastLocationGPS?.let {
                postion = Point(mLastLocationGPS!!.latitude, mLastLocationGPS!!.longitude)
            }
        }
        // TODO: 15.11.2021 !!
//        Point(54.881347, 55.44919)
        return postion
    }

    protected fun showFragment(fragment: AbstrActF) {
        //hel
        mActivity.showFragment(fragment)
    }

    protected fun deleteOutputDirectory(p_platform_uuid: String, p_container_uuid: String?) {
        mActivity.deleteOutputDirectory(p_platform_uuid, p_container_uuid)
    }

    fun getOutputFileCount(pPlatformUuid: String, pContainerUuid: String?): Int {
        return mActivity.getOutputFileCount(pPlatformUuid, pContainerUuid)
    }

    protected fun getOutputDirectory(p_platform_uuid: String, p_container_uuid: String?): File {
        return mActivity.getOutputDirectory(p_platform_uuid, p_container_uuid)
    }

    protected fun showHideActionBar(isHideMode: Boolean) {
        mActivity.showHideActionBar(isHideMode)
    }

    protected fun setActionBarTitle(resId: Int) {
        mActivity.setActionBarTitle(getString(resId))
    }

    protected fun setActionBarTitle(string: String) {
        mActivity.setActionBarTitle(string)
    }

    protected fun setScreenOrientation(isLockMode: Boolean) {
        log.debug("setScreenOrientation.before")
        mActivity.setScreenOrientation(isLockMode)
        log.debug("setScreenOrientation.after")
    }

    protected fun db(): RealmRepo {
        return mActivity.db
    }

    protected val log: Logger = LoggerFactory.getLogger("${this::class.simpleName}")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.debug("onViewCreated.before")
        setScreenOrientation(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log.debug("onDestroyView.before")

    }

    override fun onDetach() {
        super.onDetach()
        log.debug("onDetach.before")

    }

    protected fun logOUT() {
        log.info("logOUT")
//        db().deleteData()
        showFragment(LoginFragment.newInstance())
    }

    fun setLocationService() {
        mActivity.setLocationService()
    }

    fun addArgument(platformUuid: String, containerUuid: String?) {
        val bundle = Bundle(2)
        bundle.putString(ARGUMENT_NAME__PLATFORM_UUID, platformUuid)
        // TODO: 10.12.2021 let на всякий П???
        containerUuid?.let {
            bundle.putString(ARGUMENT_NAME__CONTAINER_UUID, containerUuid)
        }
        this.arguments = bundle
    }

    private fun getArgumentPlatformUUID(): String {
        val result = requireArguments().getString(ARGUMENT_NAME__PLATFORM_UUID, Snull)
        return result
    }

    private fun getArgumentContainerUUID(): String? {
        val result = requireArguments().getString(ARGUMENT_NAME__CONTAINER_UUID)
        return result
    }

    abstract fun onBackPressed()


//    override fun onLocationChanged(location: android.location.Location) {
//        log.warn("onLocationChanged.before")
//        mLocation = location
//    }


    //    companion object {
    //        private const val TAG = "CameraXBasic"
    //        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
    //        private const val PHOTO_EXTENSION = ".jpg"
    //        private const val RATIO_4_3_VALUE = 4.0 / 3.0
    //        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    //
    //        private fun createFile(baseFolder: File, format: String, extension: String) =
    //            File(baseFolder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension)
    //    }


}
