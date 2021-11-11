package ru.smartro.inventory.base

import androidx.fragment.app.Fragment
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractFragment : Fragment() {

    private val mainActivity: MainActivity  by lazy {
        activity as MainActivity
    }
    protected fun showFragment(fragment: AbstractFragment) {
        mainActivity.showFragment(fragment)
    }

    protected val log: Logger = LoggerFactory.getLogger("${this::class.simpleName}")

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
