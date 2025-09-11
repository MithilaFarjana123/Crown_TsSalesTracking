package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.data.API.EndPoint
import com.crowncement.TsSalesTracking.data.Model.GetContractorMasonResponceChild
import com.crowncement.TsSalesTracking.databinding.MasonListitemBinding

class MasonItemAdapter(private val masonList: List<GetContractorMasonResponceChild>) :
    RecyclerView.Adapter<MasonItemAdapter.ViewHolder>() {

    lateinit var mContext: Context

    var listSize: ArrayList<GetContractorMasonResponceChild> = java.util.ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasonItemAdapter.ViewHolder {
        val binding = MasonListitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mContext = holder.itemView.context
        val masonInfo = masonList[position]
        holder.binding.masonName.text = masonInfo.partyName.toString()

        holder.binding.masonnum.text = masonInfo.partyMobile.toString()

        holder.binding.masonAdd.text = masonInfo.geoHrc3.toString()+","+
        masonInfo.geoHrc2.toString()+","+masonInfo.geoHrc1.toString()

        if (masonInfo.partyImage!="") {
            Glide
                .with(mContext)
                .load(EndPoint.IMAGE_BASE_URL + masonInfo.partyImage)
                .error(R.drawable.login_cover)
                // .centerCrop()
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.login_cover)
                .into(holder.binding.masonImg)
        }





    }

    override fun getItemCount(): Int {
        return masonList.size
    }


    class ViewHolder(val binding: MasonListitemBinding) : RecyclerView.ViewHolder(binding.root) {


    }

}