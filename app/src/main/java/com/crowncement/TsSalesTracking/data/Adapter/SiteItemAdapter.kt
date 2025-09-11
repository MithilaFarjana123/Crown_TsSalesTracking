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
import com.crowncement.TsSalesTracking.data.Model.GetTSSiteInfoChild
import com.crowncement.TsSalesTracking.databinding.DemoSiteItemBinding


class SiteItemAdapter (private val siteList: List<GetTSSiteInfoChild>, private val type: String) :
    RecyclerView.Adapter<SiteItemAdapter.ViewHolder>()  {

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context

    var listSize: ArrayList<GetTSSiteInfoChild> = java.util.ArrayList()

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


    fun extractTextInParentheses(input: String): String? {
        val regex = "\\(([^)]*)\\)".toRegex()
        return regex.find(input)?.groupValues?.get(1)?.trim()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mContext = holder.itemView.context
        val SiteInfo = siteList[position]


        if (SiteInfo.partyName.toString().isNotEmpty()){

            val input = SiteInfo.partyName.toString()
            val result = extractTextInParentheses(input)
            holder.binding.partyName.visibility = View.VISIBLE
            holder.binding.partyName.text ="IHBs name : "+ result
        }else {
            holder.binding.partyName.visibility = View.GONE
        }


        holder.binding.SiteName.text = "Site Number : "+SiteInfo.siteNumber.toString()

        if(SiteInfo.partyMobile.toString().isNotEmpty()){
            holder.binding.SiteNumber.visibility = View.VISIBLE
            holder.binding.SiteNumber.text = "IHBs Mobile Number : "+SiteInfo.partyMobile.toString()
        }else{
            holder.binding.SiteNumber.visibility = View.GONE
        }

        if (SiteInfo.partyName.toString().isNotEmpty() && type == "IHBs"){
            val input = SiteInfo.partyName.toString()
            val result = extractTextInParentheses(input)
            holder.binding.partyName.visibility = View.VISIBLE
            holder.binding.partyName.text ="IHBs name : "+ result
        }
//        else if (SiteInfo.partyName.toString().isNotEmpty() && type != "IHBs"){
//            holder.binding.partyName.visibility = View.VISIBLE
//            holder.binding.partyName.text ="Party name : "+ SiteInfo.partyName.toString()
//        }
        else if (SiteInfo.partyName.toString().isNotEmpty() ){
            val input = SiteInfo.partyName.toString()
            val result = extractTextInParentheses(input)
            holder.binding.partyName.visibility = View.VISIBLE
            holder.binding.partyName.text ="IHBs name : "+ result
        }


        else {
            holder.binding.partyName.visibility = View.GONE

        }

        if(SiteInfo.siteAdd.toString().isNotEmpty()){
            holder.binding.SiteAddress.visibility = View.VISIBLE
            holder.binding.SiteAddress.text =" Site Address : "+ SiteInfo.siteAdd.toString()
        }else{
            holder.binding.SiteAddress.visibility = View.GONE
        }

        if((SiteInfo.conversionStatus=="Yes")||
            (SiteInfo.conversionStatus=="Already converted site")){
            holder.binding.ConversionIcon.visibility = View.VISIBLE
         //   holder.binding.ConversionText.setText("Converted")


        }else{
            holder.binding.ConversionIcon.visibility = View.GONE
        }

        if(SiteInfo.conversionStatus!!.isNotEmpty()){

            holder.binding.ConversionStatus.text =
                "Status : " + SiteInfo.conversionStatus.toString()
        }


        if (SiteInfo.siteImage != "") {
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

    class ViewHolder(val binding: DemoSiteItemBinding, listener: OnAdapterItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        private val textView: TextView = itemView.findViewById(R.id.SiteName)


        init {
            binding.root.setOnClickListener {
                listener.OnClick(binding.root, adapterPosition)
            }
        }
    }

}