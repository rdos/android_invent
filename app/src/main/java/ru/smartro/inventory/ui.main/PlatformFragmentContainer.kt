package ru.smartro.inventory.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.smartro.inventory.core.LoginEntity
import ru.smartro.inventory.R

import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.core.LoginRequest
import ru.smartro.inventory.database.OwnerResponse


class PlatformFragmentContainer : AbstractFragment(){

    companion object {
        fun newInstance() = PlatformFragmentContainer()
    }

    private lateinit var viewModel: PlatformContainerViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.platform_fragment_container, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(PlatformContainerViewModel::class.java)
        // TODO: Use the ViewModel
        showHideActionBar(true)

        val rv = view.findViewById<RecyclerView>(R.id.rv_platform_fragment_container)
        rv.layoutManager = GridLayoutManager(requireContext(), 3)

        rv.adapter = ContainerInPlatfornAdapter()

//        baseview.setOnClickListener {
//            MyUtil.hideKeyboard(this)
//        }

    }

    inner class ContainerInPlatfornAdapter(private val p_ownerResponse: OwnerResponse) :
        RecyclerView.Adapter<OwnerFragment.OwnerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerFragment.OwnerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.owner_fragment_rv__item,
                parent, false)
            return OwnerViewHolder(view)

        }

        override fun getItemCount(): Int {
            return p_ownerResponse.data.organisationEntityRealms.size
        }

        override fun onBindViewHolder(holder: OwnerFragment.OwnerViewHolder, position: Int) {
            val organisationEntityRealms = p_ownerResponse.data.organisationEntityRealms[position]
            holder.tv.text = organisationEntityRealms.name

            holder.llc.rootView.setOnClickListener(holder)
//            holder.llc.animation =
//                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in_left)
//            setAnimation(holder.itemView, position)
        }

    }



    class PlatformContainerViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }
}