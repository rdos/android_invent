package ru.smartro.inventory.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.ViewModel
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstractFragment

import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.textfield.TextInputEditText
import io.realm.Realm
import ru.smartro.inventory.Inull
import ru.smartro.inventory.database.ContainerEntityRealm
import ru.smartro.inventory.database.PlatformTypeRealm
import java.text.SimpleDateFormat
import java.util.*


class PlatformFragment(val p_platform_id: Int) : AbstractFragment() {

    companion object {
        fun newInstance(p_id: Int) = PlatformFragment(p_id)
    }

    private lateinit var mIn: LayoutInflater
    private lateinit var viewModel: PlatformViewModel

    fun currentTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy--HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setActionBarTitle(R.string.platform_fragment__welcome_to_system)
        setActionBarTitle("Данные по КП" + currentTime())

        val platformEntity = db().loadPlatformEntity(p_platform_id)

        val actvCoordinateLat = view.findViewById<AppCompatTextView>(R.id.actv_platform_fragment__coordinate_lat)
        actvCoordinateLat.text = getLastPoint().latitude.toString()
        val actvCoordinateLng = view.findViewById<AppCompatTextView>(R.id.actv_platform_fragment__coordinate_lng)
        actvCoordinateLng.text = getLastPoint().longitude.toString()

        val acsVid = view.findViewById<AppCompatSpinner>(R.id.acs_platform_fragment__vid)
        val vidAdapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            requireContext(), R.array.PlatformVid,
          R.layout.platform_fragment_acs_vid__item
        )
        vidAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        acsVid.setAdapter(vidAdapter)


        val platformType = db().loadPlatformType()
        val acsType = view.findViewById<AppCompatSpinner>(R.id.acs_platform_fragment__type)
        val typeAdapter = TypeAdapter(requireContext(), platformType)
        acsType.setAdapter(typeAdapter)

        val tietComment = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment__comment)

        val tietHeight = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment__height)
        val tietWidth = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment__width)


        val acbSave = view.findViewById<AppCompatButton>(R.id.acb_platform_fragment__save)
        acbSave.setOnClickListener {
            if(acsVid.selectedItem.toString() == "Открытая") {
                platformEntity.is_open = 1
            } else {
                platformEntity.is_open = 0
            }
            platformEntity.coordinates?.lat= getLastPoint().latitude
            platformEntity.coordinates?.lng= getLastPoint().longitude
            platformEntity.type = acsType.selectedItem as PlatformTypeRealm
            try {
                platformEntity.length = tietHeight.text.toString().toInt()
                platformEntity.width = tietWidth.text.toString().toInt()
                platformEntity.comment = tietComment.text.toString()
            } catch (e: Exception) {
                log.error("TODOTODOTODO")
            }
            
            getOutputDirectory(p_platform_id, null).deleteOnExit()

            exitFragment()
            exitFragment()
//            showFragment(PlatformFragmentContainerDlt.newInstance())
        }


        val acbAddContainer = view.findViewById<AppCompatButton>(R.id.acb_platform_fragment__add_container)
        acbAddContainer.setOnClickListener {
            val nextId = Realm.getDefaultInstance().where(ContainerEntityRealm::class.java).max("id")?.toInt()?.plus(1)?: Inull
            platformEntity.containers.add(ContainerEntityRealm(nextId))
            db().saveRealmEntity(platformEntity)
            showNextFragment(PhotoContainerFragment.newInstance(p_platform_id, nextId))
//            childFragmentManager.beginTransaction()
//                .replace(R.id.fl_platform_fragment, PlatformFragmentContainerDlt.newInstance(nextId))
//                .commitNow()
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.fl_platform_fragment, PlatformFragmentContainer.newInstance(p_platform_id))
            .commitNow()
    }


    class TypeAdapter(context: Context,
                               platformType: List<PlatformTypeRealm>)
        : ArrayAdapter<PlatformTypeRealm>(context, 0, platformType) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent);
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent);
        }

        fun createItemView(position: Int, recycledView: View?, parent: ViewGroup):View {
            val country = getItem(position)

            val view = recycledView ?: LayoutInflater.from(context).inflate(
                R.layout.platform_fragment_acs_type_item,
                parent,
                false
            )

            country?.let {
                view.findViewById<TextView>(R.id.textViewCountryName).text = country.name
            }
            return view
        }
    }

//    inner class MyAdapter(val platformType: List<PlatformTypeRealm>) : BaseAdapter() {
//        override fun getCount(): Int {
//            return platformType.size
//        }
//
//        override fun getItem(p0: Int): Any {
//            return platformType.get(p0)
//        }
//
//        override fun getItemId(p0: Int): Long {
//            return 11
//        }
//
//        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
//           val view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, p2)
//            view.find
//            return p1!!
//        }
//
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mIn = inflater
        val view = inflater.inflate(R.layout.platform_fragment, container, false)
//        view.findViewById<AppCompatButton>(R.id.acb_login_fragment).setOnClickListener {
//            exitFragment()
//        }
//        Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT).show();
        return view
    }

    class PlatformViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }
}