package com.crowncement.TsSalesTracking.data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.data.Model.GetSalesCallAppResponceChild
import com.crowncement.TsSalesTracking.databinding.CallRecordApprovaLitemBinding

class CallRecordApproveListAdapter (private val attList: ArrayList<GetSalesCallAppResponceChild>) :
    RecyclerView.Adapter<CallRecordApproveListAdapter.ViewHolder>(){

    private lateinit var onClickListener:OnAdapterItemClickListener

    var listSize: ArrayList<GetSalesCallAppResponceChild> = ArrayList()
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {
        fun OnAccept(v: View?, position: Int)
        fun OnAbsent(v: View?, position: Int)

    }
    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CallRecordApprovaLitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding,onClickListener)

    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mContext = holder.itemView.context
        val attInfo = attList[position]

        holder.binding.txtAttApprove.visibility = View.VISIBLE

        holder.binding.CallNum.text = attInfo.callTrno.toString()
        var deldate = Utility.changeDateFormat(
            attInfo.delDate.toString(),
            "yyyy-MM-dd",
            "MMM, dd yyyy"
        )
        holder.binding.deliverydate.text ="Delivery date : "+ deldate

        holder.binding.siteId.text ="Site Num : "+ attInfo.projCode.toString()
        holder.binding.partyName.text = attInfo.partyName.toString()

        holder.binding.deliveryQty.text = attInfo.delQty.toString()
        holder.binding.partyCode.text ="IHBs Code : "+ attInfo.partyCode.toString()

        holder.binding.convStatus.text =attInfo.convStatus.toString()
        holder.binding.partyadd.text = attInfo.partyAdd.toString()
        holder.binding.submittedby.text = attInfo.entUser.toString()

        holder.binding.siteAdd.text = attInfo.projAdd.toString()
        var convdate = Utility.changeDateFormat(
            attInfo.firstDelDate.toString(),
            "yyyy-MM-dd",
            "MMM, dd yyyy"
        )

        holder.binding.convDate.text = convdate

        if(attInfo.appStatus.toString()==""){
            holder.binding.approvalStatus.visibility = View.GONE
            holder.binding.txtAttApprove.visibility = View.VISIBLE
            holder.binding.txtAttReject.visibility = View.VISIBLE

        }else{

            holder.binding.appstat.text = attInfo.appStatus.toString()
            holder.binding.approvalStatus.visibility = View.VISIBLE
            holder.binding.txtAttApprove.visibility = View.GONE
            holder.binding.txtAttReject.visibility = View.GONE

        }

        holder.binding.ihbsNum.text = attInfo.partyMob.toString()
        holder.binding.divdisThana.text = attInfo.partyHrc2.toString() +" > "+
                attInfo.partyHrc3.toString() +" > "+
                attInfo.partyHrc4.toString()





    }


    override fun getItemCount(): Int {

        return attList.size

    }


    class ViewHolder(val binding: CallRecordApprovaLitemBinding,listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root)  {
        init {


            binding.txtAttApprove.setOnClickListener {
                listener.OnAccept(itemView, adapterPosition)
            }
            //todo for absent
            binding.txtAttReject.setOnClickListener {
                listener.OnAbsent(itemView, adapterPosition)
            }
        }
    }


}

