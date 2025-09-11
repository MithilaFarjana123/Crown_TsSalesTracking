package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.common.Utility.changeDateFormat

import com.crowncement.TsSalesTracking.data.Model.MovementPlanChild1
import com.crowncement.TsSalesTracking.databinding.MovApproveChildItemOneBinding


class MovementAppChildOneAdapter(private val movementDateList: ArrayList<MovementPlanChild1>, private val planStatus:String) :
    RecyclerView.Adapter<MovementAppChildOneAdapter.ViewHolder>() {

    var listSize: ArrayList<String> = java.util.ArrayList()

    private val TAG = "MovementAppChildOneAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnClick(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = MovApproveChildItemOneBinding.inflate(
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

        val dateInfo = movementDateList[position]

        val date = changeDateFormat(dateInfo.plnDate.toString(), "yyyy-MM-dd", "EEE-MMM.dd.yyyy")
            .toString().split("-")

        setFieldValue(holder.binding.txtDateOfMovementDay, date[0])
        setFieldValue(holder.binding.txtDateOfMovement, date[1])


        val itemAdapter = MovementAppChildTwoAdapter(dateInfo.routeDetails,planStatus)
        val manager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        holder.binding.rdMovAp.layoutManager = manager
        holder.binding.rdMovAp.adapter = itemAdapter


        itemAdapter.setOnItemClickListener(object :
            MovementAppChildTwoAdapter.OnAdapterItemClickListener {
            override fun OnClick(v: View?, position: Int) {}})


    }

    private fun setFieldValue(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return movementDateList.size
    }


    class ViewHolder(val binding: MovApproveChildItemOneBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        init {

            binding.root.setOnClickListener {
                listener.OnClick(binding.root, adapterPosition)
            }
        }
    }
}
