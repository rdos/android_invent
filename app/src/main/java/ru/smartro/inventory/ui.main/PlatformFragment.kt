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
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.textfield.TextInputEditText
import ru.smartro.inventory.core.*
import ru.smartro.inventory.database.ContainerEntityRealm
import ru.smartro.inventory.database.PlatformTypeRealm
import ru.smartro.inventory.showErrorToast
import java.text.SimpleDateFormat
import java.util.*


class PlatformFragment(val p_platform_uuid: String) : AbstractFragment() {

    companion object {
        fun newInstance(platformUuid: String) = PlatformFragment(platformUuid)
    }

    private lateinit var mTietLength: TextInputEditText
    private lateinit var mIn: LayoutInflater
    private lateinit var viewModel: PlatformViewModel

    fun currentTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setActionBarTitle(R.string.platform_fragment__welcome_to_system)
        setActionBarTitle("Данные по КП" + currentTime())
        val platformEntity = db().loadPlatformEntity(p_platform_uuid)

        val actvCoordinateLat = view.findViewById<AppCompatTextView>(R.id.actv_platform_fragment__coordinate_lat)
        actvCoordinateLat.text = String.format("%.6f", getLastPoint().latitude)

        val actvCoordinateLng = view.findViewById<AppCompatTextView>(R.id.actv_platform_fragment__coordinate_lng)
        actvCoordinateLng.text = String.format("%.6f", getLastPoint().longitude)

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

        mTietLength = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment__length)
        val tietWidth = view.findViewById<TextInputEditText>(R.id.tiet_platform_fragment__width)
        val accbHasBase = view.findViewById<AppCompatCheckBox>(R.id.accb_platform_fragment__has_base)
        val accbHasFence = view.findViewById<AppCompatCheckBox>(R.id.accb_platform_fragment__has_fence)

        val acbSave = view.findViewById<AppCompatButton>(R.id.acb_platform_fragment__save)
        acbSave.setOnClickListener {
            log.debug("save_-acbSaveOnClick.before")
            acbSave.isEnabled = false
            try {
                if (!isCheckedData(mTietLength)) return@setOnClickListener
                if (!isCheckedData(tietWidth)) return@setOnClickListener

                platformEntity.datetime = currentTime()
                platformEntity.is_open =
                    if (acsVid.selectedItem.toString() == "Открытая") 1 else 0
                platformEntity.has_base = if (accbHasBase.isChecked) 1 else 0
                platformEntity.has_fence = if (accbHasFence.isChecked) 1 else 0
                platformEntity.coordinateLat = getLastPoint().latitude
                platformEntity.coordinateLng = getLastPoint().longitude
                platformEntity.type = acsType.selectedItem as PlatformTypeRealm


                platformEntity.length = mTietLength.text.toString().toInt()
                platformEntity.width = tietWidth.text.toString().toInt()
                platformEntity.comment = tietComment.text.toString()

                platformEntity.setSynchroDisable()
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
            .replace(R.id.fl_platform_fragment, PlatformFragmentContainer.newInstance(p_platform_uuid))
            .commitNow()
    }


    private fun isCheckedData(mTietHeight: TextInputEditText): Boolean {
        var result = false
        if (mTietHeight.text.isNullOrBlank()) {
            mTietHeight.error = "Поле обязательно для заполнения"
            return result
        }
        return true
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