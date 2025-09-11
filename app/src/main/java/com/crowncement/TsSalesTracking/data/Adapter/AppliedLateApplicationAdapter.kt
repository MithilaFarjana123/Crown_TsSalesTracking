package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.LateAppliedChild
import com.crowncement.TsSalesTracking.databinding.LateAppliedListItemBinding

class AppliedLateApplicationAdapter(private val lateAppliedList: ArrayList<LateAppliedChild>) :
    RecyclerView.Adapter<AppliedLateApplicationAdapter.ViewHolder>() {

    var listSize: ArrayList<LateAppliedChild> = ArrayList()
    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context

    interface OnAdapterItemClickListener {
        fun OnClickApply(v: android.view.View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LateAppliedListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mContext = holder.itemView.context
        val applied = lateAppliedList[position]

        // Set late application details
        holder.binding.txtLateNo.text = applied.lt_trno

        val lateDate = Utility.changeDateFormat(applied.lt_date, "yyyy-MM-dd", "EEE, dd MMM yyyy").toString()
        holder.binding.txtActualLateDate.text = "Late Date: $lateDate"

        holder.binding.txtAppPurpose.text = applied.lt_purpose

        val lateAppliedDate = Utility.changeDateFormat(applied.lt_ap_dt, "yyyy-MM-dd", "EEE, dd MMM yyyy").toString()
        holder.binding.txtAppApplied.text = lateAppliedDate
        holder.binding.txtAppStatus.text = applied.lt_stat

        // Change status view color based on the status
        if (applied.lt_stat?.contains("Open") == true) {
            holder.binding.viewStatus.setBackgroundColor(Color.parseColor("#CCCCCC"))
        } else {
            holder.binding.viewStatus.setBackgroundColor(Color.parseColor("#00914C"))
        }

        holder.binding.txtLateAppAuth.text = applied.lt_hrcpath

        // Set pending authorization or hide the view if not applicable
        if (applied.lt_pending_to!!.isEmpty()) {
            holder.binding.layoutPendingAuth.visibility = android.view.View.GONE
            holder.binding.txtActualLateDate.setBackgroundColor(Color.parseColor("#00914C"))
        } else {
            holder.binding.txtPendingTo.text = applied.lt_pending_to
        }
    }

    override fun getItemCount(): Int {
        return lateAppliedList.size
    }

    class ViewHolder(val binding: LateAppliedListItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                listener.OnClickApply(itemView, adapterPosition)
            }
        }
    }
}
