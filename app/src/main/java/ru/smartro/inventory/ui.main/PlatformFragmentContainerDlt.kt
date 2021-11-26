package ru.smartro.inventory.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.internal.filterList
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.database.ContainerStatusRealm
import ru.smartro.inventory.database.ContainerTypeRealm
import ru.smartro.inventory.showErrorToast
import java.lang.Exception


class PlatformFragmentContainerDlt(val p_container_uuid: String) : AbstractFragment() {
    private lateinit var mTietNumber: TextInputEditText
    private lateinit var mTilNumber: TextInputLayout

    companion object {
        fun newInstance(containerUuid: String) = PlatformFragmentContainerDlt(containerUuid)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle("Контейнер")
        val mContainerEntityRealm = db().loadContainerEntity(p_container_uuid)

        mTietNumber = view.findViewById(R.id.tiet_platform_fragment_container_dtl__number)
        mTilNumber = view.findViewById(R.id.til_platform_fragment_container_dtl__number)


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
            try {
                acbSaveContainer.isEnabled = false
                if (checkData()) {
                    mContainerEntityRealm.type = acsContainerType.selectedItem as ContainerTypeRealm?
                    mContainerEntityRealm.container_status_id = (acsContainerStatus.selectedItem as ContainerStatusRealm).id
                    mContainerEntityRealm.container_status_name = (acsContainerStatus.selectedItem as ContainerStatusRealm).name
                    mContainerEntityRealm.has_pedal = if(accbPedal.isChecked) 1 else 0
                    mContainerEntityRealm.number = mTietNumber.text.toString()
                    mContainerEntityRealm.comment = tietComment.text.toString()
                    db().saveRealmEntity(mContainerEntityRealm)
                    // TODO: 22.11.2021 !!!
                    callOnBackPressed(false)
                    callOnBackPressed(false)
                }
            } catch (e: Exception) {
                showErrorToast(e.message)
            } finally {
                acbSaveContainer.isEnabled = true
            }
        }
        mTietNumber.setText(mContainerEntityRealm.number)
        tietComment.setText(mContainerEntityRealm.comment)
        containerType.forEachIndexed{index, containerTypeRealm ->
            mContainerEntityRealm.type?.let{
                if(containerTypeRealm.id == mContainerEntityRealm.type!!.id) {
                    acsContainerType.setSelection(index)
                    return@forEachIndexed
                }
            }
        }
        containerStatus.forEachIndexed { index, containerStatusRealm ->
                if(mContainerEntityRealm.container_status_id == containerStatusRealm.id) {
                    acsContainerStatus.setSelection(index)
                    return@forEachIndexed
                }
        }
        accbPedal.isChecked = mContainerEntityRealm.has_pedal == 1
    }

    private fun checkData(): Boolean {
        var result = false
        if (mTietNumber.text.isNullOrBlank()) {
            mTietNumber.error = "Поле обязательно для заполнения"
            return result
        }
        return true
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

