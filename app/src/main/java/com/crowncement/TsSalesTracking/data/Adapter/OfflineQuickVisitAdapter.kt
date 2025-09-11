package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.OfflineQuickVisit
import com.crowncement.TsSalesTracking.databinding.QuickVisitListItemBinding


class OfflineQuickVisitAdapter(private val offlineList: ArrayList<OfflineQuickVisit>) :
    RecyclerView.Adapter<OfflineQuickVisitAdapter.ViewHolder>() {

    var listSize: ArrayList<OfflineQuickVisit> = java.util.ArrayList()

    private val TAG = "OfflineQuickVisitListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context

    var row_index=-1
    interface OnAdapterItemClickListener {
        fun OnSelect(v: View?, position: Int)
        fun OnIconSelect(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use ViewBinding to inflate the layout
        val binding = QuickVisitListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val offlineData = offlineList[position]



        holder.binding.txtOffName.text = offlineData.ClientName + "(${offlineData.ClientType})"
        val fromDate: String =
            Utility.changeDateFormat(offlineData.CreateTime, "HH:mm:ss", "hh:mm a").toString()
        holder.binding.txtQTime.text = fromDate

        holder.binding.txtQRemarks.text =offlineData.Remarks


        holder.itemView.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
            onClickListener.OnSelect(holder.itemView, position)

        }


        holder.binding.txtOffName.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
            onClickListener.OnIconSelect(holder.itemView, position)

        }

        if (row_index == position) {
            holder.binding.OlayoutBG.setBackgroundColor(Color.parseColor("#00914C"))
            holder.binding.txtOffName.setTextColor(Color.parseColor("#ffffff"))
            holder.binding.txtQTime.setTextColor(Color.parseColor("#ffffff"))
            holder.binding.txtQRemarks.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.binding.OlayoutBG.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.binding.txtOffName.setTextColor(Color.parseColor("#000000"))
            holder.binding.txtQTime.setTextColor(Color.parseColor("#000000"))
            holder.binding.txtQRemarks.setTextColor(Color.parseColor("#000000"))
        }


    }

    override fun getItemCount(): Int {
        return offlineList.size
    }

    class ViewHolder(val binding: QuickVisitListItemBinding , listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

//        init {
//
//            itemView.setOnClickListener {
//                listener.OnSelect(itemView, adapterPosition)
//            }
//            itemView.txtOffName.setOnClickListener {
//                listener.OnIconSelect(itemView, adapterPosition)
//            }
//
//        }
    }
}