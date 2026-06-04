package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R

import com.crowncement.TsSalesTracking.data.Model.MovementPlanChild2
import com.crowncement.TsSalesTracking.databinding.MovApproveChildItemTwoBinding

class MovementAppChildTwoAdapter(
    private val routeInfoList: ArrayList<MovementPlanChild2>,
    private val planStatus: String
) :
    RecyclerView.Adapter<MovementAppChildTwoAdapter.ViewHolder>() {

    var listSize: ArrayList<String> = java.util.ArrayList()

    private val TAG = "MovementAppChildTwoAdapter"


    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnClick(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = MovApproveChildItemTwoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        // Return the ViewHolder with ViewBinding
        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val routeInfo = routeInfoList[position]

        setFieldValue(holder.binding.txtRouteInfo, routeInfo.rtHrc5.toString())
        setFieldValue(holder.binding.txtRouteInfoDe, routeInfo.rtLong.toString())

        if (planStatus != "Approved") {
            holder.binding.imgApStat.setBackgroundResource(R.drawable.ic_error)
        } else {
            holder.binding.imgApStat.setBackgroundResource(R.drawable.ic_checked_green)
        }

    }

    private fun setFieldValue(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return routeInfoList.size
    }


    class ViewHolder(val binding: MovApproveChildItemTwoBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Use binding.root to set click listener
            binding.root.setOnClickListener {
                listener.OnClick(binding.root, adapterPosition)
            }
        }
    }

}

