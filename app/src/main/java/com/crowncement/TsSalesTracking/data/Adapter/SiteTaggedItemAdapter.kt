package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.GetBPSiteInfoResponceChild
import com.crowncement.TsSalesTracking.databinding.DemoSiteItemBinding


class SiteTaggedItemAdapter(private val siteList: List<GetBPSiteInfoResponceChild>, private val type: String) :
    RecyclerView.Adapter<SiteTaggedItemAdapter.ViewHolder>() {

    private lateinit var onClickListener: SiteTaggedItemAdapter.OnAdapterItemClickListener
    lateinit var mContext: Context

    var listSize: ArrayList<GetBPSiteInfoResponceChild> = java.util.ArrayList()


    interface OnAdapterItemClickListener {
        fun OnClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use ViewBinding to inflate the layout
        val binding = DemoSiteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mContext = holder.itemView.context
        val SiteInfo = siteList[position]

        if (SiteInfo.partyName.toString().isNotEmpty() && type == "IHBs"){

            holder.binding.partyName.visibility = View.VISIBLE
            holder.binding.partyName.text ="IHBs name : "+ SiteInfo.partyName.toString()
        }
        else if (SiteInfo.ihbName.toString().isNotEmpty() && type != "IHBs"){
            holder.binding.partyName.visibility = View.VISIBLE
            holder.binding.partyName.text ="IHBs name : "+ SiteInfo.ihbName.toString()
        }

        else {
            holder.binding.partyName.visibility = View.GONE


        }



        holder.binding.SiteName.text = "Site Number : "+SiteInfo.siteNumber.toString()

        if(SiteInfo.partyMobile.toString().isNotEmpty() && type == "IHBs"){
            holder.binding.SiteNumber.visibility = View.VISIBLE
            holder.binding.SiteNumber.text = "IHBs Mobile Number : "+SiteInfo.partyMobile.toString()
        }else if(SiteInfo.ihbMobile.toString().isNotEmpty()&& type != "IHBs"){
            holder.binding.SiteNumber.visibility = View.VISIBLE
            holder.binding.SiteNumber.text = "IHBs Mobile Number : "+SiteInfo.ihbMobile.toString()
        }
        else{
            holder.binding.SiteNumber.visibility = View.GONE
        }

        if(SiteInfo.partyAdd1.toString().isNotEmpty()&& type == "IHBs"){
            holder.binding.partyAddress.visibility = View.VISIBLE
            holder.binding.partyAddress.text =" IHBs Address : "+ SiteInfo.partyAdd1.toString()
        }
        else if(SiteInfo.siteAdd.toString().isNotEmpty()&& type != "IHBs")  {
            holder.binding.partyAddress.text =" IHBs Address : "+ SiteInfo.ihbAdd.toString()
        }
        else{
            holder.binding.partyAddress.visibility = View.GONE
        }

        if(SiteInfo.siteAdd.toString().isNotEmpty()){
            holder.binding.SiteAddress.visibility = View.VISIBLE
            holder.binding.SiteAddress.text =" Site Address : "+ SiteInfo.siteAdd.toString()
        }
        else{
            holder.binding.SiteAddress.visibility = View.GONE
        }

        if((SiteInfo.conversionStatus=="Yes")||
            (SiteInfo.conversionStatus=="Already converted site")){
            holder.binding.ConversionIcon.visibility = View.VISIBLE
       //     holder.binding.ConversionText.setText("Converted")

        }else{
            holder.binding.ConversionIcon.visibility = View.GONE

        }


        holder.binding.ConversionStatus.visibility =  View.GONE

        if(SiteInfo.conversionStatus!!.isNotEmpty()){
            holder.binding.ConversionStatus.text = "Status : "+SiteInfo.conversionStatus.toString()
        }



        if (SiteInfo.siteImage!="") {
            Glide
                .with(mContext)
                .load(EndPoint.IMAGE_BASE_URL + SiteInfo.siteImage)
                .error(R.drawable.login_cover)
                // .centerCrop()
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.login_cover)
                .into(holder.binding.ConImg)
        }


        //holder.bindData(listSize[position])
    }

    override fun getItemCount(): Int {
        return siteList.size
    }

    class ViewHolder(val binding: DemoSiteItemBinding, listener: SiteTaggedItemAdapter.OnAdapterItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        private val textView: TextView = itemView.findViewById(R.id.SiteName)


        init {
            binding.root.setOnClickListener {
                listener.OnClick(itemView, adapterPosition)
            }
        }
    }

}