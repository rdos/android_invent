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
import io.realm.Realm
import ru.smartro.inventory.Inull
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.core.*
import ru.smartro.inventory.database.ContainerEntityRealm
import ru.smartro.inventory.database.PlatformEntityRealm
import ru.smartro.inventory.toast


class MapFragment : AbstractFragment(), UserLocationObjectListener, Map.CameraCallback{

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
            toast("Что-то пошло не так :(")
        }
    }

    private fun gotoAddPlatform() {
        val nextId = Realm.getDefaultInstance().where(PlatformEntityRealm::class.java).max("id")?.toInt()?.plus(1)?: Inull
        val platformEntity: PlatformEntityRealm = db().createPlatformEntity(nextId)
//        if (platformEntity.isOnull()) {
            showNextFragment(PhotoPlatformFragment.newInstance(platformEntity.id))
//        }
//        val platformEntity = PlatformEntityRealm((0..2002).random())
//        db().save {
//            db().insert(platformEntity)
//        }
//        showNextFragment(PlatformPhotoFragment.newInstance(platformEntity.id))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        }

        mApbAddPlatform = view.findViewById<AppCompatButton>(R.id.apb_map_fragment__add_platform)
        mApbAddPlatform.setOnClickListener{
            gotoAddPlatform()
        }
        gotoMyLocation()
        val platformEntity =  db().loadPlatformEntity()
        addPlatformToMap(platformEntity)

        // TODO: 15.11.2021 
        val rpcEntity = RPCProvider("inventory_mobile_get_platforms", getLastPoint()).getRPCEntity()
        val restClient = RestClient()
        val conic = PlatformRequestRPC(restClient).callAsyncRPC(rpcEntity)
        conic.observe(
            viewLifecycleOwner,
            { platforms ->
//                cats?.let {
//                    Log.d(TAG, "mafka: 0)ne ${it.size}"
//                    photoRecyclerView.adapter = PhotoAdapter(it, context)
//                }
//                showFragment(OwnerFragment.newInstance())
                addPlatformToMap(platforms)
            }
        )


        val rpcGetCatalogs = CatalogsRequestEntity(PayLoadCatalogRequest())

        val con = CatalogRequestRPC(restClient).callAsyncRPC(rpcGetCatalogs)

//        debug_fab.setOnClickListener {
//            startActivity(Intent(this, DebugActivity::class.java))
//        }
        mMapObjectCollection.addTapListener { mapObject, point ->
            log.info("mMapObjectCollection")
            true
        }
    }


    private fun getIconViewProvider(drawableResId: Int): ViewProvider {
        fun iconMarker(_drawableResId: Int): View {
            val resultIcon = View(context).apply { background = ContextCompat.getDrawable(context, _drawableResId) }
            return resultIcon
        }
        return ViewProvider(iconMarker(drawableResId))
    }

    private fun addPlatformToMap(platforms: List<PlatformEntityRealm>) {
        platforms.forEach {
            try {
                val point = Point(it.coordinates!!.lat, it.coordinates!!.lng)
                mMapObjectCollection.addPlacemark(point, getIconViewProvider(it.getIconDrawableResId()))
            } catch (e: Exception) {
                log.error("addPlatformToMap", e)
                log.error("addPlatformToMap platforms.id= ${it.id}")
            }
        }
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

}