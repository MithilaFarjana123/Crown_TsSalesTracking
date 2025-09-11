package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.OtherCustomerChild
import com.crowncement.TsSalesTracking.databinding.OtherCustomerInfoItemBinding


class OtherCustomerListAdapter(
    private val otherList: ArrayList<OtherCustomerChild>,
    private val type: String
) :

    RecyclerView.Adapter<OtherCustomerListAdapter.ViewHolder>() {

    var listSize: ArrayList<OtherCustomerChild> = java.util.ArrayList()

    private val TAG = "OtherCustomerList"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnClick(v: View?, position: Int)
      //  fun OnEngineerClick(v: View?, position: Int)
      //  fun OnContractorClick(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use ViewBinding to inflate the layout
        val binding = OtherCustomerInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val otherInfo = otherList[position]

        if(type.equals("Contractor")||type.equals("Engineer")||type.equals("Local Government Contractor")){
            saveFieldValue(holder.binding.txtOtherName, otherInfo.contactPerson.toString()+" | "+otherInfo.partyPhone.toString())
            saveFieldValue(holder.binding.txtOtherMobile, otherInfo.partyName+" | "+otherInfo.partyMobile.toString())
            //added new
            holder.binding.txtOtherTypeName.visibility = View.GONE
            holder.binding.txtOtherTypeImg.visibility = View.VISIBLE

            Glide.with(mContext).load(EndPoint.IMAGE_BASE_URL + otherInfo.partyImage)
                .error(R.drawable.ic_user).fitCenter()
                // .placeholder(R.drawable.shop_banner)
                .into(holder.binding.txtOtherTypeImg)

        }else{
            saveFieldValue(holder.binding.txtOtherMobile, otherInfo.contactPerson+" | "+otherInfo.partyPhone.toString())
            saveFieldValue(holder.binding.txtOtherName, otherInfo.partyName.toString()+" | "+otherInfo.partyMobile.toString() )
           //added new
            holder.binding.txtOtherTypeName.visibility = View.VISIBLE
            holder.binding.txtOtherTypeImg.visibility = View.GONE


        }



        saveFieldValue(holder.binding.txtOtherTypeName, type.toCharArray()[0].toString())

      //  saveFieldValue(holder.itemView.txtOtherAddress, otherInfo.geoHrc4.toString()+", "+otherInfo.geoHrc3.toString()+", "+otherInfo.geoHrc2.toString())
        saveFieldValue(holder.binding.txtOtherAddress, otherInfo.partyAdd1.toString()+","+otherInfo.geoHrc4.toString()+", "+otherInfo.geoHrc3.toString())


    }

    private fun saveFieldValue(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return otherList.size
    }


    class ViewHolder(val binding: OtherCustomerInfoItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.layoutOther.setOnClickListener {
                listener.OnClick(itemView, adapterPosition)
               // listener.OnEngineerClick(itemView, adapterPosition)
              //  listener.OnContractorClick(itemView, adapterPosition)

            }
        }
    }
}