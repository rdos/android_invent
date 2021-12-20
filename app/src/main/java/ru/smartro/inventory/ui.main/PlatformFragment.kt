package ru.smartro.inventory.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.ViewModel
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstrActF

import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.smartro.inventory.Inull
import ru.smartro.inventory.core.*
import ru.smartro.inventory.database.ContainerEntityRealm
import ru.smartro.inventory.database.PlatformTypeRealm
import ru.smartro.inventory.showErrorToast
import java.util.*


class PlatformFragment : AbstrActF() {

    companion object {
        fun newInstance(platformUuid: String): PlatformFragment {
            val fragment = PlatformFragment()
            fragment.addArgument(platformUuid, null)
            return fragment
        }
    }

    private lateinit var mIn: LayoutInflater
    private lateinit var viewModel: PlatformViewModel


    override fun onBackPressed() {
        super.onBackPressed()

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setActionBarTitle(R.string.platform_fragment__welcome_to_system)
        setActionBarTitle("КП " + getCurrdateTime())
        val platformEntity = db().loadPlatformEntity(p_platform_uuid)

        val lastPoint = getLastPoint()
//        val actvCoordinateLat = view.findViewById<AppCompatTextView>(R.id.actv_platform_fragment__coordinate_lat)
//        actvCoordinateLat.text =

        if (isNotActualLocation()) {
            val tilCoordinate = view.findViewById<TextInputLayout>(R.id.til_platform_fragment__coordinate)
            tilCoordinate.error = "Координаты неактульны"
        }
        val tietCoordinate = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment__coordinate)
        tietCoordinate.setText(String.format("%.6f", lastPoint.latitude) + " "+ String.format("%.6f", lastPoint.longitude))

        val acsVid = view.findViewById<AppCompatSpinner>(R.id.acs_platform_fragment__vid)
        val vidAdapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
            requireContext(), R.array.PlatformVid,
            R.layout.platform_fragment_acs_vid__item
        )
        vidAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        acsVid.setAdapter(vidAdapter)
        acsVid.setSelection(0)


        val platformType = db().loadPlatformType()
        val spAdataViewER = view.findViewById<AppCompatSpinner>(R.id.acs_platform_fragment__type)
        val typeAdapter = PlatformTypeAdapter__AData_Spinner(requireContext(), platformType)
        spAdataViewER.setAdapter(typeAdapter)
        spAdataViewER.setSelection(Inull)
        val tietComment = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment__comment)

        val tietLength = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment__length)
        val tietWidth = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment__width)
        val accbHasBase = view.findViewById<AppCompatCheckBox>(R.id.accb_platform_fragment__has_base)
        val accbHasFence = view.findViewById<AppCompatCheckBox>(R.id.accb_platform_fragment__has_fence)

        val acbSave = view.findViewById<AppCompatButton>(R.id.acb_platform_fragment__save)
        acbSave.setOnClickListener {
            log.debug("save_-acbSaveOnClick.before")
            acbSave.isEnabled = false
            try {

                if (isNotCheckedData(tietLength)) return@setOnClickListener
                if (isNotCheckedData(tietWidth)) return@setOnClickListener

                //                if (platformEntity.isEnableSave(acbSave) return@setOnClickListener
                if (isNotEnableSave(acbSave, platformEntity)) return@setOnClickListener
                if (acsVid.selectedItem.toString() == "Выберите вид") {
                    showErrorToast("Выберите вид КП")
                    return@setOnClickListener
                }
                val selectedplatformType = (spAdataViewER.selectedItem as PlatformTypeRealm)
                if (selectedplatformType.id == Inull) {
                    showErrorToast("${selectedplatformType.name} КП")
                    return@setOnClickListener
                }
//                platformEntity.status_name = "Новая"
//                platformEntity.status_id = 1
                log.debug("save_-acbSaveOnClick.after isCheckedData")
                hideKeyboard()
                platformEntity.datetime = getCurrdateTime()
                platformEntity.is_open =
                    if (acsVid.selectedItem.toString() == "Открытая") 1 else 0
                platformEntity.has_base = if (accbHasBase.isChecked) 1 else 0
                platformEntity.has_fence = if (accbHasFence.isChecked) 1 else 0

                try {
                    val splitCoodiate = tietCoordinate.text.toString().replace(",", ".").trim().split(" ")
                    platformEntity.coordinateLat = splitCoodiate[0].toDouble()
                    platformEntity.coordinateLng = splitCoodiate[1].toDouble()
                } catch (e: Exception) {
                    log.error("TODOSTODO", e)
                    showErrorToast("Неверный формат координат")
                    return@setOnClickListener
                }

                platformEntity.type = spAdataViewER.selectedItem as PlatformTypeRealm

                platformEntity.length = tietLength.text.toString().toInt()
                platformEntity.width = tietWidth.text.toString().toInt()
                platformEntity.comment = tietComment.text.toString()

                platformEntity.afterCreate(db())
                db().saveRealmEntity(platformEntity)

                log.debug("save_-acbSaveOnClick.saveRealmEntity")
                deleteOutputDirectory(p_platform_uuid, null)
                log.debug("save_-acbSaveOnClick.deleteOutputDirectory p_platform_uuid=${p_platform_uuid}")

                callOnBackPressed(false)
                callOnBackPressed(false)

            } catch (e: Exception) {
                log.error("TODOTODOTODO")
                showErrorToast(e.message)
            } finally {
                acbSave.isEnabled = true
            }
            log.debug("save_-acbSaveOnClick.after")
//            val con = SynchroRequestRPC().callAsyncRPC(platformEntity)
//            con.observe(
//                viewLifecycleOwner,
//                { bool ->
//
//                    if (bool){
//                        // TODO: 22.11.2021 !!!
//
//                    } else
//                    {
//
//                    }
//                }
//            )
//            showFragment(PlatformFragmentContainerDlt.newInstance())
        }


        val acbAddContainer = view.findViewById<AppCompatButton>(R.id.acb_platform_fragment__add_container)
        acbAddContainer.setOnClickListener {
//            val nextId = Realm.getDefaultInstance().where(ContainerEntityRealm::class.java).max("id")?.toInt()?.plus(1)?: Inull
            val uuid = UUID.randomUUID().toString()
            platformEntity.containers.add(ContainerEntityRealm(uuid))
            db().saveRealmEntity(platformEntity)
            showNextFragment(PhotoContainerFragment.newInstance(p_platform_uuid, uuid))
//            childFragmentManager.beginTransaction()
//                .replace(R.id.fl_platform_fragment, PlatformFragmentContainerDlt.newInstance(nextId))
//                .commitNow()
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.fl_platform_fragment, PlatformFragmentContainerS.newInstance(p_platform_uuid))
            .commitNow()
    }




    class PlatformTypeAdapter__AData_Spinner(context: Context,
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
