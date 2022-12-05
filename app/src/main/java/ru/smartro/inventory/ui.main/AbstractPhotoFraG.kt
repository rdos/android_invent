package ru.smartro.inventory.ui.main

import android.R.attr.scaleHeight
import android.R.attr.scaleWidth
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.window.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstrActF
import ru.smartro.inventory.showErrorToast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/** Helper type alias used for analysis use case callbacks */
typealias LumaListener = (luma: Double) -> Unit


const val ANIMATION_FAST_MILLIS = 50L
const val ANIMATION_SLOW_MILLIS = 100L


/**
 * Main fragment for this app. Implements all camera operations including:
 * - Viewfinder
 * - Photo taking
 * - Image analysis
:()*/
abstract class AbstractPhotoFraG : AbstrActF() {

    companion object {

        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension)
    }


    private var mAcibShow: AppCompatImageButton? = null
    private var mCameraUiFragment: View? = null
    private lateinit var mPreviewView: PreviewView
    protected lateinit var outputDirectory: File
    private lateinit var broadcastManager: LocalBroadcastManager

    private var displayId: Int = -1
    //    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var windowManager: WindowManager

//    private val displayManager by lazy {
//        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
//    }

    //    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    /**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
//    private val displayListener = object : DisplayManager.DisplayListener {
//        override fun onDisplayAdded(displayId: Int) = Unit
//        override fun onDisplayRemoved(displayId: Int) = Unit
//        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
//            if (displayId == this@CameraAbstractFragment.displayId) {
//                Log.d(TAG, "Rotation changed: ${view.display.rotation}")
//                imageCapture?.targetRotation = view.display.rotation
//            }
//        } ?: Unit
//    }

    protected fun imageToBase64(imageUri: Uri): String {
        val imageStream: InputStream? = requireContext().contentResolver.openInputStream(imageUri)
        val baos = ByteArrayOutputStream()
        imageStream.use {
            val resource = BitmapFactory.decodeStream(imageStream)
            val scaledBitmap = Bitmap.createScaledBitmap(resource, 1024, 576, true)
            scaledBitmap.compress(Bitmap.CompressFormat.WEBP, 90, baos)
            resource.recycle()
            scaledBitmap.recycle()
        }
        val b: ByteArray = baos.toByteArray()
        val imageBase64 = "data:image/png;base64,${Base64.encodeToString(b, Base64.DEFAULT)}"

        return imageBase64
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Shut down our background executor
        cameraExecutor.shutdown()
//        displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.photo_fragment, container, false)
        return view
    }


    private fun setGalleryThumbnail(uri: Uri) {
        // Run the operations in the view's thread
        mAcibShow?.let { photoViewButton ->
            photoViewButton.post {
                // Remove thumbnail padding
                try {
                    photoViewButton.setPadding(resources.getDimension(R.dimen.stroke_small).toInt())

                    // Load thumbnail into circular button using Glide
                    Glide.with(photoViewButton)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(photoViewButton)
                } catch (e: Exception) {
                    log.error("setGalleryThumbnail", e)
                }
            }
        }
    }

    inline fun View.setPadding(@Px size: Int) {
        setPadding(size, size, size, size)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideActionBar(true)
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        broadcastManager = LocalBroadcastManager.getInstance(view.context)

        // Every time the orientation of device changes, update rotation for use cases
//        displayManager.registerDisplayListener(displayListener, null)

        //Initialize WindowManager to retrieve display metrics
        windowManager = WindowManager(view.context)

        // Determine the output directory
        outputDirectory= getOutputDirectory(p_platform_uuid, p_container_uuid)
        mPreviewView = view.findViewById(R.id.pv_camera_fragment)

        // Wait for the views to be properly laid out
        mPreviewView.post {

            // Keep track of the display in which this view is attached
            displayId = mPreviewView.display.displayId

            // Build UI controls
            updateCameraUi()

            // Set up the camera and its use cases
            setUpCamera()
        }
    }

    /**
     * Inflate camera controls and update the UI manually upon config changes to avoid removing
     * and re-adding the view finder from the view hierarchy; this provides a seamless rotation
     * transition on devices that support it.
     *
     * NOTE: The flag is supported starting in Android 8 but there still is a small flash on the
     * screen for devices that run Android 9 or below.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Rebind the camera with the updated display metrics
        bindCameraUseCases()

        // Enable or disable switching between cameras
        updateCameraSwitchButton()
    }

    /** Initialize CameraX, and prepare to bind the camera use cases  */
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
//            lensFacing = when {
//                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
//                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
//                else -> throw IllegalStateException("Back and front camera are unavailable")
//            }

            // Enable or disable switching between cameras
            updateCameraSwitchButton()

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /** Declare and bind preview, capture and analysis use cases */
    private fun bindCameraUseCases() {

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = windowManager.getCurrentWindowMetrics().bounds


        Log.d(TAG, "Screen metrics: ${metrics.width()} x ${metrics.height()}")

        val screenAspectRatio = AspectRatio.RATIO_16_9
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = mPreviewView.display.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview
        val preview = Preview.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            // Set initial target rotation
            .setTargetRotation(rotation)
            .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case

            .setTargetRotation(rotation)
            .build()

        val imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setTargetRotation(rotation)
            .build()

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer)

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(mPreviewView.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    /**
     *  [androidx.camera.core.ImageAnalysis.Builder] requires enum value of
     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
//    private fun aspectRatio(width: Int, height: Int): Int {
//        val previewRatio = max(width, height).toDouble() / min(width, height)
//        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
//            return AspectRatio.RATIO_4_3
//        }
//        return AspectRatio.RATIO_16_9
//    }


    /** Method used to re-draw the camera UI controls, called every time configuration changes. */
    private fun updateCameraUi() {
        val EXTENSION_WHITELIST = arrayOf("JPG")
        // Remove previous UI if any

        mCameraUiFragment?.let {
            (view as ViewGroup).removeView(it)
        }

        mCameraUiFragment = LayoutInflater.from(requireContext()).inflate(R.layout.camera_fragment_ui, view as ViewGroup)

        // In the background, load latest photo taken (if any) for gallery thumbnail
        lifecycleScope.launch(Dispatchers.IO) {
            outputDirectory.listFiles { file ->
                EXTENSION_WHITELIST.contains(file.extension.uppercase(Locale.ROOT))
            }?.maxOrNull()?.let {
                setGalleryThumbnail(Uri.fromFile(it))
            }
        }

        mAcibShow = mCameraUiFragment?.findViewById(R.id.acib_camera_fragment_ui__show_photo)
        mAcibShow?.setOnClickListener {
            if (getOutputFileCount(p_platform_uuid, p_container_uuid) <= 0) {
                return@setOnClickListener
            }
            showNextFragment(PhotoShowFragment.newInstance(p_platform_uuid, p_container_uuid))
        }

        val acibNext = mCameraUiFragment?.findViewById<AppCompatImageButton>(R.id.acib_camera_fragment_ui__next)
        acibNext?.setOnClickListener {
            if (getOutputFileCount(p_platform_uuid, p_container_uuid) <= 0) {
                showErrorToast("Минимальное кол-во фотографий = 1")
                return@setOnClickListener
            }
            onNextClick()
        }

        val actvCount = mCameraUiFragment?.findViewById<AppCompatTextView>(R.id.actv_camera_fragment_ui__count)
        val fileCount = getOutputFileCount(p_platform_uuid, p_container_uuid)
        if (fileCount > 0) {
            actvCount?.text = fileCount.toString()
        }
        // Listener for button used to capture photo
        val acibMakePhoto = mCameraUiFragment?.findViewById<AppCompatImageButton>(R.id.acib_camera_fragment_ui__make_photo)
        acibMakePhoto?.setOnClickListener {
            if (getOutputFileCount(p_platform_uuid, p_container_uuid) >= 3) {
                showErrorToast("Максимальное кол-во фотографий = 3")
                return@setOnClickListener
            }
            acibNext?.isEnabled = false
            mAcibShow?.isEnabled = false
            // Get a stable reference of the modifiable image capture use case
            imageCapture?.let { imageCapture ->

                // Create output file to hold the image
                val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)

                // Create output options object which contains file + metadata
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
//                        .setMetadata(metadata)
                    .build()

                // Setup image capture listener which is triggered after photo has been taken
                imageCapture.takePicture(
                    outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                            Log.d(TAG, "Photo capture succeeded: $savedUri")

                            // We can only change the foreground Drawable using API level 23+ API
                            // Update the gallery thumbnail with latest picture taken
                            setGalleryThumbnail(savedUri)

                            // If the folder selected is an external media directory, this is
                            // unnecessary but otherwise other apps will not be able to access our
                            // images unless we scan them using [MediaScannerConnection]
                            val mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(savedUri.toFile().extension)
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(savedUri.toFile().absolutePath),
                                arrayOf(mimeType)
                            ) { _, uri ->
                                Log.d(TAG, "Image capture scanned into media store: $uri")
                            }

                            val handler = Handler(Looper.getMainLooper())
                            handler.post{
                                acibNext?.isEnabled = true
                                mAcibShow?.isEnabled = true
                                actvCount?.text = getOutputFileCount(p_platform_uuid, p_container_uuid).toString()
                            }
                        }
                    })

                // Display flash animation to indicate that photo was captured
                (view as ViewGroup).postDelayed({
                    (view as ViewGroup).foreground = ColorDrawable(Color.WHITE)
                    (view as ViewGroup).postDelayed(
                        { (view as ViewGroup).foreground = null }, ANIMATION_FAST_MILLIS)
                }, ANIMATION_SLOW_MILLIS)
            }
        }


    }

    abstract fun onNextClick()

    /** Enabled or disabled a button to switch cameras depending on the available cameras */
    private fun updateCameraSwitchButton() {
//        try {
//            cameraUiContainerBinding?.cameraSwitchButton?.isEnabled = hasBackCamera() && hasFrontCamera()
//        } catch (exception: CameraInfoUnavailableException) {
//            cameraUiContainerBinding?.cameraSwitchButton?.isEnabled = false
//        }
    }

    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    /** Returns true if the device has an available front camera. False otherwise */
    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }



    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
//        if (!PermissionsFragment.hasPermissions(requireContext())) {
//            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
//                    CameraFragmentDirections.actionCameraToPermissions()
//            )
//        }
    }



}
