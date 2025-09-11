package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
import com.crowncement.TsSalesTracking.data.Model.LeaveApproveChild
import com.crowncement.TsSalesTracking.databinding.LeaveApprovalListItemBinding


class LeaveApplicationApproveAdapter(private val applicationList: ArrayList<LeaveApproveChild>) :

    RecyclerView.Adapter<LeaveApplicationApproveAdapter.ViewHolder>() {

    var listSize: ArrayList<LeaveApproveChild> = java.util.ArrayList()

    private val TAG = "LeaveApplicationApproveAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context



    // method for filtering our recyclerview items.
//    fun filterList(filterList: ArrayList<LeaveApproveChild>) {
//
//        listSize = filterList
//
//        notifyDataSetChanged()
//    }


    interface OnAdapterItemClickListener {
        fun OnClickCall(v: View?, position: Int)
        fun OnAccept(v: View?, position: Int)
        fun OnReject(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = LeaveApprovalListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // Return the ViewHolder with the ViewBinding and the click listener
        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val applicationInfo = applicationList[position]

        //Trans No
        setTextToUI(holder.binding.txtApplicationNo, applicationInfo.lvTrno.toString())
        //Status
        setTextToUI(
            holder.binding.txtApplicationStatus,
            applicationInfo.lvStat.toString().split("-")[1]
        )
        // Leave from
        val from: String =
            Utility.changeDateFormat(applicationInfo.lvFdate, "yyyy-MM-dd", "EEE, dd MMM yyyy")
                .toString()
        setTextToUI(holder.binding.txtLeaveDate, " $from")
        //Leave To
        val to: String =
            Utility.changeDateFormat(applicationInfo.lvTdate, "yyyy-MM-dd", "EEE, dd MMM yyyy")
                .toString()
        setTextToUI(holder.binding.txtLeaveToDate, " $to")

        //Name
        setTextToUI(
            holder.binding.txtLeaveApplicant,
            applicationInfo.lvEmpName + "(" + applicationInfo.lvEmp + ")"
        )
        //Application date
        val appDate: String =
            Utility.changeDateFormat(
                applicationInfo.lvEntTime,
                "yyyy-MM-dd HH:mm:ss",
                "hh:mm:ss a, EEE, dd MMM yy"
            )
                .toString()
        setTextToUI(holder.binding.txtLeaveApplicationDate, "Application Date:  $appDate")
        //count days

        if (applicationInfo.lvDays!!.toInt() > 1) {
            setTextToUI(
                holder.binding.txtLeaveDays,
                applicationInfo.lvDays.toString().split(".")[0] + " Day's leave"
            )
        } else {
            setTextToUI(
                holder.binding.txtLeaveDays,
                applicationInfo.lvDays.toString().split(".")[0] + " Day leave"
            )
        }

        //change background color -->application type wise
        when {
            applicationInfo.lvType.toString() == "ML" -> {
                setTextToUI(holder.binding.txtApplicationType, "Medical")
                holder.binding.txtApplicationType.setBackgroundResource(R.drawable.curved_green_light)
                holder.binding.txtApplicationType.setTextColor(Color.BLACK)
              //  holder.itemView.leaveAuthLayout.setBackgroundColor(Color.parseColor("#EFFBE1"))
            }
            applicationInfo.lvType.toString() == "CL" -> {
                setTextToUI(holder.binding.txtApplicationType, "Casual")
                holder.binding.txtApplicationType.setBackgroundResource(R.drawable.blue_light_curved)
                holder.binding.txtApplicationType.setTextColor(Color.BLACK)
              // holder.itemView.leaveAuthLayout.setBackgroundColor(Color.parseColor("#E9F5FF"))
            }
            applicationInfo.lvType.toString() == "EL" -> {
                setTextToUI(holder.binding.txtApplicationType, "Earn")
                holder.binding.txtApplicationType.setBackgroundResource(R.drawable.gray_curved)
                holder.binding.txtApplicationType.setTextColor(Color.BLACK)
            //    holder.itemView.leaveAuthLayout.setBackgroundColor(Color.parseColor("#FCFCFC"))
            }
            else -> {
                setTextToUI(holder.binding.txtApplicationType, "Other")
                holder.binding.txtApplicationType.setBackgroundResource(R.drawable.red_curved)
                holder.binding.txtApplicationType.setTextColor(Color.WHITE)
         //       holder.itemView.leaveAuthLayout.setBackgroundColor(Color.parseColor("#C6C8D7"))

            }
        }

        //purpose
        setTextToUI(
            holder.binding.txtLeavePurpose,
            "Purpose: " + applicationInfo.lvPurpose.toString()
        )
        //Address
        setTextToUI(holder.binding.txtLeaveAddress, applicationInfo.lvAdd.toString())
        //Mobile
        setTextToUI(holder.binding.txtLeaveMobile, applicationInfo.lvPhone.toString())
        // Set Image


        Glide
            .with(mContext)
            .load(applicationInfo.lvEmpPic.toString())
            // .error("https://www.svgrepo.com/show/13656/user.svg")
            .error(R.drawable.default_user_image)
            // .centerCrop()
            .fitCenter()
            .placeholder(R.drawable.default_user_image)
            //.transform(RoundedCorners(30, 10))
            .into(holder.binding.imgApplicantImage)
        // val lateTime: String = Utility.changeDateFormat(late.lt_time, "yyyy-MM-dd HH:mm:ss", "hh:mm:ss a").toString()

    }

    private fun setTextToUI(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return applicationList.size
    }


    class ViewHolder(val binding: LeaveApprovalListItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        var isOpen: Boolean = false
        private var isSidePanelShown: Boolean = false

        init {
            // Set click listener for txtDetails to toggle visibility of layoutExtend with animation
            binding.txtDetails.setOnClickListener {
                val transition: Transition = Slide(Gravity.TOP).apply {
                    duration = 600
                    addTarget(binding.layoutExtend)
                }

                TransitionManager.beginDelayedTransition(binding.hideChild, transition)
                binding.layoutExtend.visibility = if (!isSidePanelShown) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                binding.layoutExtend.bringToFront()
                isSidePanelShown = !isSidePanelShown
            }

            // Set click listener for imgCall
            binding.imgCall.setOnClickListener {
                listener.OnClickCall(binding.root, adapterPosition)
            }

            // Set click listener for imgCancel
            binding.imgCancel.setOnClickListener {
                listener.OnReject(binding.root, adapterPosition)
            }

            // Set click listener for imgApprove
            binding.imgApprove.setOnClickListener {
                listener.OnAccept(binding.root, adapterPosition)
            }
        }
    }



}