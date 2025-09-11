package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.data.Model.GetEmpEntSumResponceChild
import com.crowncement.TsSalesTracking.databinding.ItemEmpWorkSummeryBinding

class EmployeesWorkSummeryItemAdapter(private val empWorkSummery: ArrayList<GetEmpEntSumResponceChild>) :
    RecyclerView.Adapter<EmployeesWorkSummeryItemAdapter.ViewHolder>() {


    var listSize: ArrayList<GetEmpEntSumResponceChild> = ArrayList()
    private lateinit var onClickListener:OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {

        fun onClickItem(v: View?, position: Int)

    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmployeesWorkSummeryItemAdapter.ViewHolder {
        val binding = ItemEmpWorkSummeryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)

    }



    override fun getItemCount(): Int {
        return empWorkSummery.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mContext = holder.itemView.context
        val empWorkSummery = empWorkSummery[position]

        holder.binding.SiteNo.setText(empWorkSummery.noOfSite.toString())
        holder.binding.MasonNo.setText(empWorkSummery.noOfMason.toString())
        holder.binding.ContractorNo.setText(empWorkSummery.noOfContractor.toString())
        holder.binding.engineerNo.setText(empWorkSummery.noOfEngineer.toString())



    }




    class ViewHolder(
        val binding: ItemEmpWorkSummeryBinding,
        listener: OnAdapterItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                listener.onClickItem(itemView, adapterPosition)
            }
        }

    }
    

}