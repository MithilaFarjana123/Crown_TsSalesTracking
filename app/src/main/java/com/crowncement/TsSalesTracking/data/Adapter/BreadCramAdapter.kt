package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.crowncement.TsSalesTracking.databinding.BreadcramItemBinding


class BreadCramAdapter(private val breadCramValues: ArrayList<String>) :

    RecyclerView.Adapter<BreadCramAdapter.ViewHolder>() {

    var listSize: ArrayList<String> = java.util.ArrayList()

 //   private val TAG = "BreadCramAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context

    var row_index = -1


    interface OnAdapterItemClickListener {
        fun OnClick(v: android.view.View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val binding = BreadcramItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val breadCrum = breadCramValues[position] //2022-12-11


        saveFieldValue(holder.binding.txtBreadEmp, breadCrum)

        holder.binding.root.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
            onClickListener.OnClick(holder.binding.root, position)
        }


        if (row_index == position) {
            holder.binding.layoutBreadBG.setBackgroundColor(Color.parseColor("#00914C"))
            holder.binding.txtBreadEmp.setTextColor(Color.parseColor("#ffffff"))

        } else {
            holder.binding.layoutBreadBG.setBackgroundColor(Color.parseColor("#ffffff"))
            holder.binding.txtBreadEmp.setTextColor(Color.parseColor("#000000"))
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
        return breadCramValues.size
    }


    class ViewHolder(val binding: BreadcramItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

    }
}