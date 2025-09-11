package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.data.Model.HolidayResponseChild
import com.crowncement.TsSalesTracking.databinding.HolidaysItemBinding

class HolidayAdapter (private val holidayInfo: ArrayList<HolidayResponseChild>) :

    RecyclerView.Adapter<HolidayAdapter.ViewHolder>() {

    var listSize: ArrayList<HolidayResponseChild> = java.util.ArrayList()

    private val TAG = "HolidayAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener



    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun onSelectItem(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = HolidaysItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(
            binding,
            onClickListener
        )


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context
        val info = holidayInfo[position]

        holder.binding.txtHolidayDate.text = info.HolidayDate!!.split("-")[2]
        holder.binding.txtHolidayInfo.text = info.HolidayDesc
        holder.binding.day.text = info.DayName

        when (info.DayName) {
            "Saturday" -> {
                holder.binding.txtHolidayInfo.text="Alternative Holiday"
                holder.binding.viewItemSide.setBackgroundColor(android.graphics.Color.parseColor("#F8D786"))
            }
            "Friday" -> {

                holder.binding.viewItemSide.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"))
                holder.itemView.visibility=View.GONE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            }
            else -> {
                holder.binding.viewItemSide.setBackgroundColor(android.graphics.Color.parseColor("#DC645B"))
            }
        }

    }


    override fun getItemCount(): Int {
        return holidayInfo.size
    }



    class ViewHolder(val binding: HolidaysItemBinding, listener: OnAdapterItemClickListener) : RecyclerView.ViewHolder(binding.root) {


            init {

                itemView.setOnClickListener {
                    listener.onSelectItem(itemView, adapterPosition)
                }
            }
        }

}