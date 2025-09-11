package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.data.Model.CarReqEntListResponceChild
import com.crowncement.TsSalesTracking.databinding.CarListItemBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CarBookingListAdapter(val carBookList : List<CarReqEntListResponceChild>) : RecyclerView.Adapter<MycarViewHolder>() {
    lateinit var mContext: Context


//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MycarViewHolder {
//        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
//        val view = inflater.inflate(R.layout.car_list_item, parent, false)
//        return MycarViewHolder(view)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MycarViewHolder {
        val binding = CarListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MycarViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return carBookList.size
    }

    override fun onBindViewHolder(holder: MycarViewHolder, position: Int) {
        mContext = holder.itemView.context
        var carBookListposition = carBookList[position]
        val chip = Chip(holder.itemView.context)


        holder.carbook_fdatetime.text = "From date : "+carBookList [position].fromDate+" | " + carBookList [position].startTime
        holder.carbook_purpose.text = carBookList[position].purpose
        holder.carbook_numOfUser.text = "Number of user : "+carBookList[position].noPerson
        holder.carbook_driverInfo.text = carBookList[position].driName+" (num : "+ carBookList[position].driMobile+" )"
        holder.carBook_status.text ="Status : "+ carBookList[position].reqStatus

        holder.carbook_tdatetime.text = "To date : "+carBookList [position].toDate+" | " + carBookList [position].endTime
        setChipsForAuthority(carBookList[position].appBy.toString(),holder.car_chipGroup)

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


}


//class MycarViewHolder(val binding: CarListItemBinding: View) : RecyclerView.ViewHolder(itemView){
//
//    var carbook_fdatetime=itemView.findViewById<TextView>(R.id.carbook_fdatetime)
//    var carbook_purpose=itemView.findViewById<TextView>(R.id.carbook_purpose)
//
//    var carbook_numOfUser=itemView.findViewById<TextView>(R.id.carbook_numOfUser)
//
//    var carbook_driverInfo=itemView.findViewById<TextView>(R.id.carbook_driverInfo)
//
//    var carbook_tdatetime = itemView.findViewById<TextView>(R.id.carbook_tdatetime)
//    var carBook_status = itemView.findViewById<TextView>(R.id.carBook_status)
//
//    var car_chipGroup = itemView.findViewById<ChipGroup>(R.id.car_chipGroup)
//
//}


class MycarViewHolder(val binding: CarListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    val carbook_fdatetime = binding.carbookFdatetime
    val carbook_purpose = binding.carbookPurpose
    val carbook_numOfUser = binding.carbookNumOfUser
    val carbook_driverInfo = binding.carbookDriverInfo
    val carbook_tdatetime = binding.carbookTdatetime
    val carBook_status = binding.carBookStatus
    val car_chipGroup = binding.carChipGroup
}
