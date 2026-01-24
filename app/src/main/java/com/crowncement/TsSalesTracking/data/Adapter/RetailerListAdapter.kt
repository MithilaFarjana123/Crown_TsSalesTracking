package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.crowncement.TsSalesTracking.data.Model.RetailerChild
import com.crowncement.TsSalesTracking.databinding.RetailerInfoItemBinding

class RetailerListAdapter(private val retailerList: ArrayList<RetailerChild>, private val type:String) :

    RecyclerView.Adapter<RetailerListAdapter.ViewHolder>() {

    var listSize: ArrayList<RetailerChild> = java.util.ArrayList()

    private val TAG = "RetailerListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnClick(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use ViewBinding to inflate the layout
        val binding = RetailerInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val retailerInfo = retailerList[position]

// Shop name
        saveFieldValue(holder.binding.txtRetailerName, retailerInfo.partyName.toString())
//Proprietor
        saveFieldValue(holder.binding.txtRetailerOwner, "Proprietor: " +retailerInfo.ownerName.toString())

        //LEFT type name
        //todo need to add image
        //     saveFieldValue(holder.itemView.txtTypeName, type)

//MOBILE
        saveFieldValue(holder.binding.txtRetailerMobile, retailerInfo.partyMobile.toString())

        //Address
        saveFieldValue(
            // holder.itemView.txtRetailerAddress, retailerInfo.marketPoint.toString() + ", " + retailerInfo.geoHrc4.toString() + ", " + retailerInfo.geoHrc3.toString() + ", " + retailerInfo.geoHrc2.toString())
            holder.binding.txtRetailerAddress,retailerInfo.partyAdd1.toString() + ", " + retailerInfo.geoHrc4.toString() + ", " + retailerInfo.geoHrc3.toString() )

    }

    private fun saveFieldValue(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return retailerList.size
    }


    class ViewHolder(val binding: RetailerInfoItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.layoutRetailer.setOnClickListener {
                listener.OnClick(itemView, adapterPosition)
            }
        }
    }
}