package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.crowncement.TsSalesTracking.data.Model.DOInfo
import com.crowncement.TsSalesTracking.databinding.DoAddedItemBinding


class DoAddedItemAdapter (private val productInfo: ArrayList<DOInfo>) :

    RecyclerView.Adapter<DoAddedItemAdapter.ViewHolder>() {

    var listSize: ArrayList<DOInfo> = java.util.ArrayList()

    private val TAG = "DoAddedItemAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun onSelectItem(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.do_added_item, parent, false)
        val binding = DoAddedItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onClickListener)

    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context
        val itemInfo = productInfo[position]

        holder.binding.txtPName.text = itemInfo.item_name
        holder.binding.txtPQty.text = itemInfo.select_quantity.toString()
        holder.binding.txtPDNo.text = itemInfo.do_number
        holder.binding.txtDelFro.text = itemInfo.delivery_from_address
        holder.binding.txtDeliTo.text = itemInfo.delivery_to_address
    }


    override fun getItemCount(): Int {
        return productInfo.size
    }

    class ViewHolder(val binding: DoAddedItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Use the binding object to set up click listeners
            binding.root.setOnClickListener {
                listener.onSelectItem(binding.root, adapterPosition)
            }
        }
    }

}