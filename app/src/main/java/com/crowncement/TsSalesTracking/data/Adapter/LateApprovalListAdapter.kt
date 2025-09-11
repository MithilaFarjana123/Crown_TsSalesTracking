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
import com.crowncement.TsSalesTracking.common.Utility.changeDateFormat
import com.crowncement.TsSalesTracking.data.Model.LateApproveChild
import com.crowncement.TsSalesTracking.databinding.LateApproveListChildBinding

class LateApprovalListAdapter(private val LateApprovalList: ArrayList<LateApproveChild>) :

    RecyclerView.Adapter<LateApprovalListAdapter.ViewHolder>() {

    var listSize: ArrayList<LateApproveChild> = java.util.ArrayList()

    private val TAG = "LateApprovalListAdapter"

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

        val binding = LateApproveListChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val approveItem = LateApprovalList[position]

        //Trans No
        saveFieldValue(holder.binding.txtLateId, "Late No: " + approveItem.ltTrno.toString())

        //Status
        saveFieldValue(
            holder.binding.txtLateStatus,
            "Status: " + approveItem.ltStat.toString().split("-")[1]
        )

        //Name
        saveFieldValue(
            holder.binding.txtLateEmpName,
            approveItem.ltEmpName + "(" + approveItem.ltEmp + ")"
        )

        Glide
            .with(mContext)
            .load(approveItem.ltEmpPic.toString())
            .error(R.drawable.default_user_image)
            .fitCenter()
            .placeholder(R.drawable.default_user_image)
            .into(holder.binding.imgLateApp)

        // Late date
        val time: String =
            changeDateFormat(approveItem.ltIntime, "HH:mm:ss", "hh:mm:ss a").toString()
        val date: String =
            changeDateFormat(approveItem.ltDt, "yyyy-MM-dd", "EEE, dd MMM yyyy").toString()
        saveFieldValue(holder.binding.txtLaDate, "$time | $date")


        //late apply
        val lateApply: String =
            changeDateFormat(
                approveItem.ltEntTime,
                "yyyy-MM-dd HH:mm:ss",
                "hh:mm:ss a, EEE, dd MMM yyyy"
            ).toString()
        saveFieldValue(holder.binding.txtLApplyDate, lateApply)

        //purpose
        saveFieldValue(holder.binding.txtLateReason, approveItem.ltPurpose.toString())

        //remarks
        saveFieldValue(holder.binding.txtLateRemarks, approveItem.ltRem.toString())

    }

    private fun saveFieldValue(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return LateApprovalList.size
    }


    class ViewHolder(val binding: LateApproveListChildBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        var isSidePanelShown: Boolean = false

        init {

            binding.txtMore.setOnClickListener {

                val transition: Transition = Slide(Gravity.TOP)
                transition.duration = 600
                transition.addTarget(binding.layoutMore)
                TransitionManager.beginDelayedTransition(binding.childContainer, transition)
                binding.layoutMore.visibility = if (!isSidePanelShown) View.VISIBLE else View.GONE
                isSidePanelShown = !isSidePanelShown

            }

            binding.imgLateReject.setOnClickListener {
                listener.clickReject(binding.root, adapterPosition)
            }

            binding.imgLateApprove.setOnClickListener {
                listener.clickAccept(binding.root, adapterPosition)
            }
        }
    }


}