package ru.smartro.inventory.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.inventory.R
import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.core.OwnerRequest
import ru.smartro.inventory.database.OwnerResponse


class OwnerFragment : AbstractFragment(){
    private lateinit var viewModel: OwnerViewModel
//
    inner class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tv = itemView.findViewById<AppCompatTextView>(R.id.aptv_owner_fragment_rv__item)

        override fun onClick(p0: View?) {
//            TODO("Not yet implemented")
            showFragment(MapFragment.newInstance())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                for (ownerEntity in ownerResponse.data.organisationEntityRealms) {
                    log.info("AAA", ownerEntity.name)
                }
                recyclerView.adapter = OwnerAdapter(ownerResponse)
            }
        )
        // TODO: Use the ViewModel
//        view.findViewById<AppCompatButton>(R.id.).setOnClickListener(this)
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
            return p_ownerResponse.data.organisationEntityRealms.size
        }

        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
            val organisationEntityRealms = p_ownerResponse.data.organisationEntityRealms[position]
            holder.tv.text = organisationEntityRealms.name

            holder.tv.setOnClickListener(holder)
        }

        }

    class OwnerViewModel : ViewModel() {
        // TODO: Implement the ViewModel
    }
}