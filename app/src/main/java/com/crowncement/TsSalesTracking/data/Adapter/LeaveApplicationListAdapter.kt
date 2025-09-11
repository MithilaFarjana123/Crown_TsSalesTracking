package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.LeaveApplication
import com.crowncement.TsSalesTracking.databinding.LeaveListItemBinding

class LeaveApplicationListAdapter(private val leaveApplicationList: ArrayList<LeaveApplication>) :
    RecyclerView.Adapter<LeaveApplicationListAdapter.ViewHolder>() {

    var listSize: ArrayList<LeaveApplication> = java.util.ArrayList()

    private val TAG = "LeaveApplicationListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnSelect(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = LeaveListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val leaveApplication = leaveApplicationList[position]

        if (leaveApplication.lvDays!! > 1) {
            holder.binding.txtTotalDays.text =
                leaveApplication.lvDays.toString() + " Days, Application"
        } else {
            holder.binding.txtTotalDays.text =
                leaveApplication.lvDays.toString() + " Day, Application"
        }




        when {
            leaveApplication.lvStat!!.contains("Open") -> {
                holder.binding.txtLeaveStatus.text = "Open"
                holder.binding.txtLeaveStatus.background =
                    ContextCompat.getDrawable(mContext, R.drawable.cream_curved)
            }

            leaveApplication.lvStat!!.contains("Canceled") -> {
                holder.binding.txtLeaveStatus.text = "Canceled"
                holder.binding.txtLeaveStatus.setTextColor(Color.parseColor("#000000"))
                holder.binding.txtLeaveStatus.background =
                    ContextCompat.getDrawable(mContext, R.drawable.orange_curved)
            }

            leaveApplication.lvStat!!.contains("Recommended") -> {
                holder.binding.txtLeaveStatus.text = "Recommended"
                holder.binding.txtLeaveStatus.background =
                    ContextCompat.getDrawable(mContext, R.drawable.gray_curved)
            }
            leaveApplication.lvStat!!.contains("Sanctioned") -> {
                holder.binding.txtLeaveStatus.text = "Sanctioned"
                holder.binding.txtLeaveStatus.setTextColor(Color.parseColor("#FFFFFF"))
                holder.binding.txtLeaveStatus.background =
                    ContextCompat.getDrawable(mContext, R.drawable.curved_green)
            }
            else -> {
                holder.binding.txtLeaveStatus.text = "Denied"
                holder.binding.txtLeaveStatus.setTextColor(Color.parseColor("#FFFFFF"))
                holder.binding.txtLeaveStatus.background =
                    ContextCompat.getDrawable(mContext, R.drawable.red_curved)

            }
        }

        val fromDate: String =
            Utility.changeDateFormat(leaveApplication.lvFdate, "yyyy-MM-dd", "EEE, dd MMM")
                .toString()
        val toDate: String =
            Utility.changeDateFormat(leaveApplication.lvTdate, "yyyy-MM-dd", "EEE, dd MMM")
                .toString()


        holder.binding.txtLeaveFromDate.text = "$fromDate - $toDate"

        holder.binding.txtReason.text = "Leave purpose: " + leaveApplication.lvPurpose

        when {
            leaveApplication.lvType.equals("CL") -> {
                holder.binding.txtLeaveType.text = "Casual"
                holder.binding.txtLeaveType.setBackgroundColor(Color.parseColor("#CFEAE6"))
            }
            leaveApplication.lvType.equals("ML") -> {
                holder.binding.txtLeaveType.text = "Medical"
                holder.binding.txtLeaveType.setBackgroundColor(Color.parseColor("#CCCCCC"))
            }
            else -> {
                holder.binding.txtLeaveType.text = "Earn"
                holder.binding.txtLeaveType.setBackgroundColor(Color.parseColor("#E4FEC7"))
            }
        }


    }

    override fun getItemCount(): Int {
        return leaveApplicationList.size
    }

    class ViewHolder(val binding: LeaveListItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Use the ViewBinding object to set up click listeners
            binding.root.setOnClickListener {
                listener.OnSelect(binding.root, adapterPosition)
            }
        }
    }

}