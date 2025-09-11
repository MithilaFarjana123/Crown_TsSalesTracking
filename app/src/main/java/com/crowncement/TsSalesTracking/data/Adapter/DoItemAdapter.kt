package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R

import com.crowncement.TsSalesTracking.data.Model.DOInfo
import com.crowncement.TsSalesTracking.databinding.DoItemInfoBinding

class DoItemAdapter(private val productInfo: ArrayList<DOInfo>) :

    RecyclerView.Adapter<DoItemAdapter.ViewHolder>() {

    var listSize: ArrayList<DOInfo> = java.util.ArrayList()

    private val TAG = "DoItemAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun onSelectItem(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = DoItemInfoBinding.inflate(
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

        holder.binding.txtDoNo.text = itemInfo.do_number
        holder.binding.txtCountNo.text = (position+1).toString()+"/"+productInfo.size
        if (itemInfo.item_name=="PC") {
            holder.binding.imgProductImage.setImageResource(R.drawable.pc)
        } else {
            holder.binding.imgProductImage.setImageResource(R.drawable.pcc)
        }
        holder.binding.txtProductName.text = "Product Name: "+itemInfo.item_name

        holder.binding.txtQuantity.text = "Available Quantity: "+ itemInfo.stock_quantity+" Bags"
    }

    override fun getItemCount(): Int {
        return productInfo.size
    }

    class ViewHolder(val binding: DoItemInfoBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Use the binding object to set up the click listener
            binding.btnAdd.setOnClickListener {
                listener.onSelectItem(binding.root, adapterPosition)
            }
        }
    }

}