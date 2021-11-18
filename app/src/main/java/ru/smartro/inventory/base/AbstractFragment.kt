package ru.smartro.inventory.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationManagerUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.smartro.inventory.Activity
import ru.smartro.inventory.database.RealmRepository
import ru.smartro.inventory.ui.main.MapFragment
import java.io.File

abstract class AbstractFragment : Fragment() {
    private val TARGET_LOCATION = Point(-80.243123, 25.107800)

    private val mActivity: Activity by lazy {
        activity as Activity
    }

    fun getLastPoint(): Point {
        val location: Location? = LocationManagerUtils.getLastKnownLocation()
        val position = location?.position?: TARGET_LOCATION
        // TODO: 15.11.2021 !!
        return Point(54.881347, 55.44919)
    }

    protected fun showFragment(fragment: AbstractFragment) {
        mActivity.showFragment(fragment)
    }

    protected fun showNextFragment(fragment: AbstractFragment) {
        mActivity.showNextFragment(fragment)
    }

    protected fun getOutputDirectory(p_id: Int): File {
        return mActivity.getOutputDirectory(p_id)
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

    protected fun exitFragment() {
        mActivity.onBackPressed()
    }

    protected fun db(): RealmRepository {
        return mActivity.db
    }

    protected val log: Logger = LoggerFactory.getLogger("${this::class.simpleName}")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.debug("onViewCreated.before")
        setScreenOrientation(false)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        log.debug("onDestroyView.before")

    }

    override fun onDetach() {
        super.onDetach()
        log.debug("onDetach.before")

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
