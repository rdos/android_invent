package ru.smartro.inventory.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.ui_view.ViewProvider
import ru.smartro.inventory.*
import ru.smartro.inventory.base.AbstrActF
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.core.*
import ru.smartro.inventory.database.Config
import ru.smartro.inventory.database.PlatformEntityRealm
import ru.smartro.inventory.showErrorDialog
import ru.smartro.worknote.extensions.simulateClick
import java.util.*


class MapFragment : AbstrActF(), UserLocationObjectListener, Map.CameraCallback,
    MapObjectTapListener {

    companion object {
        fun newInstance() = MapFragment()
    }

    private lateinit var mAcbInfo: AppCompatButton
//    private lateinit var mActvInfoSynchroCnt: AppCompatTextView
//    private lateinit var mActvInfoDiffCnt: AppCompatTextView
    private lateinit var mMapObjectCollection: MapObjectCollection
    private lateinit var mViewModel: MapViewModel
    private lateinit var mMapView: MapView

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("message")
            log.info("mMessageReceiver $message")
            setInfoData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.map_fragment, container, false)
        return view
    }


    private fun setInfoData() {
        val cntCreateConfig = db().loadConfigL("cnt_platform__create")
        val cntSyncConfig = db().loadConfigL("cnt_platform__sync")
        val cntDiff = cntCreateConfig.toLong() - cntSyncConfig.toLong()
        mAcbInfo.text = "${cntCreateConfig.value} / ${cntSyncConfig.value}"
//        mActvInfoSynchroCnt.text = "Отправлено : ${cntSyncConfig.value}"
//        mActvInfoDiffCnt.text = "Ожидают отправки : ${cntDiff}"
    }

    private fun gotoMyLocation() {
        log.debug("gotoMyLocation.before")
        try {
            val lastPoint = getLastPoint()
            log.info("gotoMyLocation", lastPoint.toString())
            val cameraPosition = CameraPosition(lastPoint, 17.7f, 0.0f, 0.0f)
            val cameraAnimation = Animation(Animation.Type.SMOOTH, 1F)
            mMapView.map.move(cameraPosition, cameraAnimation, this)
        } catch (e: Exception) {
            showErrorToast("Что-то пошло не так :(")
        }

    }

    private fun gotoCreatePlatform() {
        val newPlatformUuid = UUID.randomUUID().toString()
        showFragment(PhotoPlatformFragment.newInstance(newPlatformUuid))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenOrientation(false)
        mViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MapViewModel::class.java)

        mMapView = view.findViewById<View>(R.id.mapview) as MapView
        mMapObjectCollection = mMapView.map.mapObjects

        mAcbInfo = view.findViewById(R.id.acb_map_fragment__info)
        mAcbInfo.setOnClickListener {
            val cntCreateConfig = db().loadConfigL("cnt_platform__create")
            val cntSyncConfig = db().loadConfigL("cnt_platform__sync")
            val cntDiff = cntCreateConfig.toLong() - cntSyncConfig.toLong()

//        mActvInfoSynchroCnt.text = "Отправлено : ${cntSyncConfig.value}"
//        mActvInfoDiffCnt.text = "Ожидают отправки : ${cntDiff}"
            var textErrorDialog = "Создано : ${cntCreateConfig.value}"
            textErrorDialog += "\nОтправлено : ${cntSyncConfig.value}"
            textErrorDialog += "\nОжидают отправки : ${cntDiff}"

            val lastSynchroDateTime = db().loadConfigSting("last_synchro_datetime")
            textErrorDialog += "\n\nПоследняя успешная \nсинхронизация :\n${lastSynchroDateTime}"
            showInfoDialog(textErrorDialog)
        }
