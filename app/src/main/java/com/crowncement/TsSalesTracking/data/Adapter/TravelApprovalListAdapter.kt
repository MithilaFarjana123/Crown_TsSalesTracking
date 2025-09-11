package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.TourApproveResponseChild
import com.crowncement.TsSalesTracking.databinding.TourApproveListChildBinding


class TravelApprovalListAdapter (private val approveList: ArrayList<TourApproveResponseChild>) :

    RecyclerView.Adapter<TravelApprovalListAdapter.ViewHolder>() {

    var listSize: ArrayList<TourApproveResponseChild> = java.util.ArrayList()

    private val TAG = "TravelApprovalListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {

        fun clickAccept(v: View?, position: Int)
        fun clickReject(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use ViewBinding to inflate the layout
        val binding = TourApproveListChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val approveItem = approveList[position]

        //Trans No
        saveFieldValue(holder.binding.txtTourlId, "Travel No: " + approveItem.trTrno.toString())

        //Status
        saveFieldValue(
            holder.binding.txtTourStatus,
            "Status: " + approveItem.trStat.toString().split("-")[1]
        )

        //Name
        saveFieldValue(holder.binding.txtTourEmpName, approveItem.trEmpName + "(" + approveItem.trEmp + ")")

        Glide
            .with(mContext)
            .load(approveItem.trEmpPic.toString())
            .error(R.drawable.default_user_image)
            .fitCenter()
            .placeholder(R.drawable.default_user_image)
            .into(holder.binding.imgTourApp)

        // f date
        val fDate: String =
            Utility.changeDateFormat(approveItem.trFdt, "yyyy-MM-dd", "EEE, dd MMM yyyy").toString()
        saveFieldValue(holder.binding.txtTrFDate, fDate)

        //t Date
        val tDate: String =
            Utility.changeDateFormat(approveItem.trTdt, "yyyy-MM-dd", "EEE, dd MMM yyyy").toString()

        saveFieldValue(holder.binding.txtTrTDate, tDate)

        //purpose

        saveFieldValue(holder.binding.txtTrPurpose,approveItem.trPurpose.toString())

        //tour apply
        val applyDate: String =
            Utility.changeDateFormat(
                approveItem.trEntTime,
                "yyyy-MM-dd HH:mm:ss",
                "hh:mm:ss a, EEE, dd MMM yyyy"
            ).toString()
        saveFieldValue(holder.binding.txtTrApplyDate, applyDate)

        //remarks
        saveFieldValue(holder.binding.txtTrRemarks, approveItem.trRem.toString())
    }

    private fun saveFieldValue(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return approveList.size
    }


    class ViewHolder(val binding: TourApproveListChildBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        var isSidePanelShown: Boolean = false

        init {

            binding.txtTrMore.setOnClickListener {

                val transition: Transition = Slide(Gravity.TOP)
                transition.duration = 600
                transition.addTarget(binding.layoutTrMore)
                TransitionManager.beginDelayedTransition(
                    binding.childContainer, transition
                )
                binding.layoutTrMore.visibility = if (!isSidePanelShown) View.VISIBLE else View.GONE
                isSidePanelShown = !isSidePanelShown

            }

            binding.imgTrReject.setOnClickListener {
                listener.clickReject(binding.root, adapterPosition)
            }


            binding.imgTrApprove.setOnClickListener {
                listener.clickAccept(binding.root, adapterPosition)
            }
        }
    }

}