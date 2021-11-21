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
import ru.smartro.inventory.database.PlatformTypeRealm
import java.text.SimpleDateFormat
import java.util.*


class PlatformFragment : AbstractFragment() {

    companion object {
        fun newInstance() = PlatformFragment()
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

        val platformType = db().loadPlatformType()

        acsVid.setAdapter(vidAdapter)

        val acsType = view.findViewById<AppCompatSpinner>(R.id.acs_platform_fragment__type)
        val typeAdapter = TypeAdapter(requireContext(), platformType)
        acsType.setAdapter(typeAdapter)



        val acbSave = view.findViewById<AppCompatButton>(R.id.acb_platform_fragment__save)
        acbSave.setOnClickListener {
            showFragment(PlatformFragmentContainerDlt.newInstance())
        }



        childFragmentManager.beginTransaction()
            .replace(R.id.fl_platform_fragment, PlatformFragmentContainer.newInstance())
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