package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.API.EndPoint.IMAGE_BASE_URL
import com.crowncement.TsSalesTracking.data.Model.AttApproveResponseChild
import com.crowncement.TsSalesTracking.databinding.ItemAttendanceApproveBinding

class AttendanceApproveListAdapter(private val attList: ArrayList<AttApproveResponseChild>) :
    RecyclerView.Adapter<AttendanceApproveListAdapter.ViewHolder>() {

    var listSize: ArrayList<AttApproveResponseChild> = ArrayList()
    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnClickLocation(v: View?, position: Int)
        fun OnAccept(v: View?, position: Int)
        fun OnAbsent(v: View?, position: Int)
        fun onLocationTrack(v: View?, position: Int)
        fun onClickItem(v: View?, position: Int)
    }



    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttendanceApproveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mContext = holder.itemView.context
        val attInfo = attList[position]


        Glide
            .with(mContext)
            .load(IMAGE_BASE_URL + attInfo.empImg.toString())
            .error(R.drawable.default_user_image)
            .fitCenter()
            .placeholder(R.drawable.default_user_image)
            .into(holder.binding.imgAttEmpImg)

        holder.binding.swInOut?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
              //  storeKeyValue(requireActivity(), "language", "bn")
              //   setAppLanguage(requireActivity(), "bn")
                // Load employee image In
                Glide
                    .with(mContext)
                    .load(IMAGE_BASE_URL + attInfo.emp_img_out.toString())
                    .error(R.drawable.default_user_image)
                    .fitCenter()
                    .placeholder(R.drawable.default_user_image)
                    .into(holder.binding.imgAttEmpImg)

            } else {
                // Load employee image In
                Glide
                    .with(mContext)
                    .load(IMAGE_BASE_URL + attInfo.empImg.toString())
                    .error(R.drawable.default_user_image)
                    .fitCenter()
                    .placeholder(R.drawable.default_user_image)
                    .into(holder.binding.imgAttEmpImg)
             //   storeKeyValue(requireActivity(), "language", "en")
            //    setAppLanguage(requireActivity(), "en")
            }
        }



        // EmpName
        setTextToUI(holder.binding.txtAttEmpName, attInfo.empName.toString())
        // EmpID and Designation
        setTextToUI(
            holder.binding.txtAttEmpID,
            attInfo.empId.toString() + " | " + attInfo.empDesig.toString()
        )
        // Attendance Time
        val convo = Utility.changeDateFormat(attInfo.attIn.toString(), "yyyy-MM-dd HH:mm:ss", "hh:mm:ss a")
        setTextToUI(holder.binding.txtAttTime, "Check In: " + convo.toString())

        //Attendance checkout Time
        if(attInfo.attOut!!.isNotEmpty() && (attInfo.attOut!="1900-01-01 00:00:00")) {
            val conOut = Utility.changeDateFormat(
                attInfo.attOut.toString(),
                "yyyy-MM-dd HH:mm:ss",
                "hh:mm:ss a"
            )
            setTextToUI(holder.binding.txtOutTime, "Check Out: " + conOut.toString())
        }

        if (attInfo.attStat == "") {
            holder.binding.txtAttApprove.visibility = View.VISIBLE
            holder.binding.txtAttAbsent.visibility = View.VISIBLE
            holder.binding.attview.visibility = View.VISIBLE


        }
        else if(attInfo.attStat == "Absent"){
            holder.binding.txtAttAbsent.visibility = View.GONE
            holder.binding.txtAttApprove.visibility = View.GONE
            holder.binding.attview.visibility = View.GONE



        }

        else {
            holder.binding.txtAttApprove.visibility = View.GONE
            holder.binding.attview.visibility = View.GONE
        }


    }


    private fun setTextToUI(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return attList.size
    }

    class ViewHolder(val binding: ItemAttendanceApproveBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imgAttLocation.setOnClickListener {
                listener.OnClickLocation(itemView, adapterPosition)
            }

            binding.txtAttApprove.setOnClickListener {
                listener.OnAccept(itemView, adapterPosition)
            }
            //todo for absent
            binding.txtAttAbsent.setOnClickListener {
                listener.OnAbsent(itemView, adapterPosition)
            }


            binding.imgTracking.setOnClickListener {
                listener.onLocationTrack(itemView, adapterPosition)
            }

            itemView.setOnClickListener {
                listener.onClickItem(itemView, adapterPosition)
            }
        }
    }
}
