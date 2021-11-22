package ru.smartro.inventory.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSpinner
import ru.smartro.inventory.R

import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.database.ContainerStatusRealm
import ru.smartro.inventory.database.ContainerTypeRealm
import ru.smartro.inventory.database.PlatformTypeRealm


class PlatformFragmentContainerDlt(val p_container_id: Int) : AbstractFragment() {

    companion object {
        fun newInstance(containerId: Int) = PlatformFragmentContainerDlt(containerId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val containerEntityRealm = db().loadContainerEntity(p_container_id)

        val tietNumber = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment_container_dtl__number)

        val containerStatus = db().loadContainerStatus()
        val acsContainerStatus = view.findViewById<AppCompatSpinner>(R.id.acs_platform_fragment_container_dtl__status)
        val containerStatusAdapter = StatusContainerAdapter(requireContext(), containerStatus)
        acsContainerStatus.setAdapter(containerStatusAdapter)

        val containerType = db().loadContainerType()
        val acsContainerType = view.findViewById<AppCompatSpinner>(R.id.acs_platform_fragment_container_dtl__type)
        val containerTypeAdapter = TypeContainerAdapter(requireContext(), containerType)
        acsContainerType.setAdapter(containerTypeAdapter)

        val accbPedal = view.findViewById<AppCompatCheckBox>(R.id.accb_platform_fragment_container_dtl_pedal)
        val tietComment = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment_container_dtl__comment)

        val acbSaveContainer = view.findViewById<AppCompatButton>(R.id.acb_platform_fragment_container_dtl__save_container)
        acbSaveContainer.setOnClickListener {
            containerEntityRealm.type = acsContainerType.selectedItem as ContainerTypeRealm?
            containerEntityRealm.container_status_id = (acsContainerStatus.selectedItem as ContainerStatusRealm).id
            containerEntityRealm.container_status_name = (acsContainerStatus.selectedItem as ContainerStatusRealm).name
            containerEntityRealm.has_pedal = if(accbPedal.isChecked) 1 else 0
            containerEntityRealm.number = tietNumber.text.toString()
            containerEntityRealm.comment = tietComment.text.toString()
            db().saveRealmEntity(containerEntityRealm)
            // TODO: 22.11.2021 !!!
            exitFragment()
            exitFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.platform_fragment_container_dtl, container, false)
        return view
    }

    class TypeContainerAdapter(context: Context,
                      containerType: List<ContainerTypeRealm>)
        : ArrayAdapter<ContainerTypeRealm>(context, 0, containerType) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent);
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent);
        }

        fun createItemView(position: Int, recycledView: View?, parent: ViewGroup):View {
            val type = getItem(position)

            val view = recycledView ?: LayoutInflater.from(context).inflate(
                R.layout.platform_fragment_acs_type_item,
                parent,
                false
            )

            type?.let {
                view.findViewById<TextView>(R.id.textViewCountryName).text = type.name
            }
            return view
        }
    }

    class StatusContainerAdapter(context: Context,
                               containerStatus: List<ContainerStatusRealm>)
        : ArrayAdapter<ContainerStatusRealm>(context, 0, containerStatus) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent);
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent);
        }

        fun createItemView(position: Int, recycledView: View?, parent: ViewGroup):View {
            val status = getItem(position)

            val view = recycledView ?: LayoutInflater.from(context).inflate(
                R.layout.platform_fragment_acs_type_item,
                parent,
                false
            )

            status?.let {
                view.findViewById<TextView>(R.id.textViewCountryName).text = status.name
            }
            return view
        }
    }

    class PlatformFragmentContainerDltViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }

}

