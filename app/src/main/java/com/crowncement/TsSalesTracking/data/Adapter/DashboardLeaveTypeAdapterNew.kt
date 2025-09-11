package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.data.Model.LeaveTypeSummaryChild
import com.crowncement.TsSalesTracking.databinding.DashboardNewVisitSummeryItemBinding

class DashboardLeaveTypeAdapterNew (private val leaveTypeList: ArrayList<LeaveTypeSummaryChild>) :

    RecyclerView.Adapter<DashboardLeaveTypeAdapterNew.ViewHolder>() {

    var listSize: ArrayList<LeaveTypeSummaryChild> = java.util.ArrayList()


    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context

    interface OnAdapterItemClickListener {
        fun onSelectedLeaveType(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.dashboard_new_visit_summery_item, parent, false)
//
//        return ViewHolder(view, onClickListener)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DashboardNewVisitSummeryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onClickListener)
    }


    override fun getItemCount(): Int {
        return leaveTypeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mContext = holder.itemView.context
        val leave = leaveTypeList[position]

        when (leave.lvCode) {
            "CL" -> {
                holder.binding.lvType.text = mContext.getString(R.string.casual)
            }
            "ML" -> {
                holder.binding.lvType.text = mContext.getString(R.string.medical)
            }
            "EL" -> {
                holder.binding.lvType.text = mContext.getString(R.string.earn)
            }
            else -> {
                holder.binding.lvType.text = mContext.getString(R.string.alternative)
            }
        }

        holder.binding.lvRemaining.text =
            (leave.lvDue.toString().toInt() - leave.lvTkn.toString().toInt()).toString()
        //            (leave.lvAlc.toString().toInt() - leave.lvTkn.toString().toInt()).toString()
        holder.binding.lvEnjoyed.text = leave.lvTkn.toString()


    }


    class ViewHolder(val binding: DashboardNewVisitSummeryItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Use the binding object to set up click listeners
            binding.layoutItemNew.setOnClickListener {
                listener.onSelectedLeaveType(binding.root, adapterPosition)
            }
        }
    }



}


