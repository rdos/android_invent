package ru.smartro.inventory.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.smartro.inventory.R

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
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.core.*
import ru.smartro.inventory.database.PlatformEntityRealm
import ru.smartro.inventory.showErrorToast
import ru.smartro.worknote.extensions.simulateClick
import java.util.*


class MapFragment : AbstractFragment(), UserLocationObjectListener, Map.CameraCallback,
    MapObjectTapListener {

    companion object {
        fun newInstance() = MapFragment()
    }

    private lateinit var mApbAddPlatform: AppCompatButton
    private lateinit var mMapObjectCollection: MapObjectCollection
    private lateinit var mViewModel: MapViewModel
    private lateinit var mMapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.map_fragment, container, false)
        return view
    }

    private fun gotoMyLocation() {
        log.debug("gotoMyLocation.before")
        try {
            log.info("gotoMyLocation", getLastPoint().toString())
            val cameraPosition = CameraPosition(getLastPoint(), 15.0f, 0.0f, 0.0f)
            val cameraAnimation = Animation(Animation.Type.SMOOTH, 1F)
            mMapView.map.move(cameraPosition, cameraAnimation, this)
        } catch (e: Exception) {
            showErrorToast("Что-то пошло не так :(")
        }

    }

    private fun gotoCreatePlatform() {
//        val nextId = Realm.getDefaultInstance().where(PlatformEntityRealm::class.java).max("id")?.toInt()?.plus(1)?: Inull
        val platformEntity: PlatformEntityRealm = db().createPlatformEntity(UUID.randomUUID().toString())
//        if (platformEntity.isOnull()) {
            showNextFragment(PhotoPlatformFragment.newInstance(platformEntity.uuid))
//        }
//        val platformEntity = PlatformEntityRealm((0..2002).random())
//        db().save {
//            db().insert(platformEntity)
//        }
//        showNextFragment(PlatformPhotoFragment.newInstance(platformEntity.id))
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
        }

        mApbAddPlatform = view.findViewById<AppCompatButton>(R.id.apb_map_fragment__add_platform)
        mApbAddPlatform.setOnClickListener{
            val containerStatus = db().loadContainerStatus()
            if (containerStatus.isEmpty()) {
                showErrorToast("containerStatus is Empty")
                return@setOnClickListener
            }
            val containerType = db().loadContainerType()
            if (containerType.isEmpty()) {
                showErrorToast("containerType is Empty")
                return@setOnClickListener
            }
            val platformType = db().loadPlatformType()
            if (platformType.isEmpty()) {
                showErrorToast("platformType is Empty")
                return@setOnClickListener
            }
            gotoCreatePlatform()
        }


        updateData()
        gotoMyLocation()
        callPlatformRequest()
        mMapObjectCollection.addTapListener(this)

        mApbAddPlatform.simulateClick()
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
            viewLifecycleOwner,
            { platforms ->
                updateData()
            }
        )
    }

    private fun updateData() {
        val platformSEntity =  db().loadPlatformEntityS()
        platformSEntity.forEach { platform ->
            try {
                val point = Point(platform.coordinateLat, platform.coordinateLng)
                mMapObjectCollection.addPlacemark(point, getIconViewProvider(platform.getIconDrawableResId()))
            } catch (e: Exception) {
                log.error("addPlatformToMap", e)
                log.error("addPlatformToMap plaform.id= ${platform.id}")
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