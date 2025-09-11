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
import com.crowncement.TsSalesTracking.databinding.ExpenseListDateItemBinding


class ExpenseDateListAdapter (private val dateList: ArrayList<String>) :

    RecyclerView.Adapter<ExpenseDateListAdapter.ViewHolder>() {

    var listSize: ArrayList<String> = java.util.ArrayList()

    private val TAG = "ExpenseClaimDateAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context

    var row_index=-1


    interface OnAdapterItemClickListener {
        fun OnClick(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ExpenseListDateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val dateInfo = dateList[position] //2022-12-11


        saveFieldValue(holder.binding.txtDateName, dateInfo.split("-")[2])

        val convoDate= Utility.changeDateFormat(dateInfo,"yyyy-MM-dd","MMM yyyy")

        saveFieldValue(holder.binding.txtMonthName, convoDate.toString())


        holder.itemView.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
            onClickListener.OnClick(holder.itemView, position)
        }

        if (row_index == position) {
            holder.binding.layoutBG.setBackgroundColor(Color.parseColor("#00914C"))
            holder.binding.txtDateName.setTextColor(Color.parseColor("#ffffff"))
            holder.binding.txtMonthName.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.binding.layoutBG.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.binding.txtDateName.setTextColor(Color.parseColor("#000000"))
            holder.binding.txtMonthName.setTextColor(Color.parseColor("#000000"))
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
        return dateList.size
    }


    class ViewHolder(val binding: ExpenseListDateItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

//        init {
//
//            itemView.setOnClickListener {
//                listener.OnClick(itemView, adapterPosition)
//            }
//        }
    }
}