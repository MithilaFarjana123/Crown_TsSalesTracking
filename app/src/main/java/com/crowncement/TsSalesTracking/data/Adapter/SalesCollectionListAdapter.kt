package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.crowncement.TsSalesTracking.R
import com.crowncement.TsSalesTracking.common.Utility
import com.crowncement.TsSalesTracking.common.Utility.changeDateFormat
import com.crowncement.TsSalesTracking.data.Model.SalesCollectionChild
import com.crowncement.TsSalesTracking.databinding.SalesCollectionChildBinding


class SalesCollectionListAdapter(private val collectionList: ArrayList<SalesCollectionChild>) :

    RecyclerView.Adapter<SalesCollectionListAdapter.ViewHolder>() {

    var listSize: ArrayList<SalesCollectionChild> = java.util.ArrayList()

    private val TAG = "SalesCollectionListAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context


    interface OnAdapterItemClickListener {

        fun clickShowDoc(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use ViewBinding to inflate the layout
        val binding = SalesCollectionChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val value = collectionList[position]

        val payDate: String = changeDateFormat(value.depoDate, "yyyy-MM-dd", "EEE, MMM dd yyyy").toString()
        setFieldValue(holder.binding.txtPayDate, "Payment Date: $payDate")

        if (value.dep_status.equals("Pending")) {
            holder.binding.imgPayStatus.setBackgroundResource(R.drawable.ic_baseline_error_orange)
        } else {
            holder.binding.imgPayStatus.setBackgroundResource(R.drawable.ic_checked_green)
        }

        holder.binding.txtPBank.text=value.bankCode

        setBankImage(value.bankCode.toString(), holder.binding.imgBank)
        setFieldValue(holder.binding.txtBranch,value.brCode.toString())
        setFieldValue(holder.binding.txtPayTo,value.accName.toString())
        setFieldValue(holder.binding.txtPayType,value.payType.toString())
        setFieldValue(holder.binding.txtAccNo,value.accNo.toString())
        setFieldValue(holder.binding.txtRouNo,value.routingNo.toString())
        setFieldValue(holder.binding.txtPayAmount, Utility.formatAmount(value.depoAmt.toString().toDouble())!!+"/-")
    }

    private fun setBankImage(bankName:String,imageView: ImageView){

        val bankImgUrl=Utility.getBankImage(bankName)

        if(bankImgUrl.isNotEmpty())
        {

            Glide
                .with(mContext)
                .load(bankImgUrl)
                .error(R.drawable.crown_cement_icon_gray)
              //  .fitCenter()
                .centerInside()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.crown_cement_icon_gray)
                .into(imageView)
        }
    }

    private fun setFieldValue(v: TextView, value: String) {
        if (value.isNotEmpty()) {
            v.text = value
        } else {
            v.text = ""
        }
    }

    override fun getItemCount(): Int {
        return collectionList.size
    }

    class ViewHolder(val binding: SalesCollectionChildBinding, listener: OnAdapterItemClickListener) : RecyclerView.ViewHolder(binding.root) {

        var isSidePanelShown: Boolean = false

        init {

            binding.txtDisplay.setOnClickListener {

                val transition: Transition = Slide(Gravity.TOP)
                transition.duration = 600
                transition.addTarget(binding.layoutPayMore)

                TransitionManager.beginDelayedTransition(
                    binding.paychildContainer, transition
                )
                binding.layoutPayMore.visibility =
                    if (!isSidePanelShown) View.VISIBLE else View.GONE
                isSidePanelShown = !isSidePanelShown

            }

            binding.root.setOnClickListener {
                listener.clickShowDoc(binding.root, adapterPosition)
            }
        }
    }

}