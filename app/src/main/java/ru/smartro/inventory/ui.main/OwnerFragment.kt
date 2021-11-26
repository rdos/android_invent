package ru.smartro.inventory.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.core.CatalogRequestRPC
import ru.smartro.inventory.core.OwnerRequest
import ru.smartro.inventory.database.ConfigEntityRealm
import ru.smartro.inventory.database.OrganisationRealmEntity
import ru.smartro.inventory.database.OwnerResponse
import ru.smartro.inventory.showErrorToast

//     android:background="?android:attr/selectableItemBackground">
class OwnerFragment : AbstractFragment(){
    private lateinit var viewModel: OwnerViewModel
//


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle(R.string.owner_fragment__choose_owner)
//        viewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.NewInstanceFactory()
//        ).get(LoginViewModel::class.java)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_owner_fragment)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val restClient = RestClient()
        val ownerRequest = OwnerRequest(restClient)
        val col = ownerRequest.callAsyncOwner()
        col.observe(
            viewLifecycleOwner,
            { ownerResponse ->
                for (ownerEntity in ownerResponse.data.organisationRealmEntities) {
                    log.info("AAA ownerEntity.name=${ownerEntity.name}")
                }
                recyclerView.adapter = OwnerAdapter(ownerResponse)
                if (ownerResponse.data.organisationRealmEntities.size == 1) {
                    showNextFra(ownerResponse.data.organisationRealmEntities[0].id)
                }
            }
        )

        // TODO: Use the ViewModel
//        view.findViewById<AppCompatButton>(R.id.).setOnClickListener(this)

//        errorLiveData.observe(
//            viewLifecycleOwner,
//            { errorText ->
//                showErrorToast(errorText)
//            }
//        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.owner_fragment, container, false)
        return view
    }

    companion object {
        fun newInstance() = OwnerFragment()
    }

    inner class OwnerAdapter(private val p_ownerResponse: OwnerResponse) :
        RecyclerView.Adapter<OwnerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.owner_fragment_rv__item,
                parent, false)
            return OwnerViewHolder(view)

        }

        override fun getItemCount(): Int {
            return p_ownerResponse.data.organisationRealmEntities.size
        }

        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
            val organisationEntityRealms = p_ownerResponse.data.organisationRealmEntities[position]
            holder.tv.text = organisationEntityRealms.name
            holder.llc.rootView.setOnClickListener{
               showNextFra(organisationEntityRealms.id)
            }
//            holder.llc.animation =
//                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in_left)
//            setAnimation(holder.itemView, position)
        }

        }

    private fun showNextFra(ownerId: Int) {
        val config = ConfigEntityRealm(name="Owner", value=ownerId.toString())
        db().saveConfig(config)
        CatalogRequestRPC().callAsyncRPC(ownerId)
        showFragment(MapFragment.newInstance())
    }

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tv = itemView.findViewById<AppCompatTextView>(R.id.aptv_owner_fragment_rv__item)
        val llc = itemView.findViewById<LinearLayoutCompat>(R.id.llc_owner_fragment_rv__item)
    }

    class OwnerViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }
}