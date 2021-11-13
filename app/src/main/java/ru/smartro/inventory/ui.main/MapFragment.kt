package ru.smartro.inventory.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.smartro.inventory.R

import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import ru.smartro.inventory.base.AbstractFragment


class MapFragment : AbstractFragment() {

    companion object {
        private val TARGET_LOCATION = Point(59.945933, 30.320045)
        fun newInstance() = MapFragment()
    }

    private lateinit var mapView: MapView
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.map_fragment, container, false)
        mapView = view.findViewById<View>(R.id.mapview) as MapView

        // And to show what can be done with it, we move the camera to the center of Saint Petersburg.
        mapView.map.move(
            CameraPosition(TARGET_LOCATION, 14.0F, 0.0F, 0.0F),
            Animation(Animation.Type.SMOOTH, 5F),
            null
        )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(LoginViewModel::class.java)
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mapView.onStop()
    }

}