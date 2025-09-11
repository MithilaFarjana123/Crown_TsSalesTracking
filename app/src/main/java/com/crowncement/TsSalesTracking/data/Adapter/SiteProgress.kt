package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.PickDocWPartResponseChild
import com.crowncement.TsSalesTracking.databinding.SiteImgBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SiteProgress (private val siteImgList: List<PickDocWPartResponseChild>) :
    RecyclerView.Adapter<SiteProgress.ViewHolder>()  {

    lateinit var mContext: Context





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SiteImgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return  siteImgList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mContext = holder.itemView.context
        val SiteInfo = siteImgList[position]

      //  var date = main(siteImgList[position].filePath.toString()).toString()
     //   val linkdate = extractDate(siteImgList[position].filePath.toString())

        holder.binding.date.text = "  Site Progress on : "+  extractAndFormat(SiteInfo.filePath.toString())

                /*Utility.changeDateFormat(
            extractDate(SiteInfo.filePath.toString()),
            "YYYYMMDD",
            "dd MMM yyyy")

                 */

        if (SiteInfo.filePath != "") {
            Glide
                .with(mContext)
                .load(EndPoint.IMAGE_BASE_URL + SiteInfo.filePath)
                .error(R.drawable.login_cover)
                // .centerCrop()
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.login_cover)
                .into(holder.binding.ConImg)
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun formatToReadable(dateStr: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val date = LocalDate.parse(dateStr, inputFormatter)
        return date.format(outputFormatter)
    }

    // Combined usage:
    @RequiresApi(Build.VERSION_CODES.O)
    fun extractAndFormat(path: String): String? {
        val raw = extractDate(path) ?: return null
        return formatToReadable(raw)
    }


    fun extractDate(path: String): String? {
        // Regex matches exactly eight digits before ".jpg"
        val regex = Regex("""\d{8}(?=\.jpg$)""")
        return regex.find(path)?.value
    }





    class ViewHolder(val binding: SiteImgBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}