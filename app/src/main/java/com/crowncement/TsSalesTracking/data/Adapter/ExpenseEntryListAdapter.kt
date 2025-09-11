package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.ExpenseClaimChild
import com.crowncement.TsSalesTracking.databinding.TourExpenseClaimItemBinding


class ExpenseEntryListAdapter(private val expenseList: ArrayList<ExpenseClaimChild>) :

    RecyclerView.Adapter<ExpenseEntryListAdapter.ViewHolder>() {

    var listSize: ArrayList<ExpenseClaimChild> = java.util.ArrayList()

    private val TAG = "ExpenseEntryListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {

        fun showDocument(v: View?, position: Int)

    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = TourExpenseClaimItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding, onClickListener)
        }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val expenseItem = expenseList[position]

        //Trans No
        saveFieldValue(holder.binding.txtTourNoo,  expenseItem.trTrnNo.toString())

        //Date
        saveFieldValue(
            holder.binding.txtExpenseDate,
            " " + Utility.changeDateFormat(
                expenseItem.trDate.toString(),
                "yyyy-MM-dd",
                "EEE-MMM, dd yyyy"
            )
        )

        //expense Type
        saveFieldValue(holder.binding.txtExpenseType, expenseItem.trExpType.toString())
        //payment type
        saveFieldValue(holder.binding.txtEPayType, expenseItem.trPayTyp.toString())

        //AMOUNT
        saveFieldValue(holder.binding.txtTotalAmount, " ৳ ${expenseItem.trExpAmt.toString()} /-")


        // set this if transport

        if (expenseItem.trFrmLoc.toString() != "") {
            holder.binding.layoutTransport.visibility = View.VISIBLE
            saveFieldValue(holder.binding.txtDestiFrom, expenseItem.trFrmLoc.toString())
            saveFieldValue(holder.binding.txtDestiTo, expenseItem.trToLoc.toString())
            // saveFieldValue(holder.itemView.txtVehicle,expenseItem.trToLoc.toString())


        } else {
            holder.binding.layoutTransport.visibility = View.GONE
        }

        if (expenseItem.trImg.toString()=="") {
            holder.binding.imgDocStatus.isEnabled = false
            holder.binding.imgDocStatus.setBackgroundResource(R.drawable.ic_no_doc)

        } else {
            holder.binding.imgDocStatus.isEnabled=true
            holder.binding.imgDocStatus.setBackgroundResource(R.drawable.ic_document)
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
        return expenseList.size
    }


    class ViewHolder(val binding: TourExpenseClaimItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imgDocStatus.setOnClickListener {
                listener.showDocument(binding.root, adapterPosition)
            }
        }
    }


}