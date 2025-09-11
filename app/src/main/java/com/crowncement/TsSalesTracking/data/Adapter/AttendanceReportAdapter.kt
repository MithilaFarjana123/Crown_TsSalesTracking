package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.AttendanceReportChild
import com.crowncement.TsSalesTracking.databinding.AttItemBinding

class AttendanceReportAdapter ( val attendenceList: ArrayList<AttendanceReportChild>) :
    RecyclerView.Adapter<AttendanceReportAdapter.ViewHolder>() {

    var listSize: ArrayList<AttendanceReportChild> = java.util.ArrayList()

  //  private val TAG = "AttendanceReportAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnSelectSubMenu(v: android.view.View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

      //  val view = LayoutInflater.from(parent.context)
       //     .inflate(R.layout.att_item, parent, false)

        val binding = AttItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val attData = attendenceList[position]


        val bookinDate: String =
            Utility.changeDateFormat(attData.att_date, "yyyy-MM-dd", "dd").toString()

        val bookinMonth: String =
            Utility.changeDateFormat(attData.att_date, "yyyy-MM-dd", "MMM-yyyy").toString()

        val bookinDay: String =
            Utility.changeDateFormat(attData.att_date, "yyyy-MM-dd", "EEE").toString()

        if (bookinDay == "Fri") {
            holder.binding.txtStatus.setTextColor(Color.parseColor("#F44336"))
        }

        holder.binding.txtDate.text = bookinDate
        holder.binding.txtMonthYear.text = bookinMonth
        holder.binding.txtDay.text = bookinDay

        holder.binding.txtStatus.text = attData.att_stat

        val checkIn =
            //Utility.changeDateFormat(attData.att_in, "yyyy-MM-dd HH:mm:ss", "hh:mm:ss a").toString()
            Utility.changeDateFormat(attData.att_in, "yyyy-MM-dd HH:mm:ss", "hh:mm a").toString()

        holder.binding.txtInTime.text = checkIn
        val lateParameter = checkIn.split(":")

//        if ((lateParameter[0] + "." + lateParameter[1]).toFloat() > 09.15 || checkIn.contains("PM")) {
//            holder.itemView.txtInTime.setTextColor(Color.parseColor("#DD2C1F"))
//            holder.itemView.txtStatus.text = "Late"
//            holder.itemView.txtStatus.setTextColor(Color.parseColor("#DD2C1F"))
//            Log.d(TAG, "Late: "+(lateParameter[0] + "." + lateParameter[1]))
//        }
//        else{
//            holder.itemView.txtInTime.setTextColor(Color.parseColor("#929292"))
//            holder.itemView.txtStatus.setTextColor(Color.parseColor("#929292"))
//            holder.itemView.txtOutTime.setTextColor(Color.parseColor("#929292"))
//        }


        when (attData.att_stat) {
            "Pending" -> {
                holder.binding.txtStatus.setTextColor(Color.parseColor("#FC9E14"))
            }
            "Absent" -> {
                holder.binding.txtStatus.setTextColor(Color.parseColor("#DD2C1F"))
            }
            "Late" -> {
                holder.binding.txtStatus.setTextColor(Color.parseColor("#DD2C1F"))
            }
            "Holiday"->{
                holder.binding.txtStatus.setTextColor(Color.parseColor("#00914C"))
            }
            "Not-Punched"->{
                holder.binding.txtStatus.setTextColor(Color.parseColor("#EF1A0A"))
            }
            "On Leave"->{
                holder.binding.txtStatus.setTextColor(Color.parseColor("#FC9E14"))
            }

            else -> {
                holder.binding.txtStatus.setTextColor(Color.parseColor("#929292"))
            }
        }





        if (!(attData.att_out!!.contains("1900"))) {
            holder.binding.txtOutTime.text =
              //  Utility.changeDateFormat(attData.att_out, "yyyy-MM-dd HH:mm:ss", "hh:mm:ss a").toString()
                Utility.changeDateFormat(attData.att_out, "yyyy-MM-dd HH:mm:ss", "hh:mm a").toString()
        } else {
            holder.binding.txtOutTime.text = "0:0"
        }
    }

    override fun getItemCount(): Int {
        return attendenceList.size
    }


    class ViewHolder(val binding: AttItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.OnSelectSubMenu(binding.root, adapterPosition)
            }
        }
    }

}