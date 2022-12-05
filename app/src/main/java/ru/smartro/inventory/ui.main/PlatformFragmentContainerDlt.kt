package ru.smartro.inventory.ui.main

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.smartro.inventory.Inull
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstrActF
import ru.smartro.inventory.database.ContainerEntityRealm
import ru.smartro.inventory.database.ContainerStatusRealm
import ru.smartro.inventory.database.ContainerTypeRealm
import ru.smartro.inventory.showErrorToast
import ru.smartro.inventory.ui.main.util.showActionDialog
import java.lang.Exception


class PlatformFragmentContainerDlt : AbstrActF() {
    private lateinit var mTietNumber: TextInputEditText
    private lateinit var mTilNumber: TextInputLayout

    private var isBackPressEnabled = true

    private var mContainerEntityRealm: ContainerEntityRealm? = null

    companion object {
        fun newInstance(platformUuid: String, containerUuid: String): PlatformFragmentContainerDlt {
            val fragment = PlatformFragmentContainerDlt()
            fragment.addArgument(platformUuid, containerUuid)
            return fragment
        }
    }

    override fun onBackPressed() {
        if(mContainerEntityRealm?.wasSaved == false) {
            showActionDialog(
                requireContext(),
                "Вы уверены, что хотите отменить создание контейнера?",
                positiveAction = {
                    db().deleteContainerEntity(p_platform_uuid, p_container_uuid!!)
                    isBackPressEnabled = false
                    callOnBackPressed(false)
                    callOnBackPressed(false)
                },
                negativeAction = {
                    isBackPressEnabled = true
                }
            )
        } else {
            showActionDialog(
                requireContext(),
                "Вы уверены, что хотите отменить редактирование контейнера?",
                positiveAction = {
                    isBackPressEnabled = false
                    callOnBackPressed(false)
                    callOnBackPressed(false)
                },
                negativeAction = {
                    isBackPressEnabled = true
                }
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideActionBar(true)

        view.findViewById<AppCompatImageView>(R.id.aciv__container_fragment__go_back).setOnClickListener {
            callOnBackPressed(true)
        }

        mContainerEntityRealm = db().loadContainerEntity(p_platform_uuid, p_container_uuid!!)

        val title = view.findViewById<AppCompatTextView>(R.id.actv__platform_fragment_container__title)
        if(mContainerEntityRealm!!.wasSaved == true) {
            title.text = "Редактирование контейнера"
        }

        mTietNumber = view.findViewById(R.id.tiet_platform_fragment_container_dtl__number)
        mTilNumber = view.findViewById(R.id.til_platform_fragment_container_dtl__number)


        val containerStatus = db().loadContainerStatus()
        val acsContainerStatus = view.findViewById<AppCompatSpinner>(R.id.acs_platform_fragment_container_dtl__status)
        val containerStatusAdapter = StatusContainerAdapter(requireContext(), containerStatus)
        acsContainerStatus.setAdapter(containerStatusAdapter)
        acsContainerStatus.setSelection(0)
        val containerType = db().loadContainerType()
        val acsContainerType = view.findViewById<AppCompatSpinner>(R.id.acs_platform_fragment_container_dtl__type)
        val containerTypeAdapter = TypeContainerAdapter(requireContext(), containerType)
        acsContainerType.setAdapter(containerTypeAdapter)

        val accbPedal = view.findViewById<AppCompatCheckBox>(R.id.accb_platform_fragment_container_dtl_pedal)
        val tietComment = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment_container_dtl__comment)

        val acbSaveContainer = view.findViewById<AppCompatButton>(R.id.acb_platform_fragment_container_dtl__save_container)
        acbSaveContainer.setOnClickListener {
            log.debug("save_-acbSaveContainer.before")
            acbSaveContainer.isEnabled = false

            try {
//                if (isNotCheckedData(mTietNumber)) return@setOnClickListener
                val selectedContainerStatus = (acsContainerStatus.selectedItem as ContainerStatusRealm)
                if (selectedContainerStatus.id == Inull) {
                    showErrorToast("Выберите состояние")
                    return@setOnClickListener
                }
                val selectedContainerType = acsContainerType.selectedItem as ContainerTypeRealm
                if (selectedContainerType.id == Inull) {
                    showErrorToast("Выберите тип контейнера")
                    return@setOnClickListener
                }
                log.debug("save_-acbSaveContainer.after isCheckedData")
                hideKeyboard()
                mContainerEntityRealm?.wasSaved = true

                mContainerEntityRealm?.type = selectedContainerType
                mContainerEntityRealm?.container_status_id = selectedContainerStatus.id
                mContainerEntityRealm?.container_status_name = selectedContainerStatus.name
                mContainerEntityRealm?.has_pedal = if(accbPedal.isChecked) 1 else 0
                mContainerEntityRealm?.number = mTietNumber.text.toString()
                mContainerEntityRealm?.comment = tietComment.text.toString()
                mContainerEntityRealm?.wasSaved = true
                db().saveRealmEntity(mContainerEntityRealm!!)
                // TODO: 22.11.2021 !!!
                callOnBackPressed(false)
                callOnBackPressed(false)

            } catch (e: Exception) {
                showErrorToast(e.message)
            } finally {
                acbSaveContainer.isEnabled = true
            }
        }
        mTietNumber.setText(mContainerEntityRealm?.number)
        tietComment.setText(mContainerEntityRealm?.comment)
        containerType.forEachIndexed{index, containerTypeRealm ->
            mContainerEntityRealm?.type?.let{
                if(containerTypeRealm.id == mContainerEntityRealm?.type!!.id) {
                    acsContainerType.setSelection(index)
                    return@forEachIndexed
                }
            }
        }
        containerStatus.forEachIndexed { index, containerStatusRealm ->
                if(mContainerEntityRealm?.container_status_id == containerStatusRealm.id) {
                    acsContainerStatus.setSelection(index)
                    return@forEachIndexed
                }
        }
        accbPedal.isChecked = mContainerEntityRealm?.has_pedal == 1
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