//        mActvInfoSynchroCnt = view.findViewById(R.id.actv_map_fragment__info_synchro_cnt)
//        mActvInfoDiffCnt = view.findViewById(R.id.actv_map_fragment__info_diff_cnt)
        setInfoData()

        showHideActionBar(true)

        val mMapKit = MapKitFactory.getInstance()
        val userLocationLayer = mMapKit.createUserLocationLayer(mMapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.isAutoZoomEnabled = true
        userLocationLayer.setObjectListener(this)

        val fabGotoMyLocation = view.findViewById<FloatingActionButton>(R.id.fab_map_fragment__goto_my_location)
        fabGotoMyLocation.setOnClickListener {
            gotoMyLocation()
            sendPlatformRequest()
            if (isNotActualLocation()) {
                showErrorToast("Нет актуальных координат...")
                return@setOnClickListener
            }
        }

        val apbLogout = view.findViewById<AppCompatButton>(R.id.apb_map_fragment__logout)
        apbLogout.setOnClickListener {
            logOUT()
        }
        val apbCreatePlatform = view.findViewById<AppCompatButton>(R.id.apb_map_fragment__add_platform)
        apbCreatePlatform.setOnClickListener{
            val containerStatus = db().loadContainerStatus()
            val containerType = db().loadContainerType()
            val platformType = db().loadPlatformType()

            if (containerStatus.isSpinnerADataO() || containerType.isSpinnerADataO() || platformType.isSpinnerADataO()) {
                showErrorToast("Каталог Типов и Статусов пуст, попробуйте ещё раз")
                val ownerId = db().loadConfigInt("Owner")
                CatalogRequestRPC().callAsyncRPC(ownerId)
                return@setOnClickListener
            }

            gotoCreatePlatform()
        }

        val accbIsGPSMode = view.findViewById<AppCompatCheckBox>(R.id.accb_map_fragment__is_gps_mode)
        accbIsGPSMode.isChecked = !db().loadConfigBool("is_yandex_location_service")
        accbIsGPSMode.setOnCheckedChangeListener { compoundButton, b ->
            val config = Config("is_yandex_location_service", (!b).toString())
            db().saveConfig(config)
            setLocationService()
        }
        updateData()
        gotoMyLocation()
        callPlatformRequest()
        mMapObjectCollection.addTapListener(this)

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mMessageReceiver,
            IntentFilter("custom-event-name")
        )

        apbCreatePlatform.simulateClick()
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver)
    }


    private fun callPlatformRequest() {
        // TODO: 15.11.2021
        val isAllowed = db().loadConfigBool("is_allowed_inventory_get_platforms")
        if (isAllowed) {
            log.info("callPlatformRequest.isAllowed=${isAllowed}")
            sendPlatformRequest()
        } else {
            log.debug("callPlatformRequest.isAllowed=${isAllowed}")
        }
    }

    private fun sendPlatformRequest() {
        val ownerId = db().loadConfigInt("Owner")
        val rpcEntity =
            RPCProvider("inventory_get_platforms", getLastPoint()).getRPCEntity(ownerId)
        val restClient = RestClient()
        val conic = PlatformRequestRPC(restClient, requireContext()).callAsyncRPC(rpcEntity)
        conic.observe(
            viewLifecycleOwner
        ) { platforms ->
            updateData()
        }
    }


    override fun onBackPressed() {
        requireActivity().finish()
    }

    private fun updateData() {
        val platformSEntity =  db().loadPlatformEntityS()
        platformSEntity.forEach { platform ->
            try {
                val point = Point(platform.coordinateLat, platform.coordinateLng)
                mMapObjectCollection.addPlacemark(point, getIconViewProvider(platform.getIconDrawableResId()))
            } catch (e: Exception) {
                log.error("updateData", e)
                log.error("updateData plaform.id= ${platform.id}")
            }
        }
    }


    private fun getIconViewProvider(drawableResId: Int): ViewProvider {
        fun iconMarker(_drawableResId: Int): View {
            val resultIcon = View(context).apply { background = ContextCompat.getDrawable(context, _drawableResId) }
            return resultIcon
        }
        return ViewProvider(iconMarker(drawableResId))
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mMapView.onStop()
    }

    class MapViewModel : ViewModel() {
    }

    override fun onObjectAdded(p0: UserLocationView) {
        log.warn("onObjectAdded", "userLocationLayer")
    }

    override fun onObjectRemoved(p0: UserLocationView) {
//        TODO("Not yet implemented")
        log.error("onObjectRemoved", "userLocationLayer")

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
        log.warn("onObjectUpdated", "userLocationLayer")
        log.warn("onObjectUpdated", p1)
    }

    override fun onMoveFinished(p0: Boolean) {
        log.warn("onMoveFinished p0=", p0)

    }

    override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {
        val placeMark = p0 as PlacemarkMapObject
        val coordinate = placeMark.geometry
        showErrorToast("${coordinate.latitude} ${coordinate.longitude}")
        return true
    }

}





