package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.crowncement.TsSalesTracking.data.Model.DealerChild
import com.crowncement.TsSalesTracking.databinding.DealerInfoItemBinding


class DealerListAdapter(private val dealerList: ArrayList<DealerChild>, private val type: String) :

    RecyclerView.Adapter<DealerListAdapter.ViewHolder>() {

    var listSize: ArrayList<DealerChild> = java.util.ArrayList()

    private val TAG = "DealerListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnClick(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.dealer_info_item, parent, false)
//
//        return ViewHolder(view, onClickListener)
//
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DealerInfoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onClickListener)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val dealerInfo = dealerList[position]


        saveFieldValue(holder.binding.txtDealerName, dealerInfo.dealerName.toString())

        saveFieldValue(holder.binding.txtDealerTypeName, type)

        saveFieldValue(holder.binding.txtDealerMobile, dealerInfo.dealerMobile.toString())

        saveFieldValue(holder.binding.txtDealerAddress, dealerInfo.dealerAdd1.toString())

    }

    private fun saveFieldValue(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return dealerList.size
    }


    class ViewHolder(val binding: DealerInfoItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.layoutDealer.setOnClickListener {
                listener.OnClick(binding.root, adapterPosition)
            }
        }
    }

}