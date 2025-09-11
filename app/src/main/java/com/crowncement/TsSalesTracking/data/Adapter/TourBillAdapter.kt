package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.TourExpenseResponseChild
import com.crowncement.TsSalesTracking.databinding.BillListChildBinding

class TourBillAdapter (private val billList: ArrayList<TourExpenseResponseChild>) :

    RecyclerView.Adapter<TourBillAdapter.ViewHolder>() {

    var listSize: ArrayList<TourExpenseResponseChild> = java.util.ArrayList()

    private val TAG = "TourBillListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnClick(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = BillListChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val billInfo = billList[position]

        saveFieldValue(holder.binding.txtTourInvNo, billInfo.tourNo.toString())

        val convFrom= Utility.changeDateFormat(billInfo.fromDate.toString(),"yyyy-MM-dd","EEE, MMM dd yyyy")
        val convTo=Utility.changeDateFormat(billInfo.toDate.toString(),"yyyy-MM-dd","EEE, MMM dd yyyy")

        saveFieldValue(holder.binding.txtBFrom,convFrom.toString() )
        saveFieldValue(holder.binding.txtBTo, convTo.toString())
        val convKM= (billInfo.totDist ?: 0.0) /1000
        saveFieldValue(holder.binding.txtBDistance, "$convKM KM")
        saveFieldValue(holder.binding.txtBAmount,"৳ "+ billInfo.totAmt.toString())

        if(billInfo.tourNo.toString().contains("TRV")){
            holder.binding.layoutTitle.setBackgroundColor(Color.parseColor("#FF018786"))
            holder.binding.txtTourInvNo.setTextColor(Color.parseColor("#FFFFFF"))
        }
        else{
            holder.binding.layoutTitle.setBackgroundColor(Color.parseColor("#00914C"))
            holder.binding.txtTourInvNo.setTextColor(Color.parseColor("#FFFFFF"))
        }

    }

    private fun saveFieldValue(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return billList.size
    }


    class ViewHolder(val binding: BillListChildBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.OnClick(binding.root, adapterPosition)
            }
        }
    }
}