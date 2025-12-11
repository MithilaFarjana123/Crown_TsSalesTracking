package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.data.API.EndPoint.IMAGE_BASE_URL
import com.crowncement.TsSalesTracking.data.Model.EmpInfo
import com.crowncement.TsSalesTracking.databinding.EmpInfoItemBinding

class EmployeeWorkAdapter (private val attList: ArrayList<EmpInfo>) :
    RecyclerView.Adapter<EmployeeWorkAdapter.ViewHolder>() {

    var listSize: ArrayList<EmpInfo> = ArrayList()
    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context

    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }

    interface OnAdapterItemClickListener {
        fun onClickItem(v: View?, position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = EmpInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mContext = holder.itemView.context
        val attInfo = attList[position]

        Glide
            .with(mContext)
            .load(IMAGE_BASE_URL + attInfo.empImg.toString())
            .error(R.drawable.default_user_image)
            .fitCenter()
            .placeholder(R.drawable.default_user_image)
            .into(holder.binding.imgEmp)


        holder.binding.txtEmpN.setText(attInfo.empName.toString())
        holder.binding.txtEmpI.setText(attInfo.empId.toString())


    }


    override fun getItemCount(): Int {
        return attList.size
    }


    class ViewHolder(val binding: EmpInfoItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                listener.onClickItem(itemView, adapterPosition)
            }
        }

    }


}