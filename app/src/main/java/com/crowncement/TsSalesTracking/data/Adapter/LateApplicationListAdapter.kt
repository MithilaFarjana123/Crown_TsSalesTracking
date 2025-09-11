package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility

import com.crowncement.TsSalesTracking.data.Model.Late
import com.crowncement.TsSalesTracking.databinding.LateApplicationItemBinding

class LateApplicationListAdapter(private val lateList: ArrayList<Late>) :

    RecyclerView.Adapter<LateApplicationListAdapter.ViewHolder>() {

    var listSize: ArrayList<Late> = java.util.ArrayList()

    private val TAG = "LateApplicationListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnClickApply(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = LateApplicationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

//        if(position%2==1){
//            holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE"))
//        }

        val late = lateList[position]
        val lateDate: String =
            Utility.changeDateFormat(late.lt_date, "yyyy-MM-dd", "EEE, dd MMM yy").toString()
        holder.binding.txtLateDate.text = "Late on $lateDate"
        holder.binding.txtShiftCode.text = late.att_shift
        val lateTime: String =
            Utility.changeDateFormat(late.lt_time, "yyyy-MM-dd HH:mm:ss", "hh:mm:ss a").toString()
        holder.binding.txtIn.text = lateTime
        holder.binding.txtOperation.text = late.lt_ent_stat
        if(late.lt_ent_stat=="Open"){

            holder.binding.txtOperation.text = "Apply Late"
        }
        holder.binding.txtLateAuth.text = late.lt_hrcpath

        if (late.lt_ent_stat.equals("Applied")) {
      //  if (late.lt_ent_stat.equals(mContext.getString(R.string.applied))) {
            // holder.itemView.txtOperation.isEnabled=false
             holder.binding.txtOperation.text="Show"
            // holder.itemView.txtOperation.setBackgroundColor(Color.parseColor("#CCCCCC"))
            holder.binding.txtOperation.setBackgroundResource(R.drawable.gray_curved)
        } else {
         //   holder.itemView.txtOperation.setBackgroundColor(Color.parseColor("#F8D786"))
            holder.binding.txtOperation.setBackgroundResource(R.drawable.curved_orange)
        }
        // holder.itemView.txtOperation.text =late.operation

    }

    override fun getItemCount(): Int {
        return lateList.size
    }

    class ViewHolder(val binding: LateApplicationItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Use the binding object to set click listeners
            binding.txtOperation.setOnClickListener {

                listener.OnClickApply(binding.root, adapterPosition)
            }
        }
    }

}