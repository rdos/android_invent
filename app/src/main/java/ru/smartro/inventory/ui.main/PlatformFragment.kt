package ru.smartro.inventory.ui.main

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstrActF

import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.smartro.inventory.Inull
import ru.smartro.inventory.core.*
import ru.smartro.inventory.database.ContainerEntityRealm
import ru.smartro.inventory.database.PlatformEntityRealm
import ru.smartro.inventory.database.PlatformTypeRealm
import ru.smartro.inventory.showErrorToast
import ru.smartro.inventory.ui.main.util.showActionDialog
import java.util.*


class PlatformFragment : AbstrActF() {

    companion object {
        fun newInstance(platformUuid: String): PlatformFragment {
            val fragment = PlatformFragment()
            fragment.addArgument(platformUuid, null)
            return fragment
        }
    }

    private var rv: RecyclerView? = null
    private lateinit var mIn: LayoutInflater

    override fun onBackPressed() {
        var alertDialog: AlertDialog? = null
        if (platformEntity?.wasSaved == false) {
            alertDialog = showActionDialog(
                requireContext(),
                "Вы уверены, что хотите отменить создание платформы?",
                positiveAction = {
                    db().deletePlatformEntity(p_platform_uuid)
                    navigateToMap()
                },
                negativeAction = {
                    alertDialog?.dismiss()
                }
            )
        } else {
            alertDialog = showActionDialog(
                requireContext(),
                "Вы уверены, что хотите отменить редактирование платформы?",
                positiveAction = {
                    navigateToMap()
                },
                negativeAction = {
                    alertDialog?.dismiss()
                }
            )
        }
    }

    private fun navigateToMap() {
        requireActivity().supportFragmentManager.popBackStack(MapFragment::class.java.simpleName, 0)
    }
    
    private var platformEntity: PlatformEntityRealm? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showHideActionBar(true)

        val title = view.findViewById<AppCompatTextView>(R.id.actv__platform_fragment__title)
        title.text = "КП ${getCurrdateTime()}"


        view.findViewById<AppCompatImageView>(R.id.aciv__platform_fragment__go_back).setOnClickListener {
            onBackPressed()
        }

        rv = view.findViewById(R.id.rv__platform_fragment__containers)
        rv?.layoutManager = LinearLayoutManager(requireContext())

        updateAdapter()

        val lastPoint = getLastPoint()

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
                if (isNotEnableSave(acbSave, platformEntity!!)) return@setOnClickListener

                if (acsVid.selectedItem.toString() == "Выберите вид") {
                    showErrorToast("Выберите вид КП")
                    return@setOnClickListener
                }
                val selectedplatformType = (spAdataViewER.selectedItem as PlatformTypeRealm)
                if (selectedplatformType.id == Inull) {
                    showErrorToast("${selectedplatformType.name} КП")
                    return@setOnClickListener
                }

                log.debug("save_-acbSaveOnClick.after isCheckedData")

                hideKeyboard()
                platformEntity?.datetime = getCurrdateTime()
                platformEntity?.is_open =
                    if (acsVid.selectedItem.toString() == "Открытая") 1 else 0
                platformEntity?.has_base = if (accbHasBase.isChecked) 1 else 0
                platformEntity?.has_fence = if (accbHasFence.isChecked) 1 else 0

                try {
                    val splitCoodiate = tietCoordinate.text.toString().replace(",", ".").trim().split(" ")
                    platformEntity?.coordinateLat = splitCoodiate[0].toDouble()
                    platformEntity?.coordinateLng = splitCoodiate[1].toDouble()
                } catch (e: Exception) {
                    log.error("TODOSTODO", e)
                    showErrorToast("Неверный формат координат")
                    return@setOnClickListener
                }

                platformEntity?.type = spAdataViewER.selectedItem as PlatformTypeRealm

                platformEntity?.length = tietLength.text.toString().toInt()
                platformEntity?.width = tietWidth.text.toString().toInt()
                platformEntity?.comment = tietComment.text.toString()

                platformEntity?.wasSaved = true
                platformEntity?.afterCreate(db())
                db().saveRealmEntity(platformEntity!!)

                log.debug("save_-acbSaveOnClick.saveRealmEntity")
                deleteOutputDirectory(p_platform_uuid, null)
                log.debug("save_-acbSaveOnClick.deleteOutputDirectory p_platform_uuid=${p_platform_uuid}")

                navigateToMap()
            } catch (e: Exception) {
                log.error("TODOTODOTODO")
                showErrorToast(e.message)
            } finally {
                acbSave.isEnabled = true
            }
            log.debug("save_-acbSaveOnClick.after")
        }


        val acbAddContainer = view.findViewById<AppCompatButton>(R.id.acb_platform_fragment__add_container)
        acbAddContainer.setOnClickListener {
            val uuid = UUID.randomUUID().toString()
            showFragment(PhotoContainerFragment.newInstance(p_platform_uuid, uuid))
        }
    }

    private fun updateAdapter() {
        platformEntity = db().loadPlatformEntity(p_platform_uuid)
        rv?.adapter = ContainerInPlatfornAdapter(platformEntity!!.containers)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mIn = inflater
        val view = inflater.inflate(R.layout.platform_fragment, container, false)

        return view
    }

    class PlatformViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }

    inner class ContainerInPlatfornAdapter(private val platformContainerList: List<ContainerEntityRealm>) :
        RecyclerView.Adapter<PlatformContainerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformContainerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.platform_fragment_container__rv,
                parent, false)
            return PlatformContainerViewHolder(view)

        }

        override fun getItemCount(): Int {
            return platformContainerList.size
        }

        override fun onBindViewHolder(holder: PlatformContainerViewHolder, position: Int) {
            val container = platformContainerList.get(position)
            holder.aptvIndex.text = (position + 1).toString()

            holder.editContainer.setOnClickListener {
                showFragment(PhotoContainerFragment.newInstance(p_platform_uuid, container.uuid))
            }

            holder.removeContainer.setOnClickListener {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                db().deleteContainerEntity(p_platform_uuid, container.uuid)
                                updateAdapter()
                            }

                            DialogInterface.BUTTON_NEGATIVE -> {
                                dialog.dismiss()
                            }
                        }
                    }

                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

                builder.setMessage("Вы уверены, что хотите удалить данный контейнер?")
                    .setPositiveButton("Да, удалить", dialogClickListener)
                    .setNegativeButton("Отмена", dialogClickListener)
                    .show()
            }
        }

    }

    inner class PlatformContainerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val aptvIndex = itemView.findViewById<AppCompatTextView>(R.id.aptv_platform_fragment_container__rv__index)
        val editContainer = itemView.findViewById<AppCompatImageView>(R.id.aciv__platform_fragment_container_item__edit)
        val removeContainer = itemView.findViewById<AppCompatImageView>(R.id.aciv__platform_fragment_container_item__remove)
        override fun onClick(p0: View?) {
        }
    }

}
