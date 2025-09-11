package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.data.Model.MeetRoomBKListChild
import com.crowncement.TsSalesTracking.databinding.MeetingListItemBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MeetingRoomBKLogAdapter (val BkList : List<MeetRoomBKListChild>) : RecyclerView.Adapter<MyViewHolder>() {

    lateinit var mContext: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MeetingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        mContext = holder.itemView.context
        var BKposition = BkList[position]
        val chip = Chip(holder.itemView.context)

        holder.bk_roomtype.text = BkList[position].roomName
        holder.bk_bookingdate.text = BkList[position].bookDate
        holder.bk_startTime.text = BkList[position].startTime
        holder.bk_endTime.text = BkList[position].endTime
        holder.bk_purpose.text = BkList[position].purpose
        holder.bk_status.text = "Purpose : "+ BkList[position].bkStatus

        if(BkList[position].bkStatus.equals("Approved")){


        }else if(BkList[position].bkStatus.equals("pending")){

        }

        setChipsForAuthority(BkList[position].appBy.toString(),holder.bk_chipGroup)


    }

    private fun setChipsForAuthority(authName: String, chi: ChipGroup) {

        val splitValue = authName.split(">")


        for (item in splitValue) {
            val chip = Chip(mContext)
            // val regex = Regex("[^A-Za-z0-9]")
            //  val result = regex.replace(item, "")
            val result = item.replace("-", "")
            // chip.text = item+result
            chip.text = result
            // Set the chip icon
            chip.chipIcon =
                ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_account)
            chip.setChipIconTintResource(R.color.crown_icon_color)

            chi.addView(chip)
        }
    }


    override fun getItemCount(): Int {
        return BkList.size
    }

}


class MyViewHolder(val binding: MeetingListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    // Directly access views through the binding object
    var bk_roomtype = binding.bkRoomtype
    var bk_bookingdate = binding.bkBookingdate
    var bk_startTime = binding.bkStartTime
    var bk_endTime = binding.bkEndTime
    var bk_purpose = binding.bkPurpose
    var bk_status = binding.bkStatus
    var bk_chipGroup = binding.bkChipGroup

    init {
        // Example click listener setup, if needed
        binding.root.setOnClickListener {
            // Handle click event
        }
    }
}
