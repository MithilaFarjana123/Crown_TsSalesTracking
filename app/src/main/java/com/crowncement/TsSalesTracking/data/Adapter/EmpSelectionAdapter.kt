package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.EmpInfo
import com.crowncement.TsSalesTracking.databinding.EmpInfoItemBinding


class EmpSelectionAdapter (private val empList: ArrayList<EmpInfo>) : RecyclerView.Adapter<EmpSelectionAdapter.ViewHolder>() {

    var listSize: ArrayList<String> = java.util.ArrayList()

    private val TAG = "EmpSelectionAdapter"

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

        val binding = EmpInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val empInfo = empList[position]


        saveFieldValue(holder.binding.txtEmpN, empInfo.empName.toString())

        saveFieldValue(holder.binding.txtEmpI, empInfo.empId.toString())

        if (empInfo.empImg != "") {
            Glide
                .with(mContext)
                .load(EndPoint.IMAGE_BASE_URL + empInfo.empImg)
                .error(R.drawable.default_user_image)
                .fitCenter()
                .placeholder(R.drawable.default_user_image)
                .into(holder.binding.imgEmp)
        }else{
            Glide
                .with(mContext)
                .load(R.drawable.default_user_image)
                .fitCenter()
                .into(holder.binding.imgEmp)
        }


        holder.itemView.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
            onClickListener.OnClick(holder.itemView, position)
        }

        if (row_index == position) {
            holder.binding.layoutEmp.setBackgroundColor(Color.parseColor("#00914C"))
            holder.binding.txtEmpN.setTextColor(Color.parseColor("#ffffff"))
            holder.binding.txtEmpI.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.binding.layoutEmp.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.binding.txtEmpN.setTextColor(Color.parseColor("#000000"))
            holder.binding.txtEmpI.setTextColor(Color.parseColor("#000000"))
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
        return empList.size
    }


    class ViewHolder(val binding: EmpInfoItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Set up click listeners or any other initialization using binding
            binding.root.setOnClickListener {
                listener.OnClick(binding.root, adapterPosition)
            }
        }
    }


//    class ViewHolder(ItemView: EmpInfoItemBinding, listener: OnAdapterItemClickListener) :
//        RecyclerView.ViewHolder(ItemView) {
//
//    }



}