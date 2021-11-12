package ru.smartro.inventory.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.smartro.inventory.R

import ru.smartro.inventory.base.AbstractFragment
import ru.smartro.inventory.base.RestClient
import ru.smartro.inventory.core.OwnerRequest


class OwnerFragment : AbstractFragment(), View.OnClickListener {

    companion object {
        fun newInstance() = OwnerFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.owner_fragment, container, false)
        return view
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
            { ownerEntityList ->
                for (ownerEntity in ownerEntityList) {
                    log.info("AAA", ownerEntity.name)
                }
//                recyclerView.adapter = OwnerAdapter(ownerEntityList)
            }
        )
        // TODO: Use the ViewModel
//        view.findViewById<AppCompatButton>(R.id.).setOnClickListener(this)
    }

    override fun onClick(v: View?) {

    }

//    class OwnerAdapter(private val ownerEntityList: OwnerEntityList) :
//        RecyclerView.Adapter<OwnerAdapter.OwnerViewHolder>() {
//        private var checkedPosition = -1
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
//            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_choose, parent, false)
//            return OwnerViewHolder(view)
//        }
//
//        override fun getItemCount(): Int {
//            return items.size
//        }
//
//        override fun onBindViewHolder(holder: OwnerViewHolder, position: Int) {
//            val organisation = items[position]
//
//            if (checkedPosition == -1){
//                holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
//                holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
//            }else{
//                if (checkedPosition == holder.adapterPosition) {
//                    holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
//                    holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
//                } else {
//                    holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
//                    holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
//                }
//            }
//
//            holder.itemView.choose_title.text = organisation.name
//            holder.itemView.setOnClickListener {
//                holder.itemView.choose_cardview.isVisible = true
//                if (checkedPosition != holder.adapterPosition){
//                    holder.itemView.choose_cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
//                    holder.itemView.choose_title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
//                    notifyItemChanged(checkedPosition)
//                    checkedPosition = holder.adapterPosition
//                }
//            }
//        }
//
//        fun getSelectedId () : Int{
//            return if (checkedPosition != -1){
//                items[checkedPosition].id
//            }else{
//                -1
//            }
//
//        }
//
//        class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        }

}