package com.crowncement.TsSalesTracking.data.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crowncement.TsSalesTracking.data.Model.MovementPlanChild2
import com.crowncement.TsSalesTracking.databinding.MovementPlanListItemBinding


class MovementListItemAdapter(private val planList: ArrayList<MovementPlanChild2>) :

    RecyclerView.Adapter<MovementListItemAdapter.ViewHolder>() {

    var listSize: ArrayList<MovementPlanChild2> = ArrayList()

    private val TAG = "MovementListItemAdapter"

    private lateinit var onClickListener: OnAdapterItemClickListener
    lateinit var mContext: Context

    interface OnAdapterItemClickListener {
        fun OnClickApply(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnAdapterItemClickListener) {
        onClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = MovementPlanListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mContext = holder.itemView.context

        val plan = planList[position]

        holder.binding.txtRouteNa.text =plan.routeName
        holder.binding.txtRouteDetails.text = plan.rtHrc5+" > "+plan.rtLong


        holder.binding.txtMovementNumber.text = "${position+1}"

    }

    override fun getItemCount(): Int {
        return planList.size
    }

    class ViewHolder(val binding: MovementPlanListItemBinding, listener: OnAdapterItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {
                listener.OnClickApply(binding.root, adapterPosition)
            }
        }
    }
}