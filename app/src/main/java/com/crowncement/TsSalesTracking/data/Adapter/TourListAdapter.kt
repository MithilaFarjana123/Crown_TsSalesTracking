package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.TourResponseChild
import com.crowncement.TsSalesTracking.databinding.TourListItemBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class TourListAdapter(private val tourList: ArrayList<TourResponseChild>) :

    RecyclerView.Adapter<TourListAdapter.ViewHolder>() {

    var listSize: ArrayList<TourResponseChild> = java.util.ArrayList()

    private val TAG = "TourListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {

        fun onStartTour(v: View?, position: Int)
        fun OnStopTour(v: View?, position: Int)
        fun OnExpenseClaim(v: View?, position: Int)
        fun OnFinishExpense(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use ViewBinding to inflate the layout
        val binding = TourListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

//        if(position%2==1){
//            holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE"))
//        }

        val tourInfo = tourList[position]

        holder.binding.txtTourNo.text = tourInfo.trTrno

        val applyDate: String = Utility.changeDateFormat(tourInfo.trApDt, "yyyy-MM-dd", "EEE, dd MMM yy").toString()
        val fromDate: String = Utility.changeDateFormat(tourInfo.trFdate, "yyyy-MM-dd", "EEE, dd MMM yy").toString()
        val toDate: String = Utility.changeDateFormat(tourInfo.trTdate, "yyyy-MM-dd", "EEE, dd MMM yy").toString()





        holder.binding.txtTourAppliedOn.text = "Applied On: $applyDate"

        holder.binding.txtTourPurpose.text = "Purpose:  " + tourInfo.trPurpose



        holder.binding.txtTourFrom.text ="$fromDate - $toDate"



        //  holder.itemView.txtTotalTourDays.text="Tour Day: "+tourInfo.trDay.toString()
        var countDays = 0
        countDays = Utility.getDayCountBetweenDates(
            tourInfo.trFdate.toString(),
            tourInfo.trTdate.toString()
        )
        countDays += 1
        if (countDays > 1) holder.binding.txtTotalTourDays.text = "Duration: $countDays Days"
        else holder.binding.txtTotalTourDays.text = "Duration: $countDays Day"

        holder.binding.txtAppliedTourStatus.text =
            "Application Status: " + tourInfo.trStat!!.split("-")[1]

        if (tourInfo.trStat!!.split("-")[1].equals("Sanctioned")) {
            holder.binding.imgTrStatus.setImageResource(R.drawable.ic_checked_green)
            holder.binding.layoutTrPending.visibility = View.GONE
        } else {
            holder.binding.imgTrStatus.setImageResource(R.drawable.ic_baseline_error_orange)
            holder.binding.layoutTrPending.visibility = View.VISIBLE
            holder.binding.txtTrPendingTo.text = tourInfo.trPendingTo
        }

        holder.binding.txtTrApprove.text = tourInfo.trHrcpath.toString()


        //TODO bottom status--------------------------------------------------------------

        //Start
        when (tourInfo.tour_exec_st) {
            "1-Upcoming" -> {

                holder.binding.txtStart.visibility = View.VISIBLE

                holder.binding.txtCompleted.visibility = View.GONE
                holder.binding.txtStop.visibility      = View.GONE
                holder.binding.txtExpense.visibility   = View.GONE
                holder.binding.txtRunning.visibility   = View.GONE
                holder.binding.imgRunning.visibility   = View.GONE
                holder.binding.txtTourComplete.visibility   = View.GONE
                holder.binding.txtFinishExpense.visibility   = View.GONE

            }
            //Running
            "2-Running" -> {

                holder.binding.txtStart.visibility = View.GONE
                holder.binding.txtCompleted.visibility = View.GONE
                holder.binding.txtTourComplete.visibility = View.GONE
                holder.binding.txtFinishExpense.visibility = View.GONE

                holder.binding.txtStop.visibility      = View.VISIBLE
                holder.binding.txtExpense.visibility   = View.VISIBLE
                holder.binding.txtRunning.visibility   = View.VISIBLE
                holder.binding.imgRunning.visibility   = View.VISIBLE

            }
            //Running
            "3-Trip_Over" -> {

                holder.binding.txtStart.visibility = View.GONE
                holder.binding.txtCompleted.visibility = View.GONE
                holder.binding.txtStop.visibility      = View.GONE
                holder.binding.txtRunning.visibility   = View.GONE
                holder.binding.imgRunning.visibility   = View.GONE

                holder.binding.txtExpense.visibility   = View.VISIBLE
                holder.binding.txtFinishExpense.visibility   = View.VISIBLE
                holder.binding.txtTourComplete.visibility   = View.VISIBLE

            }


            //Complete
            else -> {

                holder.binding.txtCompleted.visibility = View.VISIBLE

                holder.binding.txtExpense.visibility   = View.GONE
                holder.binding.txtStart.visibility     = View.GONE
                holder.binding.txtStop.visibility      = View.GONE
                holder.binding.txtRunning.visibility   = View.GONE
                holder.binding.imgRunning.visibility   = View.GONE
                holder.binding.txtTourComplete.visibility   = View.GONE
                holder.binding.txtFinishExpense.visibility   = View.GONE
            }
        }

        // setChipsForAuthority(tourInfo.trHrcpath.toString(),  holder.itemView.adminChipGroup)



        if(tourInfo.trStat=="D-Denied"){
            holder.binding.txtStart.visibility=View.GONE
            holder.binding.layStatys.setBackgroundColor(Color.parseColor("#B10C0C"))
            holder.binding.txtTotalTourDays.setTextColor(Color.WHITE)
            holder.binding.txtAppliedTourStatus.setTextColor(Color.WHITE)

        }

    }

    private fun setChipsForAuthority(authName: String, chi: ChipGroup) {

        val splitValue = authName.split(">")
        for (item in splitValue) {
            val chip = Chip(mContext)
            val result = item.replace("-", "")
            chip.text = result
            chip.chipIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_account)
            chip.setChipIconTintResource(R.color.crown_icon_color)
            chi.addView(chip)
        }
    }

    override fun getItemCount(): Int {
        return tourList.size
    }

    class ViewHolder(val binding: TourListItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.txtStart.setOnClickListener {
                listener.onStartTour(itemView, adapterPosition)
            }
            binding.txtStop.setOnClickListener {
                listener.OnStopTour(itemView, adapterPosition)
            }
            binding.txtExpense.setOnClickListener {
                listener.OnExpenseClaim(itemView, adapterPosition)
            }
            binding.txtFinishExpense.setOnClickListener {
                listener.OnFinishExpense(itemView, adapterPosition)
            }



        }
    }
}