package ru.smartro.inventory.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.smartro.inventory.R

import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.database.ContainerEntityRealm


class PlatformFragmentContainer(val p_platformId: Int) : AbstractFragment(){

    companion object {
        fun newInstance(p_platformId: Int) = PlatformFragmentContainer(p_platformId)
    }
    //
    inner class PlatformContainerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tv = itemView.findViewById<AppCompatTextView>(R.id.aptv_platform_fragment_container__rv__item)
        val llc = itemView.findViewById<LinearLayoutCompat>(R.id.llc_platform_fragment_container__rv__item)
        override fun onClick(p0: View?) {
//            TODO("Not yet implemented")
//            showFragment(MapFragment.newInstance())
        }
    }
    private lateinit var viewModel: PlatformContainerViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.platform_fragment_container, container, false)
        return view
    }


    private fun goPlatformAddContainerS(): List<ContainerEntityRealm> {
//private fun gotoAddContainerS(): List<ContainerEntityRealm> {
//          reSUlt
//      val containerSE =  db().createContainerSEntity()
        val containerS =  db().createContainerEntityS()
//        if (containerS.isOnull()) {
//            db().saveRealmEntity(containerS)
//            showNextFragment(PlatformPhotoFragment.newInstance(res.id))
//       }

        //
     return containerS
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(PlatformContainerViewModel::class.java)
        // TODO: Use the ViewModel
        showHideActionBar(true)


//        val containerS: List<ContainerEntityRealm> = goPlatformAddContainerS()

        val rv = view.findViewById<RecyclerView>(R.id.rv_platform_fragment_container)
        rv.layoutManager = GridLayoutManager(requireContext(), 3)
        val platformContainerList = db().loadPlatformContainers(p_platformId)
        rv.adapter = ContainerInPlatfornAdapter(platformContainerList)

//        baseview.setOnClickListener {
//            MyUtil.hideKeyboard(this)
//        }

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
            holder.tv.text = container.number

            holder.llc.rootView.setOnClickListener(holder)
//            holder.llc.animation =
//                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in_left)
//            setAnimation(holder.itemView, position)
        }

    }

    private fun <E> List<E>.isOnull(): Boolean {
        log.error(" private fun <E> List<E>.isOnull(): Boolean {")
        log.warn("TODO")
        log.info("TODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODOTODO")
        log.error("PlatformFragmentContainer.isOnull")
        return false
    }


    class PlatformContainerViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }
}

