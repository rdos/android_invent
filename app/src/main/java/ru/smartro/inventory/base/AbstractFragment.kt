package ru.smartro.inventory.base

import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationManagerUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.smartro.inventory.*
import ru.smartro.inventory.RealmRepo
import java.io.File

abstract class AbstractFragment : Fragment() {
    private val TARGET_LOCATION = Point(-80.243123, 25.107800)
    var lastFragmentClazz: String? = null
    private val mActivity: Activity by lazy {
        activity as Activity
    }

    protected fun isNotCheckedData(tiet: TextInputEditText): Boolean {
        return !isCheckedData(tiet)
    }
    private fun isCheckedData(tiet: TextInputEditText): Boolean {
        if (tiet.text.isNullOrBlank()) {
            tiet.error = "Поле обязательно для заполнения"
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


    fun getCurrentTimeStamp(): Long {
//        return System.currentTimeMillis() / 1000L
        return System.currentTimeMillis()
    }


    fun getLastPoint(): Point {
        val location: Location? = LocationManagerUtils.getLastKnownLocation()
        val position = location?.position?: TARGET_LOCATION
        // TODO: 15.11.2021 !!
//        Point(54.881347, 55.44919)
        return position
    }

//    fun hasLastPoint(): Boolean {
//        val result = getLastKnownLocation() == null
//        log.debug("hasLastPoint.result=${result}")
//        return result
//    }
//
//    private fun getLastKnownLocation(): Location? {
//        log.debug("getLastPoint.before")
//        val lastKnownLocation: Location? = LocationManagerUtils.getLastKnownLocation()
//        var subtraction = Lnull
//        lastKnownLocation?.apply {
//            subtraction  = getCurrentTimeStamp() - absoluteTimestamp
//        }
//
//        var result = lastKnownLocation
//        // TODO: 23.11.2021 100?wtf!
//        if (subtraction < 100) {
//            result = null
//        }
//        log.debug("hasLastPoint.result=${result}")
//        return result
//    }
/// TODO: 18.11.2021 !!!
//    fun getLastPoint(): Point {
//        val location: Location? = LocationManagerUtils.getLastKnownLocation()
//        val position = location?.position?: TARGET_LOCATION
//        // TODO: 15.11.2021 !!
//        return Point(54.881347, 55.44919)
//    }

    protected fun showFragment(container: Int, fragment: AbstractFragment) {
        mActivity.showFragment(container, fragment)
    }

    protected fun showFragment(fragment: AbstractFragment, ) {
        mActivity.showFragment(fragment,)
    }

    protected fun showNextFragment(fragment: AbstractFragment) {
        mActivity.showNextFragment(fragment)
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


    protected fun callOnBackPressed() {
        mActivity.onBackPressed()
    }
    //todo: call?_!!!
    protected fun callOnBackPressed(isCallOnBackPressed: Boolean) {
        mActivity.onBackPressed(isCallOnBackPressed)
    }


    protected fun db(): RealmRepo {
        return mActivity.db
    }

    protected val log: Logger = LoggerFactory.getLogger("${this::class.simpleName}")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.debug("onViewCreated.before")
        setScreenOrientation(true)
        hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log.debug("onDestroyView.before")

    }

    override fun onDetach() {
        super.onDetach()
        log.debug("onDetach.before")

    }


    open fun onBackPressed() {
        log.info("onBackPressed.before")
    }

    open fun onCloseFragment(){
        log.info("onCloseFragment.before")
    }




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
